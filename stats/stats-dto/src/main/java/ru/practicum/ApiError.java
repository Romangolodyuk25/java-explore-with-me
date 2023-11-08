package ru.practicum;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ApiError {
    String message;
    String reason;
    String status;
    String timestamp;
}
