package com.springframework.spring6playground.bootstrap;

import com.springframework.spring6playground.entities.Beer;
import com.springframework.spring6playground.entities.Customer;
import com.springframework.spring6playground.model.BeerStyle;
import com.springframework.spring6playground.repositories.BeerRepository;
import com.springframework.spring6playground.repositories.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;

@Component //spring component when spring boot starts at full context this component will run and be executed
@RequiredArgsConstructor
public class BootstrapData implements CommandLineRunner {
    private final BeerRepository beerRepository;
    private final CustomerRepository customerRepository;

    @Override
    public void run(String... args) throws Exception {
        loadBeerData();
        loadCustomerData();
    }

    private void loadBeerData() {
        if (beerRepository.count() == 0) {
            Beer beer1 = Beer.builder()
                    .beerName("Sierra Nevada")
                    .beerStyle(BeerStyle.PALE_ALE)
                    .upc("45113432")
                    .price(new BigDecimal("12.90"))
                    .quantityOnHand(234)
                    .createdDate(LocalDateTime.now())
                    .updatedDate(LocalDateTime.now())
                    .build();

            Beer beer2 = Beer.builder()
                    .beerName("Super Bock")
                    .beerStyle(BeerStyle.PILSNER)
                    .upc("123123")
                    .price(new BigDecimal("1.20"))
                    .quantityOnHand(123)
                    .createdDate(LocalDateTime.now())
                    .updatedDate(LocalDateTime.now())
                    .build();

            Beer beer3 = Beer.builder()
                    .beerName("Guinness")
                    .beerStyle(BeerStyle.IPA)
                    .upc("123123")
                    .price(new BigDecimal("11.50"))
                    .quantityOnHand(123)
                    .createdDate(LocalDateTime.now())
                    .updatedDate(LocalDateTime.now())
                    .build();

            beerRepository.saveAll(Arrays.asList(beer1, beer2, beer3));
        }
    }

    private void loadCustomerData() {
        if (customerRepository.count() == 0) {
            Customer customer1 = Customer.builder()
                    .name("Customer 1")
                    .createdDate(LocalDateTime.now())
                    .updateDate(LocalDateTime.now())
                    .build();

            Customer customer2 = Customer.builder()
                    .name("Customer 2")
                    .createdDate(LocalDateTime.now())
                    .updateDate(LocalDateTime.now())
                    .build();

            Customer customer3 = Customer.builder()
                    .name("Customer 3")
                    .createdDate(LocalDateTime.now())
                    .updateDate(LocalDateTime.now())
                    .build();

            customerRepository.saveAll(Arrays.asList(customer1, customer2, customer3));
        }
    }
}
