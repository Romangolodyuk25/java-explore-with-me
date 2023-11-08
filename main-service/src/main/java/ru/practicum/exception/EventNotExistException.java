package ru.practicum.exception;

public class EventNotExistException extends RuntimeException {
    public EventNotExistException(String mess) {
        super(mess);
    }
}
