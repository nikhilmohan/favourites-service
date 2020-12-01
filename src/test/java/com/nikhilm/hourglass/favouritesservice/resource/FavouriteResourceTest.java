package com.nikhilm.hourglass.favouritesservice.resource;

import com.nikhilm.hourglass.favouritesservice.exceptions.ApiError;
import com.nikhilm.hourglass.favouritesservice.models.*;
import com.nikhilm.hourglass.favouritesservice.services.FavouriteMovieService;
import com.nikhilm.hourglass.favouritesservice.services.FavouriteTriviaService;
import com.nikhilm.hourglass.favouritesservice.services.TriviaMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@WebFluxTest
class FavouriteResourceTest {

    @MockBean
    FavouriteMovieService favouriteMovieService;

    @MockBean
    FavouriteTriviaService favouriteTriviaService;

    @Autowired
    WebTestClient webTestClient;

    @MockBean
    TriviaMapper triviaMapper;

    @Test
    public void testGetFavouriteMovies()    {

        FavouriteMoviesResponse favouriteMovieResponse = new FavouriteMoviesResponse();

        favouriteMovieResponse.setUserId("abc");

        Mockito.when(favouriteMovieService.getFavouriteMovies("abc", Optional.empty()))
                .thenReturn(Mono.just(favouriteMovieResponse));
        FavouriteMoviesResponse response = webTestClient.get().uri("http://localhost:9040/favourites/user/abc/movies")
                .exchange()
                .expectBody(FavouriteMoviesResponse.class)
                .returnResult()
                .getResponseBody();

        assertEquals("abc", response.getUserId());

    }
    @Test
    public void testGetFavouriteTrivia()    {

        FavouriteTriviaResponse fav = new FavouriteTriviaResponse();

        fav.setUserId("abc");

        Mockito.when(favouriteTriviaService.getFavouriteTrivia("abc", Optional.empty()))
                .thenReturn(Mono.just(fav));
        FavouriteMoviesResponse response = webTestClient.get().uri("http://localhost:9040/favourites/user/abc/trivia")
                .exchange()
                .expectBody(FavouriteMoviesResponse.class)
                .returnResult()
                .getResponseBody();

        assertEquals("abc", response.getUserId());

    }

    @Test
    public void testCreateFavouriteList()   {
        Mockito.when(favouriteMovieService.createFavouriteList("abc"))
                .thenReturn(Mono.just(new FavouriteMovies()));
        Mockito.when(favouriteTriviaService.createFavouriteList("abc"))
                .thenReturn(Mono.just(new FavouriteTrivia()));

        webTestClient.post().uri("http://localhost:9040/favourites")
                .header("user", "abc")
                .exchange()
                .expectStatus().is2xxSuccessful();
    }
    @Test
    public void testCreateFavouriteListError()   {
        Mockito.when(favouriteMovieService.createFavouriteList("abc"))
                .thenReturn(Mono.just(new FavouriteMovies()));
        Mockito.when(favouriteTriviaService.createFavouriteList("abc"))
                .thenReturn(Mono.error(new RuntimeException()));

        ApiError apiError = webTestClient.post().uri("http://localhost:9040/favourites")
                .header("user", "abc")
                .exchange()
                .expectBody(ApiError.class)
                .returnResult()
                .getResponseBody();

        assertEquals("Server error!", apiError.getMessage());

    }

    @Test
    public void testGetFavourites() {
        FavouriteTriviaResponse favouriteTriviaResponse = new FavouriteTriviaResponse();
        favouriteTriviaResponse.setUserId("abc");

        FavouriteMoviesResponse favouriteMovieResponse = new FavouriteMoviesResponse();
        favouriteMovieResponse.setUserId("abc");

        Mockito.when(favouriteMovieService.getFavouriteMovies("abc", Optional.empty()))
                .thenReturn(Mono.just(favouriteMovieResponse));
        Mockito.when(favouriteTriviaService.getFavouriteTrivia("abc", Optional.empty()))
                .thenReturn(Mono.just(favouriteTriviaResponse));

        FavouritesResponse response = webTestClient.get().uri("http://localhost:9040/favourites")
                .header("user", "abc")
                .exchange()
                .expectBody(FavouritesResponse.class)
                .returnResult()
                .getResponseBody();

        assertEquals("abc", response.getUserId());
        assertNotNull(response.getFavouriteMovies());
        assertNotNull(response.getFavouriteTrivia());

    }

