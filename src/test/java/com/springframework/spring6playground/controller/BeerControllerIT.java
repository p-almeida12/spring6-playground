package com.springframework.spring6playground.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springframework.spring6playground.entities.Beer;
import com.springframework.spring6playground.mappers.BeerMapper;
import com.springframework.spring6playground.model.BeerDTO;
import com.springframework.spring6playground.model.BeerStyle;
import com.springframework.spring6playground.repositories.BeerRepository;
import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.core.Is.is;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//integration test

@SpringBootTest //testing the whole spring boot context, so its going to run the bootstrap data
class BeerControllerIT {

    @Autowired
    BeerController beerController;

    @Autowired
    BeerRepository beerRepository;

    @Autowired
    BeerMapper beerMapper;

    @Autowired
    WebApplicationContext webApplicationContext;

    @Autowired
    ObjectMapper objectMapper;

    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    void testListBeers() {
        Page<BeerDTO> dtos = beerController.listBeers(null, null, false, 1, 2413);

        assertThat(dtos.getContent().size()).isEqualTo(1000);
    }

    @Rollback //after the test is completed the transaction is set its prior state, not affecting other tests
    @Transactional //setting the test as transactional
    @Test
    void testEmptyList() {
        beerRepository.deleteAll();
        Page<BeerDTO> beerPage = beerController.listBeers(null, null, false, 1, 25);

        assertThat(beerPage.getContent().size()).isEqualTo(0);
    }

    @Test
    void testGetById() {
        Beer beer = beerRepository.findAll().get(0);
        BeerDTO beerDTO = beerController.getBeerById(beer.getId());

        assertThat(beerDTO).isNotNull();
    }

    @Test
    void testBeerIdNotFound() {
        assertThrows(NotFoundException.class, () -> {
            beerController.getBeerById(UUID.randomUUID());
        });
    }

    @Rollback
    @Transactional
    @Test
    void testSaveNewBeer() {
        BeerDTO beerDTO = BeerDTO.builder()
                .beerName("New Beer")
                .build();

        ResponseEntity responseEntity = beerController.handlePost(beerDTO);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(201));
        assertThat(responseEntity.getHeaders().getLocation()).isNotNull();

        String[] locationUUID = responseEntity.getHeaders().getLocation().getPath().split("/");
        UUID savedUUID = UUID.fromString(locationUUID[4]);

