package com.nikhilm.hourglass.favouritesservice.services;

import com.nikhilm.hourglass.favouritesservice.models.FavouriteMovies;
import com.nikhilm.hourglass.favouritesservice.models.FavouriteMoviesResponse;
import com.nikhilm.hourglass.favouritesservice.models.Movie;
import com.nikhilm.hourglass.favouritesservice.repositories.FavouriteMoviesRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
class FavouriteMovieServiceTest {

    @Mock
    FavouriteMoviesRepository favouriteMoviesRepository;

    @Mock
    ReactiveCircuitBreakerFactory reactiveCircuitBreakerFactory;

    @Mock
    UserIntegrationService userIntegrationService;

    @InjectMocks
    FavouriteMovieService favouriteMovieService;

    @Test
    public void testGetMovies() {
        FavouriteMovies favouriteMovies = new FavouriteMovies();
        favouriteMovies.setUserId("abc");
        Mockito.when(favouriteMoviesRepository.findByUserId(anyString())).thenReturn(Mono.just(favouriteMovies));
        StepVerifier.create(favouriteMovieService.getMovies("abc"))
                .expectSubscription()
                .expectNextMatches(favouriteMovies1 -> favouriteMovies1.getUserId().equalsIgnoreCase("abc"))
                .verifyComplete();
    }

    @Test
    public void testUpdateMovieAsFavourite()    {
        FavouriteMovies favouriteMovies = new FavouriteMovies();
        favouriteMovies.setUserId("abc");
        Mockito.when(favouriteMoviesRepository.findByUserId(anyString())).thenReturn(Mono.just(favouriteMovies));
        Mockito.when(favouriteMoviesRepository.save(any(FavouriteMovies.class)))
                .thenReturn(Mono.just(favouriteMovies));

        Movie movie = new Movie();
        movie.setName("some name");
        movie.setDescription("An interesting movie!");
        movie.setPoster("/url.jpg");
        movie.setId("abcdef");

        StepVerifier.create(favouriteMovieService.updateMovieAsFavourite("abc", movie))
                .expectSubscription()
                .expectNextMatches(favouriteMovies1 ->
                        favouriteMovies1.getMovies().stream()
                                .anyMatch(movie1 -> movie1.getName().equalsIgnoreCase(movie.getName())))
                .verifyComplete();
    }

