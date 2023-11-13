package ru.practicum.handler;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorResponse {
    String message;
    String description;
    String stackTrace;

    public ErrorResponse(String mess, String description) {
        this.message = mess;
        this.description = description;
    }
}
