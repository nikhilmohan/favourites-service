package com.nikhilm.hourglass.favouritesservice.models;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class FavouritesResponse {
    private String userId;
    private List<Movie> favouriteMovies = new ArrayList<>();
    private List<Trivia> favouriteTrivia = new ArrayList<>();
}
