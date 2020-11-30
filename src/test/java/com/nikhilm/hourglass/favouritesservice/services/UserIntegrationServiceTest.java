package com.nikhilm.hourglass.favouritesservice.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyExtractor;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.booleanThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Slf4j
class UserIntegrationServiceTest {

    @InjectMocks
    UserIntegrationService userIntegrationService;

    @Mock
    WebClient webClient;

    @Test
    void testIsUserLoggedIn() {
        boolean loginStatus = true;
        ObjectMapper mapper = new ObjectMapper();
        String body = "";
        try {
            body =  mapper.writeValueAsString(loginStatus);
        } catch (
                JsonProcessingException e) {
            log.error("Cannot parse");
        }
        WebClient.RequestHeadersUriSpec requestHeadersUriSpecMock
                = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec requestHeadersSpecMock = mock(WebClient.RequestHeadersSpec.class);
        ClientResponse clientResponse = ClientResponse.create(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(body).build();

        WebClient.ResponseSpec responseSpecMock = mock(WebClient.ResponseSpec.class);


        when(webClient.get()).thenReturn(requestHeadersUriSpecMock);
        when(requestHeadersUriSpecMock.uri(anyString()))
                .thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(Boolean.class)).thenReturn(Mono.just(loginStatus));

        StepVerifier.create(userIntegrationService.isUserLoggedIn("abc"))
                .expectSubscription()
                .expectNext(true)
                .verifyComplete();

    }
}