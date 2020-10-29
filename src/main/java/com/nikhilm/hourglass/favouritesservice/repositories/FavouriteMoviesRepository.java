package com.nikhilm.hourglass.favouritesservice.repositories;

import com.nikhilm.hourglass.favouritesservice.models.FavouriteMovies;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface FavouriteMoviesRepository extends ReactiveMongoRepository<FavouriteMovies, String> {

    public Mono<FavouriteMovies> findByUserId(String userId);
}
