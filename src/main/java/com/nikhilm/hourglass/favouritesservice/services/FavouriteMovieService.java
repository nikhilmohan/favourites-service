package com.nikhilm.hourglass.favouritesservice.services;

import com.nikhilm.hourglass.favouritesservice.exceptions.FavouritesException;
import com.nikhilm.hourglass.favouritesservice.models.FavouriteMovies;
import com.nikhilm.hourglass.favouritesservice.models.FavouriteMoviesResponse;
import com.nikhilm.hourglass.favouritesservice.models.Movie;
import com.nikhilm.hourglass.favouritesservice.repositories.FavouriteMoviesRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FavouriteMovieService implements FavouriteService{

    @Autowired
    FavouriteMoviesRepository favouriteMoviesRepository;


    @Autowired
    UserIntegrationService userIntegrationService;


    public Mono<FavouriteMovies> getMovies(String userId) {
        return favouriteMoviesRepository.findByUserId(userId);

    }

    public Mono<FavouriteMovies> updateMovieAsFavourite(String userId, Movie movie) {
        return favouriteMoviesRepository.findByUserId(userId)
                .map(favouriteMovies -> {
                    if (favouriteMovies.getMovies().stream()
                            .anyMatch(existingMovie -> existingMovie.getId().equalsIgnoreCase(movie.getId()))) {
                        favouriteMovies.setMovies(favouriteMovies.getMovies().stream()
                                .filter(movie1 -> !movie1.getId().equalsIgnoreCase(movie.getId()))
                                .collect(Collectors.toList()));

                    } else  {
                        favouriteMovies.getMovies().add(movie);
                    }

                    log.info("Updated favourite movie " + movie + " for user " + userId);
                    return favouriteMovies;
                })
                .switchIfEmpty(Mono.error(new RuntimeException()))
                .flatMap(favouriteMovies -> {
                        log.info("favourite movies " + favouriteMovies);
                        return favouriteMoviesRepository.save(favouriteMovies);
                });

    }

    public Mono<FavouriteMovies> removeMovieAsFavourite(String userId, Movie movie) {
        return favouriteMoviesRepository.findByUserId(userId)
                .map(favouriteMovies -> {
                    List<Movie> movies = favouriteMovies.getMovies()
                                            .stream().filter(movie1 -> !(movie1.getId().equalsIgnoreCase(movie.getId())))
                                            .collect(Collectors.toList());
                    favouriteMovies.setMovies(movies);
                    return favouriteMovies;
                })
                .switchIfEmpty(Mono.error(new RuntimeException()))
                .flatMap(favouriteMoviesRepository::save);
    }

    public Mono<FavouriteMovies> createFavouriteList(String userId) {
        FavouriteMovies favouriteMovies = new FavouriteMovies();
        favouriteMovies.setUserId(userId);
        log.info("Saving init movies " + userId);
        return favouriteMoviesRepository.save(favouriteMovies)
                .doOnError(throwable -> {
                    log.error("Exception " + throwable.getMessage());
                    throw new FavouritesException(500, "Internal server error!");
                });
    }

    public Mono<FavouriteMoviesResponse> getFavouriteMovies(String userId, Optional<String> ids) {

        FavouriteMoviesResponse response = new FavouriteMoviesResponse();

        return userIntegrationService.isUserLoggedIn(userId)
                .flatMap(loginStatus -> {
                    if (loginStatus)    {
                        List<String> idList = ( ids.isPresent()) ? getParamList(ids.get(), ",") : new ArrayList<>();

                        log.info("idList " + idList);

                        response.setUserId(userId);
                        return getMovies(userId)
                                .flatMap(favouriteMovies -> {
                                    log.info("favourite movies " + favouriteMovies);

                                    response.getFavouriteMovies().addAll(favouriteMovies.getMovies()
                                            .stream()
                                            .filter(movie -> isMovieRequested(movie, idList))
                                            .collect(Collectors.toList()));
                                    return Mono.just(response);

                                })
                                .defaultIfEmpty(response);
                    } else  {
                        return Mono.just(response);
                    }
                })
                .onErrorReturn(response);

    }

    private boolean isMovieRequested(Movie movie, List<String> idList) {
        return idList.isEmpty() || idList.contains(movie.getId());
    }

}
