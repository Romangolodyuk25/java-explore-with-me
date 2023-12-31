package ru.practicum;

import lombok.*;

import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
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
