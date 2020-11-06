package com.nikhilm.hourglass.favouritesservice.services;

import com.nikhilm.hourglass.favouritesservice.models.FavouriteMovies;
import com.nikhilm.hourglass.favouritesservice.models.Movie;
import com.nikhilm.hourglass.favouritesservice.repositories.FavouriteMoviesRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FavouriteMovieService {

    @Autowired
    FavouriteMoviesRepository favouriteMoviesRepository;

    public Mono<FavouriteMovies> getMovies(String userId) {
        return favouriteMoviesRepository.findByUserId(userId);

    }

    public Mono<FavouriteMovies> addMovieAsFavourite(String userId, Movie movie) {
        return favouriteMoviesRepository.findByUserId(userId)
                .map(favouriteMovies -> {
                    favouriteMovies.getMovies().add(movie);
                    log.info("Adding favourite movie " + movie + " for user " + userId);
                    return favouriteMovies;
                })
                .switchIfEmpty(Mono.error(new RuntimeException()))
                .flatMap(favouriteMoviesRepository::save);

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
        return favouriteMoviesRepository.save(favouriteMovies);
    }
}
