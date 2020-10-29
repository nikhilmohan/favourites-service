package com.nikhilm.hourglass.favouritesservice.models;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class FavouriteMoviesResponse {
    String userId;
    List<Movie> favouriteMovies = new ArrayList<>();
}
