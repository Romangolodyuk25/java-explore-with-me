package ru.practicum.exception;

public class CompilationNotExistException extends RuntimeException {
    public CompilationNotExistException(String mess) {
        super(mess);
    }
}
