package com.springframework.spring6playground.controller;

import com.springframework.spring6playground.exception.BeerNotFoundException;
import com.springframework.spring6playground.exception.CustomerNotFoundException;
import com.springframework.spring6playground.model.CustomerDTO;
import com.springframework.spring6playground.services.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
public class CustomerController {

    public static final String CUSTOMER_PATH = "/api/v1/customer";
    public static final String CUSTOMER_PATH_ID = CUSTOMER_PATH + "/{customerId}";

    private final CustomerService customerService;

    @Autowired
    private MessageSource messageSource;

    @PatchMapping(CUSTOMER_PATH_ID)
    public ResponseEntity updateCustomerPatchById(@PathVariable("customerId") UUID customerId, @RequestBody CustomerDTO customer) {

        customerService.patchByCustomerId(customerId, customer);

        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping(CUSTOMER_PATH_ID)
    public ResponseEntity deleteById(@PathVariable("customerId") UUID customerId) {

        if (!customerService.deleteById(customerId)){
            Locale locale = LocaleContextHolder.getLocale();
            throw new BeerNotFoundException(messageSource.getMessage(
                    "customer.not.found.message",
                    null,
                    "Default Message", locale)
                    + customerId);
        }

        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @PutMapping(CUSTOMER_PATH_ID)
    public ResponseEntity updateById(@PathVariable("customerId") UUID customerId, @RequestBody CustomerDTO customer) {

        if (customerService.updateCustomerById(customerId, customer).isEmpty()){
            Locale locale = LocaleContextHolder.getLocale();
            throw new BeerNotFoundException(messageSource.getMessage(
                    "customer.not.found.message",
                    null,
                    "Default Message", locale)
                    + customerId);
        }

        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @GetMapping(CUSTOMER_PATH)
    public List<CustomerDTO> listAllCustomers() {
        return customerService.getAllCustomers();
    }

    @GetMapping(CUSTOMER_PATH_ID)
    public CustomerDTO getCustomerById(@PathVariable("customerId") UUID id) {
        Locale locale = LocaleContextHolder.getLocale();
        return customerService.getCustomerById(id).orElseThrow(() -> new CustomerNotFoundException(messageSource.getMessage(
                "customer.not.found.message",
                null,
                "Default Message", locale)
                + id));
    }

    @PostMapping(CUSTOMER_PATH)
    public ResponseEntity handlePost(@RequestBody CustomerDTO customer) {
        CustomerDTO savedCustomer = customerService.saveNewCustomer(customer);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", CUSTOMER_PATH + "/" + savedCustomer.getId().toString());

        return new ResponseEntity(headers, HttpStatus.CREATED);
    }

}
