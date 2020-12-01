package com.nikhilm.hourglass.favouritesservice.models;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TriviaDTO {

    String id;
    private String term;
    private String fact;
    private String category;
}
