package com.nikhilm.hourglass.favouritesservice.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class SyncService {

    @Autowired
    FavouriteTriviaService favouriteTriviaService;

    @Autowired
    FavouriteMovieService favouriteMovieService;

    public void initializeUser(String userId) {
        Flux.merge(favouriteMovieService.createFavouriteList(userId)
                , favouriteTriviaService.createFavouriteList(userId))
        .blockLast();
    }


}
