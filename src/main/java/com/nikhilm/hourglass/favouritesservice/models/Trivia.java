package com.nikhilm.hourglass.favouritesservice.models;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Document(collection = "favouriteTrivia")
public class Trivia {
    @Id
    String id;
    private String term;
    private String fact;
    private String category;
}
