package ru.practicum.exception;

public class RequestNotExistException extends RuntimeException {
    public RequestNotExistException(String mess) {
        super(mess);
    }
}
