package com.paylin.payment.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.validation.ValidationException;

@ControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<String> handle(MethodArgumentNotValidException ex) {

        BindingResult result = ex.getBindingResult();
        StringBuilder errorMessage = new StringBuilder();
        result.getFieldErrors().forEach(fieldError -> {
            errorMessage.append(fieldError.getField()).append(": ").append(fieldError.getDefaultMessage()).append("\n");
        });
        return new ResponseEntity<>(errorMessage.toString(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<String> handle(ValidationException exception) {

        String errorMessage = exception.getMessage();
        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }
}
