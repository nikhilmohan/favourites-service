package com.nikhilm.hourglass.favouritesservice.repositories;

import com.nikhilm.hourglass.favouritesservice.models.FavouriteMovies;
import com.nikhilm.hourglass.favouritesservice.models.FavouriteTrivia;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface FavouriteTriviaRepository extends ReactiveMongoRepository<FavouriteTrivia, String> {

    public Mono<FavouriteTrivia> findByUserId(String userId);
}
