package com.springframework.spring6playground.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springframework.spring6playground.config.SpringSecurityConfig;
import com.springframework.spring6playground.model.CustomerDTO;
import com.springframework.spring6playground.services.CustomerService;
import com.springframework.spring6playground.services.CustomerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
@Import(SpringSecurityConfig.class)
class CustomerControllerTest {

    CustomerServiceImpl customerServiceImpl;

    @BeforeEach
    void setUp() {
        customerServiceImpl = new CustomerServiceImpl();
    }

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    CustomerService customerService;

    @Captor
    ArgumentCaptor<UUID> argumentCaptor;

    @Captor
    ArgumentCaptor<CustomerDTO> customerArgumentCaptor;

    @Test
    void testPatchCustomer() throws Exception {
        CustomerDTO customer = customerServiceImpl.getAllCustomers().get(0);

        Map<String, Object> customerMap = new HashMap<>();
        customerMap.put("name", "New Name");

        mockMvc.perform(patch(CustomerController.CUSTOMER_PATH_ID, customer.getId())
                        .with(httpBasic(BeerControllerTest.USERNAME, BeerControllerTest.PASSWORD))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerMap)))
                .andExpect(status().isNoContent());

        verify(customerService).patchByCustomerId(argumentCaptor.capture(),
                customerArgumentCaptor.capture());

        assertThat(argumentCaptor.getValue()).isEqualTo(customer.getId());
        assertThat(customerArgumentCaptor.getValue().getName())
                .isEqualTo(customerMap.get("name"));
    }

    @Test
    void testDeleteCustomer() throws Exception {
        CustomerDTO customer = customerServiceImpl.getAllCustomers().get(0);

        given(customerService.deleteById(any())).willReturn(true);

        mockMvc.perform(delete(CustomerController.CUSTOMER_PATH_ID, customer.getId())
                        .with(httpBasic(BeerControllerTest.USERNAME, BeerControllerTest.PASSWORD))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(customerService).deleteById(argumentCaptor.capture());

        assertThat(customer.getId()).isEqualTo(argumentCaptor.getValue());
    }

    @Test
    void testUpdateCutomer() throws Exception {
        CustomerDTO customer = customerServiceImpl.getAllCustomers().get(0);

        given(customerService.updateCustomerById(any(), any())).willReturn(Optional.of(CustomerDTO.builder()
                .build()));

        mockMvc.perform(put(CustomerController.CUSTOMER_PATH_ID, customer.getId())
                        .with(httpBasic(BeerControllerTest.USERNAME, BeerControllerTest.PASSWORD))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customer)))
                .andExpect(status().isNoContent());

        verify(customerService).updateCustomerById(any(UUID.class), any(CustomerDTO.class));

    }

    @Test
    void testCreateNewCustomer() throws Exception {
        CustomerDTO customer = customerServiceImpl.getAllCustomers().get(0);
        customer.setId(null);
        customer.setVersion(null);

        given(customerService.saveNewCustomer(any(CustomerDTO.class))).willReturn(customerServiceImpl.getAllCustomers().get(1));

        mockMvc.perform(post(CustomerController.CUSTOMER_PATH)
                        .with(httpBasic(BeerControllerTest.USERNAME, BeerControllerTest.PASSWORD))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customer)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));
    }

    @Test
    void testGetCustomerList() throws Exception {
        given(customerService.getAllCustomers()).willReturn(customerServiceImpl.getAllCustomers());

        mockMvc.perform(get(CustomerController.CUSTOMER_PATH)
                        .with(httpBasic(BeerControllerTest.USERNAME, BeerControllerTest.PASSWORD))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()", is(3))
                );
    }

    @Test
    void getCustomerByIdNotFound() throws Exception {

        given(customerService.getCustomerById(any(UUID.class))).willReturn(Optional.empty());

        mockMvc.perform(get(CustomerController.CUSTOMER_PATH_ID, UUID.randomUUID())
                        .with(httpBasic(BeerControllerTest.USERNAME, BeerControllerTest.PASSWORD)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetCustomerById() throws Exception {
        CustomerDTO testCustomer = customerServiceImpl.getAllCustomers().get(0);

        given(customerService.getCustomerById(testCustomer.getId())).willReturn(Optional.of(testCustomer));

        mockMvc.perform(get(CustomerController.CUSTOMER_PATH_ID, testCustomer.getId())
                        .with(httpBasic(BeerControllerTest.USERNAME, BeerControllerTest.PASSWORD))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                //validate json using jason matchers
                .andExpect(jsonPath("$.id", is(testCustomer.getId().toString())))
                .andExpect(jsonPath("$.name", is(testCustomer.getName())));
    }

}