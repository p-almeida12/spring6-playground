package com.springframework.spring6playground.repositories;

import com.springframework.spring6playground.bootstrap.BootstrapData;
import com.springframework.spring6playground.entities.Beer;
import com.springframework.spring6playground.model.BeerStyle;
import com.springframework.spring6playground.services.BeerCsvServiceImpl;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@Import({BootstrapData.class, BeerCsvServiceImpl.class})
class BeerRepositoryTest {

    @Autowired
    BeerRepository beerRepository;

    @Test
    void testSaveBeer() {
        Beer savedBeer = beerRepository.save(Beer.builder()
                .beerName("Paulo's Beer")
                        .beerStyle(BeerStyle.PILSNER)
                        .upc("123244212")
                        .price(new BigDecimal("23.44"))
                .build());

        beerRepository.flush();

        assertThat(savedBeer).isNotNull();
        assertThat(savedBeer.getId()).isNotNull();
    }

    @Test
    void testSaveBeerNameInvalidLenght() {
        assertThrows(ConstraintViolationException.class, () -> {
            Beer savedBeer = beerRepository.save(Beer.builder()
                    .beerName("Paulo's Beer too long too long too long too long too long too long")
                    .beerStyle(BeerStyle.PILSNER)
                    .upc("123244212")
                    .price(new BigDecimal("23.44"))
                    .build());

            beerRepository.flush();
        });
    }

    @Test
    void testGetBeerListByName() {
        List<Beer> beerList = beerRepository.findAllByBeerNameIsLikeIgnoreCase("%IPA%");

        assertThat(beerList.size()).isEqualTo(336);
    }

    @Test
    void testGetBeerListByBeerStyle() {
        List<Beer> beerList = beerRepository.findAllByBeerStyle(BeerStyle.IPA);

        assertThat(beerList.size()).isEqualTo(548);
    }
}