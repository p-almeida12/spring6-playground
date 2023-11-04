package com.springframework.spring6playground.controller;

import com.springframework.spring6playground.exception.BeerNotFoundException;
import com.springframework.spring6playground.exception.CustomerNotFoundException;
import com.springframework.spring6playground.model.ErrorDetails;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
public class CustomErrorController {

    @ExceptionHandler()
    ResponseEntity handleJpaViolations(TransactionSystemException transactionSystemException){
        ResponseEntity.BodyBuilder responseEntity = ResponseEntity.badRequest();

        if (transactionSystemException.getCause().getCause() instanceof ConstraintViolationException) {
            ConstraintViolationException constraintViolationException = (ConstraintViolationException) transactionSystemException
                    .getCause()
                    .getCause();

            List errors = constraintViolationException.getConstraintViolations()
                    .stream()
                    .map(constraintViolation -> {
                        Map<String, String> errorMap = new HashMap<>();
                        errorMap.put(constraintViolation.getPropertyPath().toString(),
                                constraintViolation.getMessage());

                        return errorMap;
                    }).toList();
            return responseEntity.body(errors);
        }

        return responseEntity.build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity handleBindErrors(MethodArgumentNotValidException exception) {

        List errorList = exception.getFieldErrors().stream()
                .map(fieldError -> {
                    Map<String, String> errorMap= new HashMap<>();
                    errorMap.put(fieldError.getField(), fieldError.getDefaultMessage());
                    return  errorMap;
                }).toList();

        return ResponseEntity.badRequest().body(errorList);
    }

    @ExceptionHandler({BeerNotFoundException.class, CustomerNotFoundException.class})
    public final ResponseEntity<ErrorDetails> handleUserNotFoundExceptions(Exception ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(),
                ex.getMessage(), request.getDescription(false));

        return new ResponseEntity<ErrorDetails>(errorDetails, HttpStatus.NOT_FOUND);

    }

}
