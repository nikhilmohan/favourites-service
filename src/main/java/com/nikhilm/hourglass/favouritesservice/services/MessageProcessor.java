package com.nikhilm.hourglass.favouritesservice.services;

import com.nikhilm.hourglass.favouritesservice.models.Event;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.MessageChannel;


@EnableBinding(MessageProcessor.MessageSink.class)
@Slf4j
public class MessageProcessor {

    private final SyncService syncService;

    @Autowired
    public MessageProcessor(SyncService syncService) {
        this.syncService = syncService;
    }


    @StreamListener(target = MessageSink.INPUT_USERS)
    public void processUserEvents(Event<String, Object> event) {

        log.info("Process message created at {}...", event.getEventCreatedAt());

        switch (event.getEventType()) {

            case USER_ADDED:
                log.info("Added user with ID: {}", event.getKey());
                syncService.initializeUser(event.getKey());
                break;
            default:
                String errorMessage = "Incorrect event type: " + event.getEventType() + ", expected a relevant goal event";
                log.warn(errorMessage);
                throw new RuntimeException(errorMessage);
        }

        log.info("Message processing done!");
    }
    public interface MessageSink {

        String INPUT_USERS = "input-users";


        @Input(INPUT_USERS)
        MessageChannel inputUsers();


    }
}
