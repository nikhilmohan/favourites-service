package com.nikhilm.hourglass.favouritesservice.services;

import com.nikhilm.hourglass.favouritesservice.exceptions.FavouritesException;
import com.nikhilm.hourglass.favouritesservice.models.*;
import com.nikhilm.hourglass.favouritesservice.repositories.FavouriteMoviesRepository;
import com.nikhilm.hourglass.favouritesservice.repositories.FavouriteTriviaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FavouriteTriviaService implements FavouriteService{

    @Autowired
    FavouriteTriviaRepository favouriteTriviaRepository;

    @Autowired
    ReactiveCircuitBreakerFactory reactiveCircuitBreakerFactory;

    @Autowired
    UserIntegrationService userIntegrationService;


    public Mono<FavouriteTrivia> getTrivia(String userId) {
        return favouriteTriviaRepository.findByUserId(userId);

    }

    public Mono<FavouriteTrivia> updateTriviaAsFavourite(String userId, Trivia trivia) {
        return favouriteTriviaRepository.findByUserId(userId)
                .map(favouriteTrivia -> {
                    if (favouriteTrivia.getTrivia().stream()
                            .anyMatch(existingTrivia -> existingTrivia.getTerm().equalsIgnoreCase(trivia.getTerm()))) {
                        favouriteTrivia.setTrivia(favouriteTrivia.getTrivia().stream()
                                .filter(trivia1 -> !trivia1.getTerm().equalsIgnoreCase(trivia.getTerm()))
                                .collect(Collectors.toList()));

                    } else  {
                        favouriteTrivia.getTrivia().add(trivia);
                    }
                    return favouriteTrivia;
                })
                .switchIfEmpty(Mono.error(new RuntimeException()))
                .flatMap(favouriteTriviaRepository::save);

    }

    public Mono<FavouriteTrivia> removeTriviaAsFavourite(String userId, Trivia trivia) {
        return favouriteTriviaRepository.findByUserId(userId)
                .map(favouriteTrivia -> {
                    List<Trivia> triviaList = favouriteTrivia.getTrivia()
                                            .stream().filter(trivia1 -> !(trivia1.getTerm().equalsIgnoreCase(trivia.getTerm())))
                                            .collect(Collectors.toList());
                    favouriteTrivia.setTrivia(triviaList);
                    return favouriteTrivia;
                })
                .switchIfEmpty(Mono.error(new RuntimeException()))
                .flatMap(favouriteTriviaRepository::save);
    }
    public Mono<FavouriteTrivia> createFavouriteList(String userId) {
        FavouriteTrivia favouriteTrivia = new FavouriteTrivia();
        favouriteTrivia.setUserId(userId);
        log.info("Saving init trivia " + userId);
        return favouriteTriviaRepository.save(favouriteTrivia)
                .doOnError(throwable -> {
                    log.error("Exception " + throwable.getMessage());
                    throw new FavouritesException(500, "Internal server error!");
                });
    }

    public Mono<FavouriteTriviaResponse> getFavouriteTrivia(String userId, Optional<String> terms) {

        FavouriteTriviaResponse response = new FavouriteTriviaResponse();
        return userIntegrationService.isUserLoggedIn(userId)
                .flatMap(loginStatus -> {
                    if (loginStatus) {
                        List<String> termList = (terms.isPresent()) ? getParamList(terms.get(), ",") : new ArrayList<>();

                        log.info("termList " + termList);
                        response.setUserId(userId);

                        return getTrivia(userId)
                                .flatMap(favouriteTrivia -> {
                                    log.info("favourite trivia " + favouriteTrivia);
                                    response.setUserId(favouriteTrivia.getUserId());
                                    response.getFavouriteTrivia().addAll(favouriteTrivia.getTrivia()
                                            .stream()
                                            .filter(trivia -> isTriviaRequested(trivia, termList))
                                            .collect(Collectors.toList()));
                                    return Mono.just(response);

                                })
                                .defaultIfEmpty(response);
                    } else return Mono.just(response);
                })
                .onErrorReturn(response);
    }

    private boolean isTriviaRequested(Trivia trivia, List<String> idList) {
        return idList.isEmpty() || idList.contains(trivia.getTerm());
    }
}
