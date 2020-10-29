package com.nikhilm.hourglass.favouritesservice.services;

import com.nikhilm.hourglass.favouritesservice.models.FavouriteMovies;
import com.nikhilm.hourglass.favouritesservice.models.FavouriteTrivia;
import com.nikhilm.hourglass.favouritesservice.models.Movie;
import com.nikhilm.hourglass.favouritesservice.models.Trivia;
import com.nikhilm.hourglass.favouritesservice.repositories.FavouriteMoviesRepository;
import com.nikhilm.hourglass.favouritesservice.repositories.FavouriteTriviaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FavouriteTriviaService {

    @Autowired
    FavouriteTriviaRepository favouriteTriviaRepository;

    public Mono<FavouriteTrivia> getTrivia(String userId) {
        return favouriteTriviaRepository.findByUserId(userId);

    }

    public Mono<FavouriteTrivia> addTriviaAsFavourite(String userId, Trivia trivia) {
        return favouriteTriviaRepository.findByUserId(userId)
                .map(favouriteTrivia -> {
                    favouriteTrivia.getTrivia().add(trivia);
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
        return favouriteTriviaRepository.save(favouriteTrivia);
    }

}
