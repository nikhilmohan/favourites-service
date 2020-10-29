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
@Document(collection = "favouriteTrivia")
public class FavouriteTrivia {
    @Id
    String id;
    String userId;
    List<Trivia> trivia = new ArrayList<>();
}
