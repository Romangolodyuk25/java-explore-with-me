package ru.practicum;

import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatsDtoResponse {
    @NotNull
    String app;

    @NotNull
    String uri;

    @NotNull
    long hits;
}
