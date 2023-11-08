package ru.practicum.exception;

public class CompilationDoesNotSatisfyRulesException extends RuntimeException {
    public CompilationDoesNotSatisfyRulesException(String mess) {
    super(mess);
    }

}
