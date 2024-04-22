package com.paylin.payment.service;

import com.paylin.payment.model.TransactionDto;

import java.text.ParseException;

public interface PaymentService {

    String processPayment(TransactionDto transactionDto) throws ParseException;
}
