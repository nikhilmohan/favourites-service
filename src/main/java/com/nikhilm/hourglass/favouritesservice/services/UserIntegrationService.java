package com.nikhilm.hourglass.favouritesservice.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class UserIntegrationService {

    @Autowired
    WebClient webClient;

    public Mono<Boolean> isUserLoggedIn(String userId)    {

        return webClient.
                get().uri("http://localhost:9900/user-service/user/" + userId + "/status")
                .retrieve()
                .bodyToMono(Boolean.class);



    }
}
