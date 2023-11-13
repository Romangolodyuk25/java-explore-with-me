package ru.practicum.event;

import lombok.*;
import org.hibernate.validator.constraints.Length;
import ru.practicum.location.Location;

import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
public class NewEventDto {
    @NotNull
    @Length(min = 20, max = 2000)
    String annotation;

    @NotNull
    Long category;

    @Length(min = 20, max = 7000)
    @NotNull
    String description;

    @NotNull
    String eventDate;

    @NotNull
    Location location;

    Boolean paid;

    int participantLimit;

    Boolean requestModeration;

    @NotNull
    @Length(min = 3, max = 120)
    String title;
}
