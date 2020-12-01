package com.nikhilm.hourglass.favouritesservice.services;


import com.nikhilm.hourglass.favouritesservice.models.Trivia;
import com.nikhilm.hourglass.favouritesservice.models.TriviaDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TriviaMapper {

    Trivia triviaDTOToTrivia(TriviaDTO triviaDTO);
    TriviaDTO triviaToTriviaDTO(Trivia  trivia);
}
