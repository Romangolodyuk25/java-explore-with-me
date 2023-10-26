package ru.practicum.model;

import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ViewStats {
    @NotNull
    String app;

    @NotNull
    String uri;

    @NotNull
    int hits;
}
