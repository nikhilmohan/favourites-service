package com.nikhilm.hourglass.favouritesservice.models;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Movie {
    String id;
    String name;
    String description;
    String poster;
}