    @Test
    public void testRemoveMovieAsFavourite()    {
        FavouriteMovies favouriteMovies = new FavouriteMovies();
        favouriteMovies.setUserId("abc");
        Movie movie = new Movie();
        movie.setName("some name");
        movie.setDescription("An interesting movie!");
        movie.setPoster("/url.jpg");
        movie.setId("abcdef");

        favouriteMovies.getMovies().add(movie);
        Mockito.when(favouriteMoviesRepository.findByUserId(anyString())).thenReturn(Mono.just(favouriteMovies));
        Mockito.when(favouriteMoviesRepository.save(any(FavouriteMovies.class)))
                .thenReturn(Mono.just(favouriteMovies));

        StepVerifier.create(favouriteMovieService.removeMovieAsFavourite("abc", movie))
                .expectSubscription()
                .expectNextMatches(favouriteMovies1 ->
                        favouriteMovies1.getMovies().stream()
                                .noneMatch(movie1 -> movie1.getName().equalsIgnoreCase(movie.getName())))
                .verifyComplete();


    }
    @Test
    public void testUpdateFavouriteMovieFailure()   {
        Mockito.when(favouriteMoviesRepository.findByUserId(anyString())).thenReturn(Mono.empty());

        StepVerifier.create(favouriteMovieService.updateMovieAsFavourite("abc", new Movie()))
                .expectSubscription()
                .expectError(RuntimeException.class)
                .verify();
    }
    @Test
    public void testUpdateFavouriteMovieDBError()   {
        FavouriteMovies favouriteMovies = new FavouriteMovies();
        favouriteMovies.setUserId("abc");
        Movie movie = new Movie();
        movie.setName("some name");
        movie.setDescription("An interesting movie!");
        movie.setPoster("/url.jpg");
        movie.setId("abcdef");

        favouriteMovies.getMovies().add(movie);
        Mockito.when(favouriteMoviesRepository.findByUserId(anyString())).thenReturn(Mono.just(favouriteMovies));
        Mockito.when(favouriteMoviesRepository.save(any(FavouriteMovies.class)))
                .thenThrow(new RuntimeException());


        StepVerifier.create(favouriteMovieService.updateMovieAsFavourite("abc", new Movie()))
                .expectSubscription()
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    public void testRemoveFavouriteMovieFailure()   {
        Mockito.when(favouriteMoviesRepository.findByUserId(anyString())).thenReturn(Mono.empty());

        StepVerifier.create(favouriteMovieService.removeMovieAsFavourite("abc", new Movie()))
                .expectSubscription()
                .expectError(RuntimeException.class)
                .verify();
    }
    @Test
    public void testRemoveFavouriteMovieDBError()   {
        FavouriteMovies favouriteMovies = new FavouriteMovies();
        favouriteMovies.setUserId("abc");
        Movie movie = new Movie();
        movie.setName("some name");
        movie.setDescription("An interesting movie!");
        movie.setPoster("/url.jpg");
        movie.setId("abcdef");

        favouriteMovies.getMovies().add(movie);
        Mockito.when(favouriteMoviesRepository.findByUserId(anyString())).thenReturn(Mono.just(favouriteMovies));
        Mockito.when(favouriteMoviesRepository.save(any(FavouriteMovies.class)))
                .thenThrow(new RuntimeException());


        StepVerifier.create(favouriteMovieService.removeMovieAsFavourite("abc", new Movie()))
                .expectSubscription()
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    public void testCreateFavouriteList()   {
        FavouriteMovies favouriteMovies = new FavouriteMovies();
        favouriteMovies.setUserId("abc");
        Mockito.when(favouriteMoviesRepository.save(any(FavouriteMovies.class)))
                .thenReturn(Mono.just(favouriteMovies));

        StepVerifier.create(favouriteMovieService.createFavouriteList("abc"))
                .expectSubscription()
                .expectNextMatches(favouriteMovies1 -> favouriteMovies1.getUserId().equalsIgnoreCase("abc"))
                .verifyComplete();

    }
    @Test
    public void testCreateFavouriteListError()   {
        FavouriteMovies favouriteMovies = new FavouriteMovies();
        favouriteMovies.setUserId("abc");
        Mockito.when(favouriteMoviesRepository.save(any(FavouriteMovies.class)))
                .thenReturn(Mono.error(new RuntimeException()));

        StepVerifier.create(favouriteMovieService.createFavouriteList("abc"))
                .expectSubscription()
                .expectErrorMessage("Internal server error!")
                .verify();

    }

    @Test
    public void testGetFavouriteMovies() {
        FavouriteMovies favouriteMovies = new FavouriteMovies();
        favouriteMovies.setUserId("abc");
        Movie movie = new Movie();
        movie.setName("some name");
        movie.setDescription("An interesting movie!");
        movie.setPoster("/url.jpg");
        movie.setId("abcdef");
        favouriteMovies.getMovies().add(movie);


        FavouriteMoviesResponse response = new FavouriteMoviesResponse();
        response.setUserId("abc");
        response.getFavouriteMovies().add(movie);

        Mockito.when(userIntegrationService.isUserLoggedIn("abc")).thenReturn(Mono.just(true));
        Mockito.when(favouriteMoviesRepository.findByUserId("abc")).thenReturn(Mono.just(favouriteMovies));

        StepVerifier.create(favouriteMovieService.getFavouriteMovies("abc", Optional.of("xyz,abcdef")))
                .expectSubscription()
                .expectNextMatches(favouriteMoviesResponse ->
                        favouriteMoviesResponse.getFavouriteMovies().size() == 1 &&
                        favouriteMoviesResponse.getFavouriteMovies().stream()
                                .anyMatch(movie1 -> movie1.getId().equalsIgnoreCase("abcdef")))
                .verifyComplete();
    }

    @Test
    public void testGetFavouriteMoviesNotLoggedIn() {
        FavouriteMovies favouriteMovies = new FavouriteMovies();
        favouriteMovies.setUserId("abc");
        Movie movie = new Movie();
        movie.setName("some name");
        movie.setDescription("An interesting movie!");
        movie.setPoster("/url.jpg");
        movie.setId("abcdef");
        favouriteMovies.getMovies().add(movie);


        FavouriteMoviesResponse response = new FavouriteMoviesResponse();

        Mockito.when(userIntegrationService.isUserLoggedIn("abc")).thenReturn(Mono.just(false));

        StepVerifier.create(favouriteMovieService.getFavouriteMovies("abc", Optional.of("xyz,abcdef")))
                .expectSubscription()
                .expectNextMatches(favouriteMoviesResponse ->
                        favouriteMoviesResponse.getFavouriteMovies().isEmpty() &&
                                favouriteMoviesResponse.getUserId() == null)
                .verifyComplete();
    }

    @Test
    public void testGetFavouriteMoviesInvalidUserId() {
        FavouriteMovies favouriteMovies = new FavouriteMovies();
        favouriteMovies.setUserId("abc");
        Movie movie = new Movie();
        movie.setName("some name");
        movie.setDescription("An interesting movie!");
        movie.setPoster("/url.jpg");
        movie.setId("abcdef");
        favouriteMovies.getMovies().add(movie);


        FavouriteMoviesResponse response = new FavouriteMoviesResponse();

        Mockito.when(userIntegrationService.isUserLoggedIn("abc")).thenReturn(Mono.just(true));
        Mockito.when(favouriteMoviesRepository.findByUserId("abc")).thenReturn(Mono.empty());

        StepVerifier.create(favouriteMovieService.getFavouriteMovies("abc", Optional.of("xyz,abcdef")))
                .expectSubscription()
                .expectNextMatches(favouriteMoviesResponse ->
                        favouriteMoviesResponse.getFavouriteMovies().isEmpty() &&
                                favouriteMoviesResponse.getUserId().equalsIgnoreCase("abc"))
                .verifyComplete();
    }



}