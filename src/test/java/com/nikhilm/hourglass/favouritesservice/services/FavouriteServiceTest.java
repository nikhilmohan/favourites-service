package com.nikhilm.hourglass.favouritesservice.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class FavouriteServiceTest {

    @Mock
    ReactiveCircuitBreakerFactory factory;

    @InjectMocks
    FavouriteMovieService favouriteService;

    @Test
    void getParamList() {
        List<String> idList = List.of("apple", "mango", "orange");
        List<String> result = favouriteService.getParamList("apple,mango,orange", ",");

        assertEquals(3, result.size());
        assertEquals(result, idList);



    }
}