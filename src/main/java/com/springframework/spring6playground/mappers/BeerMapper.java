package com.springframework.spring6playground.mappers;

import com.springframework.spring6playground.entities.Beer;
import com.springframework.spring6playground.model.BeerDTO;
import org.mapstruct.Mapper;

@Mapper
public interface BeerMapper {

    Beer beerDtoToBeer(BeerDTO beerDTO);

    BeerDTO beerToBeerDto(Beer beer);

}
