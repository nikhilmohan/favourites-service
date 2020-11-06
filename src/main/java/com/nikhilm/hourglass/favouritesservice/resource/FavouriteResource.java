package com.nikhilm.hourglass.favouritesservice.resource;

import com.nikhilm.hourglass.favouritesservice.models.*;
import com.nikhilm.hourglass.favouritesservice.services.FavouriteMovieService;
import com.nikhilm.hourglass.favouritesservice.services.FavouriteTriviaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    @GetMapping("/favourites/user/{userId}/movies")
    public Mono<FavouriteMoviesResponse> getFavouriteMovies(@PathVariable("userId") String userId,
                                                    @RequestParam("ids") Optional<String> ids)   {
        FavouriteMoviesResponse response = new FavouriteMoviesResponse();
        WebClient client = WebClient.create("http://localhost:9900/user-service/user/");
        return client.get().uri(userId + "/status")
                .retrieve()
                .bodyToMono(Boolean.class)
                .flatMap(loginStatus -> {
                    if (loginStatus)    {
                        List<String> idList = ( ids.isPresent()) ? getParamList(ids.get(), ",") : new ArrayList<>();

                        log.info("idList " + idList);

                        response.setUserId(userId);
                        return favouriteMovieService.getMovies(userId)
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
                });

    }
    @GetMapping("/favourites/user/{userId}/trivia")
    public Mono<FavouriteTriviaResponse> getFavouriteTrivia(@PathVariable("userId") String userId,
                                                            @RequestParam("terms") Optional<String> terms)   {

        FavouriteTriviaResponse response = new FavouriteTriviaResponse();
        WebClient client = WebClient.create("http://localhost:9900/user-service/user/");
        return client.get().uri(userId + "/status")
                .retrieve()
                .bodyToMono(Boolean.class)
                .flatMap(loginStatus -> {
                    if (loginStatus) {
                        List<String> termList = (terms.isPresent()) ? getParamList(terms.get(), ",") : new ArrayList<>();

                        log.info("termList " + termList);
                        response.setUserId(userId);

                        return favouriteTriviaService.getTrivia(userId)
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
                });

    }
    private List<String> getParamList(String ids, String delim)    {
        return Arrays.stream(ids.split(delim)).collect(Collectors.toList());
    }
    private boolean isMovieRequested(Movie movie, List<String> idList) {
        return idList.isEmpty() || idList.contains(movie.getId());
    }
    private boolean isTriviaRequested(Trivia trivia, List<String> idList) {
        return idList.isEmpty() || idList.contains(trivia.getTerm());
    }

    @PostMapping("/favourites/user/{userId}/movie")
    public Mono<ResponseEntity<Movie>> addMovieAsFavourite(@PathVariable("userId") String userId, @RequestBody Movie movie) {
        return favouriteMovieService.addMovieAsFavourite(userId, movie)
                .map(favouriteMovies -> ResponseEntity.ok(movie));
    }
    @DeleteMapping("/favourites/user/{userId}/movie")
    public Mono<ResponseEntity<Void>> removeMovieAsFavourite(@PathVariable("userId") String userId, @RequestBody Movie movie)    {
        return favouriteMovieService.removeMovieAsFavourite(userId, movie)
                .map(favouriteMovies -> {
                    return ResponseEntity.noContent().build();
                });
    }
    @PostMapping("/favourites/user/{userId}")
    public Mono<ResponseEntity<Void>> createFavouriteMovieList(@PathVariable("userId") String userId) {
        return Flux.merge(favouriteMovieService.createFavouriteList(userId)
                , favouriteTriviaService.createFavouriteList(userId))
                .then(Mono.just(ResponseEntity.ok().build()));

    }

    @PostMapping("/favourites/user/{userId}/trivia")
    public Mono<ResponseEntity<Trivia>> addTriviaAsFavourite(@PathVariable("userId") String userId, @RequestBody Trivia trivia) {
        return favouriteTriviaService.addTriviaAsFavourite(userId, trivia)
                .map(favouriteTrivia -> ResponseEntity.ok(trivia));
    }
    @DeleteMapping("/favourites/user/{userId}/trivia")
    public Mono<ResponseEntity<Void>> removeTriviaAsFavourite(@PathVariable("userId") String userId, @RequestBody Trivia trivia)    {
        return favouriteTriviaService.removeTriviaAsFavourite(userId, trivia)
                .map(favouriteTrivia -> {
                    return ResponseEntity.noContent().build();
                });
    }

}
