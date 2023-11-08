package ru.practicum.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.category.CategoryDto;
import ru.practicum.user.UserShortDto;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class EventShortDto {
    long id;
    String annotation;
    CategoryDto category;
    int confirmedRequests;
    String eventDate;
    UserShortDto initiator;
    Boolean paid;
    String title;
    long views;
}
