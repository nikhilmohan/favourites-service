package com.nikhilm.hourglass.favouritesservice.models;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Document(collection = "favouriteMovies")
public class FavouriteMovies {
    @Id
    String id;
    String userId;
    List<Movie> movies = new ArrayList<>();
}