    @Test
    public void testUpdateTrivia()   {
        Trivia trivia = new Trivia();
        trivia.setCategory("science");
        trivia.setTerm("astrophysics");
        trivia.setFact("space and stuff!");

        FavouriteTrivia favouriteTrivia = new FavouriteTrivia();
        favouriteTrivia.getTrivia().add(trivia);
        favouriteTrivia.setUserId("abc");

        Mockito.when(triviaMapper.triviaDTOToTrivia(any(TriviaDTO.class))).thenReturn(trivia);
        Mockito.when(favouriteTriviaService.updateTriviaAsFavourite(eq("abc"), any(Trivia.class)))
                .thenReturn(Mono.just(favouriteTrivia));

        webTestClient.put().uri("http://localhost:9040/favourites/user/abc/trivia")
                .header("user", "abc")
                .body(Mono.just(trivia), Trivia.class)
                .exchange()
                .expectStatus()
                .is2xxSuccessful();

    }
    @Test
    public void testUpdateTriviaAccessDenied()   {
        Trivia trivia = new Trivia();
        trivia.setCategory("science");
        trivia.setTerm("astrophysics");
        trivia.setFact("space and stuff!");

        FavouriteTrivia favouriteTrivia = new FavouriteTrivia();
        favouriteTrivia.getTrivia().add(trivia);
        favouriteTrivia.setUserId("abc");
        Mockito.when(triviaMapper.triviaDTOToTrivia(any(TriviaDTO.class))).thenReturn(trivia);

        Mockito.when(favouriteTriviaService.updateTriviaAsFavourite(eq("abc"), any(Trivia.class)))
                .thenReturn(Mono.just(favouriteTrivia));

        webTestClient.put().uri("http://localhost:9040/favourites/user/abc/trivia")
                .header("user", "xyz")
                .body(Mono.just(trivia), Trivia.class)
                .exchange()
                .expectStatus()
                .is4xxClientError();

    }

    @Test
    public void testUpdateTriviaNotFound()   {
        Trivia trivia = new Trivia();
        trivia.setCategory("science");
        trivia.setTerm("astrophysics");
        trivia.setFact("space and stuff!");

        FavouriteTrivia favouriteTrivia = new FavouriteTrivia();
        favouriteTrivia.getTrivia().add(trivia);
        favouriteTrivia.setUserId("abc");

        Mockito.when(triviaMapper.triviaDTOToTrivia(any(TriviaDTO.class))).thenReturn(trivia);

        Mockito.when(favouriteTriviaService.updateTriviaAsFavourite(eq("abc"), any(Trivia.class)))
                .thenReturn(Mono.empty());

        webTestClient.put().uri("http://localhost:9040/favourites/user/abc/trivia")
                .header("user", "abc")
                .body(Mono.just(trivia), Trivia.class)
                .exchange()
                .expectStatus()
                .isNotFound();

    }
    @Test
    public void testUpdateMovies()   {
        Movie movie = new Movie();
        movie.setDescription("A new movie");
        movie.setName("Some");

        FavouriteMovies favouriteMovies = new FavouriteMovies();
        favouriteMovies.getMovies().add(movie);
        favouriteMovies.setUserId("abc");

        Mockito.when(favouriteMovieService.updateMovieAsFavourite(eq("abc"), any(Movie.class)))
                .thenReturn(Mono.just(favouriteMovies));

        webTestClient.put().uri("http://localhost:9040/favourites/user/abc/movie")
                .header("user", "abc")
                .body(Mono.just(movie), Movie.class)
                .exchange()
                .expectStatus()
                .is2xxSuccessful();

    }
    @Test
    public void testUpdateMovieAccessDenied()   {
        Movie movie = new Movie();
        movie.setDescription("A new movie");
        movie.setName("Some");

        FavouriteMovies favouriteMovies = new FavouriteMovies();
        favouriteMovies.getMovies().add(movie);
        favouriteMovies.setUserId("abc");

        Mockito.when(favouriteMovieService.updateMovieAsFavourite(eq("abc"), any(Movie.class)))
                .thenReturn(Mono.just(favouriteMovies));

        webTestClient.put().uri("http://localhost:9040/favourites/user/abc/movie")
                .header("user", "xyz")
                .body(Mono.just(movie), Movie.class)
                .exchange()
                .expectStatus()
                .is4xxClientError();

    }

    @Test
    public void testUpdateMovieNotFound()   {
        Movie movie = new Movie();
        movie.setDescription("A new movie");
        movie.setName("Some");

        FavouriteMovies favouriteMovies = new FavouriteMovies();
        favouriteMovies.getMovies().add(movie);
        favouriteMovies.setUserId("abc");

        Mockito.when(favouriteMovieService.updateMovieAsFavourite(eq("abc"), any(Movie.class)))
                .thenReturn(Mono.empty());

        webTestClient.put().uri("http://localhost:9040/favourites/user/abc/movie")
                .header("user", "abc")
                .body(Mono.just(movie), Movie.class)
                .exchange()
                .expectStatus()
                .isNotFound();

    }

}