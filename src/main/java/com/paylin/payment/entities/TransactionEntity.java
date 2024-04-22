package com.paylin.payment.entities;

import com.paylin.payment.util.TransactionStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Transaction")
public class TransactionEntity {

    @Id
    private String transactionId;
    private String cardNumber;
    private String expiryDate;
    private String cvv;
    private Double amount;
    private String currency;
    private String merchantId;
    private TransactionStatus status;
}