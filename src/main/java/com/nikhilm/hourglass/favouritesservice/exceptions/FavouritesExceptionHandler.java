package com.nikhilm.hourglass.favouritesservice.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class FavouritesExceptionHandler {

    @ExceptionHandler(FavouritesException.class)
    public ResponseEntity<ApiError> handleGoalException(FavouritesException e) {
        log.error("Exception " + e.getMessage());
        return ResponseEntity.status(e.getStatus()).body(new ApiError(String.valueOf(e.getStatus()), e.getMessage()));
    }




}
