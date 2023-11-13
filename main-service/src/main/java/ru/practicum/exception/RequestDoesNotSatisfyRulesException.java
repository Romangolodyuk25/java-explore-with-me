package ru.practicum.exception;

public class RequestDoesNotSatisfyRulesException extends RuntimeException {
    public RequestDoesNotSatisfyRulesException(String mess) {
        super(mess);
    }
}
