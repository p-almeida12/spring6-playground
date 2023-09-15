package com.springframework.spring6playground.repositories;

import com.springframework.spring6playground.entities.Beer;
import com.springframework.spring6playground.entities.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CategoryRepositoryTest {

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    BeerRepository beerRepository;
    Beer beer;

    @BeforeEach
    void setUp() {
        beer = beerRepository.findAll().get(0);
    }

    @Transactional
    @Test
    void testAddCategory() {
        Category category = categoryRepository.save(Category.builder()
                .description("Ales")
                .build());

        beer.addCategory(category);
        Beer savedBeer = beerRepository.save(beer);

        System.out.println(savedBeer.getBeerName());
    }
}