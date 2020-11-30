package com.nikhilm.hourglass.favouritesservice.services;

import com.nikhilm.hourglass.favouritesservice.models.*;
import com.nikhilm.hourglass.favouritesservice.repositories.FavouriteMoviesRepository;
import com.nikhilm.hourglass.favouritesservice.repositories.FavouriteTriviaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
class FavouriteTriviaServiceTest {

    @Mock
    FavouriteTriviaRepository favouriteTriviaRepository;


    @Mock
    UserIntegrationService userIntegrationService;

    @InjectMocks
    FavouriteTriviaService favouriteTriviaService;

    @Test
    public void testGetTrivia() {
        FavouriteTrivia favouriteTrivia = new FavouriteTrivia();
        favouriteTrivia.setUserId("abc");
        Mockito.when(favouriteTriviaRepository.findByUserId(anyString())).thenReturn(Mono.just(favouriteTrivia));
        StepVerifier.create(favouriteTriviaService.getTrivia("abc"))
                .expectSubscription()
                .expectNextMatches(favouriteTrivia1 -> favouriteTrivia1.getUserId().equalsIgnoreCase("abc"))
                .verifyComplete();
    }

    @Test
    public void testUpdateTriviaAsFavourite() {
        FavouriteTrivia favouriteTrivia = new FavouriteTrivia();
        favouriteTrivia.setUserId("abc");
        Mockito.when(favouriteTriviaRepository.findByUserId(anyString())).thenReturn(Mono.just(favouriteTrivia));
        Mockito.when(favouriteTriviaRepository.save(any(FavouriteTrivia.class)))
                .thenReturn(Mono.just(favouriteTrivia));

        Trivia trivia = new Trivia();
        trivia.setCategory("travel");
        trivia.setTerm("Austria");
        trivia.setFact("Hallstatt is a beautiful town!");

        StepVerifier.create(favouriteTriviaService.updateTriviaAsFavourite("abc", trivia))
                .expectSubscription()
                .expectNextMatches(favouriteTrivia1 ->
                        favouriteTrivia1.getTrivia().stream()
                                .anyMatch(trivia1 -> trivia1.getTerm().equalsIgnoreCase(trivia.getTerm())))
                .verifyComplete();
    }

    @Test
    public void testRemoveTriviaAsFavourite() {
        FavouriteTrivia favouriteTrivia = new FavouriteTrivia();
        favouriteTrivia.setUserId("abc");
        Trivia trivia = new Trivia();
        trivia.setCategory("travel");
        trivia.setTerm("Austria");
        trivia.setFact("Hallstatt is a beautiful town!");


        favouriteTrivia.getTrivia().add(trivia);
        Mockito.when(favouriteTriviaRepository.findByUserId(anyString())).thenReturn(Mono.just(favouriteTrivia));
        Mockito.when(favouriteTriviaRepository.save(any(FavouriteTrivia.class)))
                .thenReturn(Mono.just(favouriteTrivia));

        StepVerifier.create(favouriteTriviaService.removeTriviaAsFavourite("abc", trivia))
                .expectSubscription()
                .expectNextMatches(favouriteTrivia1 ->
                        favouriteTrivia1.getTrivia().stream()
                                .noneMatch(trivia1 -> trivia1.getTerm().equalsIgnoreCase(trivia.getTerm())))
                .verifyComplete();


    }

