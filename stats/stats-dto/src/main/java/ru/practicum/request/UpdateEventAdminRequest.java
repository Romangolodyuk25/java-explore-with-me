package ru.practicum.request;

import lombok.*;
import org.hibernate.validator.constraints.Length;
import ru.practicum.location.Location;

@Data
@Builder
@AllArgsConstructor
public class UpdateEventAdminRequest {
    @Length(min = 20, max = 2000)
    String annotation;
    Integer category;
    @Length(min = 20, max = 7000)
    String description;
    String eventDate;
    Location location;
    Boolean paid;
    Integer participantLimit;
    Boolean requestModeration;
    String stateAction;
    @Length(min = 3, max = 120)
    String title;
}
