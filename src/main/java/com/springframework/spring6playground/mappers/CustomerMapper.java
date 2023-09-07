package com.springframework.spring6playground.mappers;

import com.springframework.spring6playground.entities.Customer;
import com.springframework.spring6playground.model.CustomerDTO;
import org.mapstruct.Mapper;

@Mapper
public interface CustomerMapper {

    Customer customerDtoToCustomer (CustomerDTO customerDTO);

    CustomerDTO customerToCustomerDto (Customer customer);

}
