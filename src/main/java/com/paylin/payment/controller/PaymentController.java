package com.paylin.payment.controller;

import com.paylin.payment.model.TransactionDto;
import com.paylin.payment.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.text.ParseException;


@RestController
@RequestMapping("/payment")
@Validated
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping(value = "/process", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> processPayment(@RequestBody @Valid TransactionDto transactionDto) throws ParseException {
        String response = paymentService.processPayment(transactionDto);

        return new ResponseEntity<>(response, null, HttpStatus.OK);
    }
}
