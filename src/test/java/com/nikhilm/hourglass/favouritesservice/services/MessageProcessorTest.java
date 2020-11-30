package com.nikhilm.hourglass.favouritesservice.services;

import static org.junit.jupiter.api.Assertions.*;

import com.nikhilm.hourglass.favouritesservice.exceptions.FavouritesException;
import com.nikhilm.hourglass.favouritesservice.models.Event;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MessageProcessorTest {

    @Mock
    SyncService syncService;

    @InjectMocks
    MessageProcessor messageProcessor;

    @Test
    public void testProcessEvent()  {

       Event event = new Event<String, Object>(Event.Type.USER_ADDED, "abc",
                Optional.empty());

        Mockito.doNothing().when(syncService).initializeUser("abc");
        messageProcessor.processUserEvents(event);
        verify(syncService, times(1)).initializeUser(anyString());

    }

    @Test
    public void testProcessWithEventData() {
        Event event = new Event<String, Object>(Event.Type.USER_ADDED, "abc",
                Optional.of(new Object()));
        Mockito.doNothing().when(syncService).initializeUser("abc");
        messageProcessor.processUserEvents(event);

        verify(syncService, times(1)).initializeUser(anyString());

    }
    @Test
    public void testEventModel() {
        Event e = new Event();
        assertNull(e.getKey());
        assertNull(e.getEventCreatedAt());

    }

}
