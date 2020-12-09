package com.nikhilm.hourglass.favouritesservice.resource;

import com.nikhilm.hourglass.favouritesservice.exceptions.ApiError;
import com.nikhilm.hourglass.favouritesservice.exceptions.FavouritesException;
import com.nikhilm.hourglass.favouritesservice.models.*;
import com.nikhilm.hourglass.favouritesservice.services.FavouriteMovieService;
import com.nikhilm.hourglass.favouritesservice.services.FavouriteTriviaService;
import com.nikhilm.hourglass.favouritesservice.services.SyncService;
import com.nikhilm.hourglass.favouritesservice.services.TriviaMapper;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;
import org.springframework.cloud.client.loadbalancer.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@Slf4j
public class FavouriteResource {

    @Autowired
    FavouriteMovieService favouriteMovieService;

    @Autowired
    FavouriteTriviaService favouriteTriviaService;

    @Autowired
    ReactiveCircuitBreakerFactory factory;

    @Autowired
    TriviaMapper triviaMapper;


    ReactiveCircuitBreaker rcb;

    public FavouriteResource(ReactiveCircuitBreakerFactory factory) {
        this.factory = factory;
        this.rcb = factory.create("favourites");
    }


    @GetMapping("/favourites/user/{userId}/movies")
    public Mono<FavouriteMoviesResponse> getFavouriteMovies(@PathVariable("userId") String userId,
                                                    @RequestParam("ids") Optional<String> ids)   {
        return favouriteMovieService.getFavouriteMovies(userId, ids);

    }

    @GetMapping("/favourites/user/{userId}/trivia")
    public Mono<FavouriteTriviaResponse> getFavouriteTrivia(@PathVariable("userId") String userId,
                                                            @RequestParam("terms") Optional<String> terms)   {
        return favouriteTriviaService.getFavouriteTrivia(userId, terms);
    }


    @PutMapping("/favourites/user/{userId}/movie")
    public Mono<ResponseEntity<Movie>> updateMovieAsFavourite(@PathVariable("userId") String userId, @RequestBody Movie movie,
                                                              @RequestHeader("user") String user) {
        if (!userId.equalsIgnoreCase(user)) {
            return Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN).build());
        }
        return favouriteMovieService.updateMovieAsFavourite(userId, movie)
                .map(favouriteMovies -> {
                    log.info("saved fav movies " + favouriteMovies);
                    return ResponseEntity.ok(movie);
                })
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping("/favourites")
    public Mono<ResponseEntity<Object>> createFavouriteMovieList(@RequestHeader("user") String userId) {

        return Flux.merge(favouriteMovieService.createFavouriteList(userId)
                , favouriteTriviaService.createFavouriteList(userId))
                .then(Mono.just(ResponseEntity.ok().build()))
                .onErrorReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new ApiError("500", "Server error!")));

    }

    @GetMapping("/favourites")
    public Mono<FavouritesResponse> getFavourites(@RequestHeader("user") String userId) {
        return Mono.zip(favouriteMovieService.getFavouriteMovies(userId, Optional.empty()),
                favouriteTriviaService.getFavouriteTrivia(userId, Optional.empty()),
                ((favouriteMoviesResponse, favouriteTriviaResponse)
                        -> {
                                FavouritesResponse response = new FavouritesResponse();
                                response.setUserId(userId);
                                response.getFavouriteMovies().addAll(favouriteMoviesResponse.getFavouriteMovies());
                                response.getFavouriteTrivia().addAll(favouriteTriviaResponse.getFavouriteTrivia());
                                return response;}));
    }

    @PutMapping("/favourites/user/{userId}/trivia")
    public Mono<ResponseEntity<Trivia>> updateTriviaAsFavourite(@PathVariable("userId") String userId,
                                                                @RequestBody TriviaDTO triviaDTO,
                                                                @RequestHeader("user") String user) {
        if (!userId.equalsIgnoreCase(user)) {
            return Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN).build());
        }
        return favouriteTriviaService.updateTriviaAsFavourite(userId, triviaMapper.triviaDTOToTrivia(triviaDTO))
                .map(favouriteTrivia -> ResponseEntity.ok(triviaMapper.triviaDTOToTrivia(triviaDTO)))
                .defaultIfEmpty(ResponseEntity.notFound().build());

    }


}
