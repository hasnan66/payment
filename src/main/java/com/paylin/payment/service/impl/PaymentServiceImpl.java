package com.paylin.payment.service.impl;


import com.paylin.payment.entities.TransactionEntity;
import com.paylin.payment.model.TransactionDto;
import com.paylin.payment.repository.TransactionRepository;
import com.paylin.payment.service.PaymentService;
import com.paylin.payment.util.TransactionStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import javax.validation.ValidationException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private TransactionRepository transactionRepository;
    @Value("${payments.tps}")
    private int threadPoolSize;
    private ExecutorService executor;

    @PostConstruct
    public void initializeExecutor() {
        executor = Executors.newFixedThreadPool(threadPoolSize);
    }

    @Async
    @Override
    public String processPayment(TransactionDto transactionDto) throws ParseException {

        if (!isValidCardNumber(transactionDto.getCardNumber())) {
            throw new ValidationException("invalid card numer.");
        }

        if (isCardExpired(transactionDto.getExpiryDate())) {
            throw new ValidationException("Card has expired.");
        }

        String transactionId = generateTransactionId();
        TransactionEntity transaction = new TransactionEntity(transactionId, transactionDto.getCardNumber(), transactionDto.getExpiryDate(), transactionDto.getCvv(), transactionDto.getAmount(), transactionDto.getCurrency(), transactionDto.getMerchantId(), TransactionStatus.PENDING);
        transactionRepository.save(transaction);

        return processAsyncPayments(transactionId);
    }

    @Transactional
    public String processAsyncPayments(String transactionId) {
        Optional<TransactionEntity> transactionEntity = transactionRepository.findByTransactionId(transactionId);

        if (transactionEntity.isPresent()) {
            Future<String> future = executor.submit(() -> {

                TransactionEntity transaction = transactionEntity.get();
                // Simulate communication with acquirer
                boolean isApproved = simulateTransactionProcessing(transaction.getCardNumber());

                // Update transaction status
                if (isApproved) {
                    transaction.setStatus(TransactionStatus.APPROVED);
                } else {
                    transaction.setStatus(TransactionStatus.DENIED);
                }

                //update the transaction status
                transactionRepository.save(transaction);
                return "Transaction ID: " + transactionId + ", Status: " + transaction.getStatus();
            });

            try {
                return future.get(); // Wait for the task to complete and return the result
            } catch (InterruptedException | ExecutionException e) {
                return "Error processing payment: " + e.getMessage();
            }
        } else {
            return "Error processing payment, please try again.";
        }
    }


    //generate unique TransactionId
    private String generateTransactionId() {
        return "TXN" + System.currentTimeMillis();
    }

    // Luhn's algorithm for card number validation
    private boolean isValidCardNumber(String cardNumber) {
        int sum = 0;
        boolean alternate = false;

        // Start from the rightmost digit
        for (int i = cardNumber.length() - 1; i >= 0; i--) {
            int digit = Character.getNumericValue(cardNumber.charAt(i));

            if (alternate) {
                digit *= 2;
                if (digit > 9) {
                    digit = (digit % 10) + 1;
                }
            }

            sum += digit;
            alternate = !alternate;
        }

        return sum % 10 == 0;
    }

    private boolean isCardExpired(String expiryDate) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/yy");
        simpleDateFormat.setLenient(false);
        Date expiry = simpleDateFormat.parse(expiryDate);
        return expiry.before(new Date());
    }

    public boolean simulateTransactionProcessing(String cardNumber) {
        int lastDigit = Character.getNumericValue(cardNumber.charAt(cardNumber.length() - 1));

        // If the last digit is even, approve the transaction; otherwise, deny it
        return lastDigit % 2 == 0;
    }

    public void shutdown() {
        executor.shutdown();
    }
}
