package com.paylin.payment.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDto {

    @NotNull
    @Pattern(regexp = "[0-9]{16}", message = "Invalid card number.")
    private String cardNumber;

    @NotNull
    @Pattern(regexp = "\\d{2}/\\d{2}", message = "Expiry date must be in the format MM/YY.")
    private String expiryDate;

    @NotNull
    @Pattern(regexp = "[0-9]{3}", message = "Invalid ccv.")
    private String cvv;

    @NotNull(message = "Please enter valid amount")
    @DecimalMin(value = "0", inclusive = true, message = "Amount must be greater than or equal to 0.")
    @DecimalMax(value = "1000000", inclusive = true, message = "Amount must be less than or equal to 1,000,000.")
    private Double amount;
    @NotNull(message = "currency is not valid")
    @Pattern(regexp = "[A-Z]{3}", message = "Currency must be in the format of three uppercase letters.")
    private String currency;
    @NotNull(message = "please enter merchant id.")
    private String merchantId;
}