    @Test
    public void testUpdateFavouriteTriviaFailure() {
        Mockito.when(favouriteTriviaRepository.findByUserId(anyString())).thenReturn(Mono.empty());

        StepVerifier.create(favouriteTriviaService.updateTriviaAsFavourite("abc", new Trivia()))
                .expectSubscription()
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    public void testUpdateFavouriteTriviaDBError() {
        FavouriteTrivia favouriteTrivia = new FavouriteTrivia();
        favouriteTrivia.setUserId("abc");
        Trivia trivia = new Trivia();
        trivia.setCategory("travel");
        trivia.setTerm("Austria");
        trivia.setFact("Hallstatt is a beautiful town!");


        favouriteTrivia.getTrivia().add(trivia);
        Mockito.when(favouriteTriviaRepository.findByUserId(anyString())).thenReturn(Mono.just(favouriteTrivia));
        Mockito.when(favouriteTriviaRepository.save(any(FavouriteTrivia.class)))
                .thenThrow(new RuntimeException());


        StepVerifier.create(favouriteTriviaService.updateTriviaAsFavourite("abc", new Trivia()))
                .expectSubscription()
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    public void testRemoveFavouriteTriviaFailure() {
        Mockito.when(favouriteTriviaRepository.findByUserId(anyString())).thenReturn(Mono.empty());

        StepVerifier.create(favouriteTriviaService.removeTriviaAsFavourite("abc", new Trivia()))
                .expectSubscription()
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    public void testRemoveFavouriteTriviaDBError() {
        FavouriteTrivia favouriteTrivia = new FavouriteTrivia();
        favouriteTrivia.setUserId("abc");
        Trivia trivia = new Trivia();
        trivia.setCategory("travel");
        trivia.setTerm("Austria");
        trivia.setFact("Hallstatt is a beautiful town!");

        favouriteTrivia.getTrivia().add(trivia);
        Mockito.when(favouriteTriviaRepository.findByUserId(anyString())).thenReturn(Mono.just(favouriteTrivia));
        Mockito.when(favouriteTriviaRepository.save(any(FavouriteTrivia.class)))
                .thenThrow(new RuntimeException());
        StepVerifier.create(favouriteTriviaService.removeTriviaAsFavourite("abc", new Trivia()))
                .expectSubscription()
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    public void testCreateFavouriteList() {
        FavouriteTrivia favouriteTrivia = new FavouriteTrivia();
        favouriteTrivia.setUserId("abc");
        Mockito.when(favouriteTriviaRepository.save(any(FavouriteTrivia.class)))
                .thenReturn(Mono.just(favouriteTrivia));

        StepVerifier.create(favouriteTriviaService.createFavouriteList("abc"))
                .expectSubscription()
                .expectNextMatches(favouriteTrivia1 -> favouriteTrivia1.getUserId().equalsIgnoreCase("abc"))
                .verifyComplete();

    }

    @Test
    public void testCreateFavouriteListError() {
        FavouriteTrivia favouriteTrivia = new FavouriteTrivia();
        favouriteTrivia.setUserId("abc");
        Mockito.when(favouriteTriviaRepository.save(any(FavouriteTrivia.class)))
                .thenReturn(Mono.error(new RuntimeException()));

        StepVerifier.create(favouriteTriviaService.createFavouriteList("abc"))
                .expectSubscription()
                .expectErrorMessage("Internal server error!")
                .verify();

    }

    @Test
    public void testGetFavouriteTrivia() {
        FavouriteTrivia favouriteTrivia = new FavouriteTrivia();
        favouriteTrivia.setUserId("abc");
        Trivia trivia = new Trivia();
        trivia.setCategory("travel");
        trivia.setTerm("Austria");
        trivia.setFact("Hallstatt is a beautiful town!");
        favouriteTrivia.getTrivia().add(trivia);


        FavouriteTriviaResponse response = new FavouriteTriviaResponse();
        response.setUserId("abc");
        response.getFavouriteTrivia().add(trivia);

        Mockito.when(userIntegrationService.isUserLoggedIn("abc")).thenReturn(Mono.just(true));
        Mockito.when(favouriteTriviaRepository.findByUserId("abc")).thenReturn(Mono.just(favouriteTrivia));

        StepVerifier.create(favouriteTriviaService.getFavouriteTrivia("abc", Optional.of("xyz,Austria")))
                .expectSubscription()
                .expectNextMatches(favouriteTriviaResponse ->
                        favouriteTriviaResponse.getFavouriteTrivia().size() == 1 &&
                                favouriteTrivia.getTrivia().stream()
                                        .anyMatch(trivia1 -> trivia.getTerm().equalsIgnoreCase("Austria")))
                .verifyComplete();
    }

    @Test
    public void testGetFavouriteMoviesNotLoggedIn() {
        FavouriteTrivia favouriteTrivia = new FavouriteTrivia();
        favouriteTrivia.setUserId("abc");
        Trivia trivia = new Trivia();
        trivia.setCategory("travel");
        trivia.setTerm("Austria");
        trivia.setFact("Hallstatt is a beautiful town!");
        favouriteTrivia.getTrivia().add(trivia);


        FavouriteTriviaResponse response = new FavouriteTriviaResponse();
        response.setUserId("abc");
        response.getFavouriteTrivia().add(trivia);

        Mockito.when(userIntegrationService.isUserLoggedIn("abc")).thenReturn(Mono.just(false));
        StepVerifier.create(favouriteTriviaService.getFavouriteTrivia("abc", Optional.of("xyz,Austria")))
                .expectSubscription()
                .expectNextMatches(favouriteTriviaResponse ->
                        favouriteTriviaResponse.getFavouriteTrivia().isEmpty())

                .verifyComplete();


    }
    @Test
    public void testGetFavouriteMoviesInvalidUserId() {
        FavouriteTrivia favouriteTrivia = new FavouriteTrivia();
        favouriteTrivia.setUserId("abc");
        Trivia trivia = new Trivia();
        trivia.setCategory("travel");
        trivia.setTerm("Austria");
        trivia.setFact("Hallstatt is a beautiful town!");
        favouriteTrivia.getTrivia().add(trivia);


        FavouriteTriviaResponse response = new FavouriteTriviaResponse();
        response.setUserId("abc");
        response.getFavouriteTrivia().add(trivia);

        Mockito.when(userIntegrationService.isUserLoggedIn("abc")).thenReturn(Mono.just(true));
        Mockito.when(favouriteTriviaRepository.findByUserId("abc")).thenReturn(Mono.empty());

        StepVerifier.create(favouriteTriviaService.getFavouriteTrivia("abc", Optional.of("xyz,Austria")))
                .expectSubscription()
                .expectNextMatches(favouriteTriviaResponse ->
                        favouriteTriviaResponse.getFavouriteTrivia().isEmpty())
                .verifyComplete();
    }

}