        Beer beer = beerRepository.findById(savedUUID).get();
        assertThat(beer).isNotNull();
    }

    @Rollback
    @Transactional
    @Test
    void testUpdateBeer() {
        Beer beer = beerRepository.findAll().get(0);
        BeerDTO beerDTO = beerMapper.beerToBeerDto(beer);
        beerDTO.setId(null);
        beerDTO.setVersion(null);
        final String beerName = "UPDATED NAME";
        beerDTO.setBeerName(beerName);

        ResponseEntity responseEntity = beerController.updateById(beer.getId(), beerDTO);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(204));

        Beer updatedBeer = beerRepository.findById(beer.getId()).get();
        assertThat(updatedBeer.getBeerName()).isEqualTo(beerName);
    }

    @Test
    void testUpdateBeerNotFound() {
        assertThrows(NotFoundException.class, () -> {
            beerController.updateById(UUID.randomUUID(), BeerDTO.builder().build());
        });
    }

    @Rollback
    @Transactional
    @Test
    void testDeleteById() {
        Beer beer = beerRepository.findAll().get(0);

        ResponseEntity responseEntity = beerController.deleteById(beer.getId());
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(204));
        assertThat(beerRepository.findById(beer.getId())).isEmpty();
    }

    @Test
    void testDeleteByIdNotFound() {
        assertThrows(NotFoundException.class, () -> {
            beerController.deleteById(UUID.randomUUID());
        });
    }

    @Test
    void testPatchBeerInvalidName() throws Exception {
        Beer beer = beerRepository.findAll().get(0);

        Map<String, Object> beerMap = new HashMap<>();
        beerMap.put("beerName", "New Name too long too long too long too long too long too long too long too long too long too long too long");

        MvcResult mvcResult = mockMvc.perform(patch(BeerController.BEER_PATH_ID, beer.getId())
                        .with(httpBasic(BeerControllerTest.USERNAME, BeerControllerTest.PASSWORD))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(beerMap)))
                .andExpect(status().isBadRequest()).andReturn();

        System.out.println(mvcResult.getResponse().getContentAsString());
    }

    @Test
    void testListBeersFilterByName() throws Exception {
        mockMvc.perform(get(BeerController.BEER_PATH)
                        .with(httpBasic(BeerControllerTest.USERNAME, BeerControllerTest.PASSWORD))
                        .queryParam("beerName", "IPA")
                        .queryParam("pageSize", "800"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.size()", is(336)));
    }

    @Test
    void testListBeersFilterByStyle() throws Exception {
        mockMvc.perform(get(BeerController.BEER_PATH)
                        .with(httpBasic(BeerControllerTest.USERNAME, BeerControllerTest.PASSWORD))
                        .queryParam("beerStyle", BeerStyle.IPA.name())
                        .queryParam("pageSize", "800"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.size()", is(548)));
    }

    @Test
    void tesListBeersByStyleAndName() throws Exception {
        mockMvc.perform(get(BeerController.BEER_PATH)
                        .with(httpBasic(BeerControllerTest.USERNAME, BeerControllerTest.PASSWORD))
                        .queryParam("beerName", "IPA")
                        .queryParam("beerStyle", BeerStyle.IPA.name())
                        .queryParam("pageSize", "800"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.size()", is(310)));
    }

    @Test
    void tesListBeersByStyleAndNameShowInventoryTrue() throws Exception {
        mockMvc.perform(get(BeerController.BEER_PATH)
                        .with(httpBasic(BeerControllerTest.USERNAME, BeerControllerTest.PASSWORD))
                        .queryParam("beerName", "IPA")
                        .queryParam("beerStyle", BeerStyle.IPA.name())
                        .queryParam("showInventory", "true")
                        .queryParam("pageSize", "800"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.size()", is(310)))
                .andExpect(jsonPath("$.content.[0].quantityOnHand").value(IsNull.notNullValue()));
    }

    @Test
    void tesListBeersByStyleAndNameShowInventoryFalse() throws Exception {
        mockMvc.perform(get(BeerController.BEER_PATH)
                        .with(httpBasic(BeerControllerTest.USERNAME, BeerControllerTest.PASSWORD))
                        .queryParam("beerName", "IPA")
                        .queryParam("beerStyle", BeerStyle.IPA.name())
                        .queryParam("showInventory", "false")
                        .queryParam("pageSize", "800"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.size()", is(310)))
                .andExpect(jsonPath("$.content.[0].quantityOnHand").value(IsNull.nullValue()));
    }

    @Test
    void tesListBeersByStyleAndNameShowInventoryTruePage2() throws Exception {
        mockMvc.perform(get(BeerController.BEER_PATH)
                        .with(httpBasic(BeerControllerTest.USERNAME, BeerControllerTest.PASSWORD))
                        .queryParam("beerName", "IPA")
                        .queryParam("beerStyle", BeerStyle.IPA.name())
                        .queryParam("showInventory", "true")
                        .queryParam("pageNumber", "2")
                        .queryParam("pageSize", "50"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.size()", is(50)))
                .andExpect(jsonPath("$.content[0].quantityOnHand").value(IsNull.notNullValue()));
    }

    @Disabled //optimistic locking demo
    @Test
    void testUpdateBeerBadVersion() throws Exception {
        Beer beer = beerRepository.findAll().get(0);

        BeerDTO beerDTO = beerMapper.beerToBeerDto(beer);

        beerDTO.setBeerName("Updated 1");

        MvcResult result = mockMvc.perform(put(BeerController.BEER_PATH_ID, beer.getId())
                        .with(httpBasic(BeerControllerTest.USERNAME, BeerControllerTest.PASSWORD))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(beerDTO)))
                .andExpect(status().isNoContent())
                .andReturn();

        System.out.println(result.getResponse().getContentAsString());

        beerDTO.setBeerName("Updated 2");

        MvcResult result2 = mockMvc.perform(put(BeerController.BEER_PATH_ID, beer.getId())
                        .with(httpBasic(BeerControllerTest.USERNAME, BeerControllerTest.PASSWORD))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(beerDTO)))
                .andExpect(status().isNoContent())
                .andReturn();

        System.out.println(result2.getResponse().getStatus());
    }

    @Test
    void testNoAuthorization() throws Exception {
        mockMvc.perform(get(BeerController.BEER_PATH)
                        .queryParam("beerStyle", BeerStyle.PILSNER.name())
                        .queryParam("pageSize", "222"))
                .andExpect(status().isUnauthorized());
    }
}