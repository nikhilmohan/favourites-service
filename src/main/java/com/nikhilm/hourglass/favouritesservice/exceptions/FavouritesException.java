package com.nikhilm.hourglass.favouritesservice.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FavouritesException extends RuntimeException{
    private int status;
    private String message;
}
