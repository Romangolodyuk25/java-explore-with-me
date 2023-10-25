package ru.practicum;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@Builder
public class StatsDtoRequest {
    @NotNull
    String app;

    @NotNull
    String uri;

    @NotNull
    String ip;

    @NotNull
    String timestamp;
}
