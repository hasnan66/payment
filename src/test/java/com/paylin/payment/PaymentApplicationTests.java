package com.paylin.payment;

import com.paylin.payment.entities.TransactionEntity;
import com.paylin.payment.model.TransactionDto;
import com.paylin.payment.repository.TransactionRepository;
import com.paylin.payment.service.impl.PaymentServiceImpl;
import com.paylin.payment.util.TransactionStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

import javax.validation.ValidationException;
import java.text.ParseException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
class PaymentApplicationTests {

    @Mock
    private TransactionRepository transactionRepository;
    @InjectMocks
    private PaymentServiceImpl paymentService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(paymentService, "threadPoolSize", 5);
        paymentService.initializeExecutor();
    }

    @Test
    public void testProcessPayment_WithValidInput_ReturnsTransactionId() throws ParseException {
        TransactionDto transactionDto = new TransactionDto("374245455400126", "12/25", "123", 100.0, "USD", "merchantId");
        TransactionEntity transactionEntity = new TransactionEntity("123", "374245455400126", "12/25", "123", 100.0, "USD", "merchantId", TransactionStatus.PENDING);
        when(transactionRepository.save(any())).thenReturn(new TransactionEntity());
        when(transactionRepository.findByTransactionId(anyString())).thenReturn(Optional.of(transactionEntity));
        when(paymentService.processAsyncPayments("123")).thenReturn("Transaction ID: 123, Status: APPROVED");

        String result = paymentService.processPayment(transactionDto);
        assertNotNull(result);
    }

    @Test
    public void testProcessPayment_WithInvalidCardNumber_ThrowsValidationException() {
        TransactionDto transactionDto = new TransactionDto("123456789012345", "12/25", "123", 100.0, "USD", "merchantId");
        assertThrows(ValidationException.class, () -> paymentService.processPayment(transactionDto));
    }

    @Test
    public void testProcessPayment_WithExpiredCard_ThrowsValidationException() {
        TransactionDto transactionDto = new TransactionDto("1234567890123456", "01/20", "123", 100.0, "USD", "merchantId");
        assertThrows(ValidationException.class, () -> paymentService.processPayment(transactionDto));
    }

    @Test
    public void testProcessAsyncPayments_WithExistingTransaction_ReturnsStatus() {
        TransactionEntity transactionEntity = new TransactionEntity("123", "1234567890123456", "12/25", "123", 100.0, "USD", "merchantId", TransactionStatus.PENDING);
        when(transactionRepository.findByTransactionId("123")).thenReturn(Optional.of(transactionEntity));
        String future = paymentService.processAsyncPayments("123");
        assertNotNull(future);
        assertTrue(future.contains("APPROVED"));
    }

    @Test
    public void testProcessAsyncPayments_WithNonExistingTransaction_ReturnsErrorMessage() {
        when(transactionRepository.findByTransactionId("123")).thenReturn(Optional.empty());
        String result = paymentService.processAsyncPayments("123");
        assertEquals("Error processing payment, please try again.", result);
    }

}
