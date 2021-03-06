package com.nikhilm.hourglass.favouritesservice.models;

import java.time.LocalDateTime;
import java.util.Optional;

import static java.time.LocalDateTime.now;

public class Event<K, T> {

    public enum Type {USER_ADDED}

    private Type eventType ;
    private K key;
    private Optional<T> data;
    private LocalDateTime eventCreatedAt;

    public Event() {
        this.eventType = null;
        this.key = null;
        this.data = Optional.empty();
        this.eventCreatedAt = null;
    }

    public Event(Type eventType, K key, Optional<T> data) {
        this.eventType = eventType;
        this.key = key;
        this.data = data;
        this.eventCreatedAt = now();
    }

    public Type getEventType() {
        return eventType;
    }

    public K getKey() {
        return key;
    }

    public Optional<T> getData() {
        return data;
    }

    public LocalDateTime getEventCreatedAt() {
        return eventCreatedAt;
    }
}
