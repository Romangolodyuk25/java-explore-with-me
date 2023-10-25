package ru.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.StatsDtoRequest;
import ru.practicum.model.Hit;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@UtilityClass
public class StatsDtoMapper {
    public static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static Hit toHit(StatsDtoRequest statsDtoRequest) {
        return Hit.builder()
                .app(statsDtoRequest.getApp())
                .ip(statsDtoRequest.getIp())
                .uri(statsDtoRequest.getUri())
                .timestamp(LocalDateTime.parse(statsDtoRequest.getTimestamp(), FORMAT))
                .build();
    }

    public static StatsDtoRequest toStatsDtoRequest(Hit hit) {
        return StatsDtoRequest.builder()
                .app(hit.getApp())
                .ip(hit.getIp())
                .uri(hit.getUri())
                .timestamp(FORMAT.format(hit.getTimestamp()))
                .build();
    }
}
