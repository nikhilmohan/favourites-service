package com.nikhilm.hourglass.favouritesservice.services;

import com.nikhilm.hourglass.favouritesservice.models.FavouriteMovies;
import com.nikhilm.hourglass.favouritesservice.models.FavouriteTrivia;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class SyncServiceTest {

    @Mock
    FavouriteTriviaService favouriteTriviaService;

    @Mock
    FavouriteMovieService favouriteMovieService;

    @InjectMocks
    SyncService syncService;

    @Test
    void initializeUser() {
        Mockito.when(favouriteMovieService.createFavouriteList("abc")).thenReturn(Mono.just(new FavouriteMovies()));
        Mockito.when(favouriteTriviaService.createFavouriteList("abc")).thenReturn(Mono.just(new FavouriteTrivia()));

        assertDoesNotThrow( ()-> syncService.initializeUser("abc"));
    }
}