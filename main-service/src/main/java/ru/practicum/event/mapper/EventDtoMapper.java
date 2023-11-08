package ru.practicum.event.mapper;

import ru.practicum.State;
import ru.practicum.category.mapper.CategoryDtoMapper;
import ru.practicum.category.model.Category;
import ru.practicum.event.EventFullDto;
import ru.practicum.event.EventShortDto;
import ru.practicum.event.NewEventDto;
import ru.practicum.event.model.Event;
import ru.practicum.location.Location;
import ru.practicum.user.mapper.UserDtoMapper;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EventDtoMapper {
    public final static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static Event toEvent(NewEventDto newEventDto, Category category, User user) {
        return Event.builder()
                .description(newEventDto.getDescription())
                .createdOn(LocalDateTime.now())
                .eventDate(LocalDateTime.parse(newEventDto.getEventDate(), FORMATTER))
                .annotation(newEventDto.getAnnotation())
                .category(category)
                .confirmedRequests(0)
                .initiator(user)
                .latitude(newEventDto.getLocation().getLat())
                .longitude(newEventDto.getLocation().getLon())
                .paid(newEventDto.getPaid() != null ? newEventDto.getPaid() : false)
                .participantLimit(newEventDto.getParticipantLimit())
                .publishedOn(LocalDateTime.now())
                .requestModeration(newEventDto.getRequestModeration() != null ? newEventDto.getRequestModeration() : true)
                .state(State.PENDING)
                .title(newEventDto.getTitle())
                .views(0)
                .build();
    }

    public static EventFullDto toEventFullDto(Event event) {
        return EventFullDto.builder()
                .id((int) event.getId())
                .description(event.getDescription())
                .createdOn(FORMATTER.format(event.getCreatedOn()))
                .eventDate(FORMATTER.format(event.getEventDate()))
                .annotation(event.getAnnotation())
                .category(CategoryDtoMapper.toCategoryDto(event.getCategory()))
                .confirmedRequests(event.getConfirmedRequests())
                .initiator(UserDtoMapper.toUserShortDto(event.getInitiator()))
                .location(new Location(event.getLatitude(), event.getLongitude()))
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(FORMATTER.format(event.getPublishedOn()))
                .requestModeration(event.getRequestModeration())
                .state(event.getState().name())
                .title(event.getTitle())
                .views(event.getViews())
                .build();
    }

    public static EventFullDto toFullDtoWithViews(Event event, Long veiws) {
        return EventFullDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(CategoryDtoMapper.toCategoryDto(event.getCategory()))
                .confirmedRequests(event.getConfirmedRequests())
                .createdOn(event.getCreatedOn().format(FORMATTER))
                .description(event.getDescription())
                .eventDate(event.getEventDate().format(FORMATTER))
                .initiator(UserDtoMapper.toUserShortDto(event.getInitiator()))
                .location(new Location(event.getLatitude(), event.getLongitude()))
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedOn() != null ? event.getPublishedOn().format(FORMATTER) : null)
                .requestModeration(event.getRequestModeration())
                .state(event.getState().name())
                .title((event.getTitle()))
                .views(veiws)
                .build();
    }

    public static EventShortDto toEventShortDto(Event event) {
        return EventShortDto.builder()
                .id((int) event.getId())
                .annotation(event.getAnnotation())
                .category(CategoryDtoMapper.toCategoryDto(event.getCategory()))
                .confirmedRequests(event.getConfirmedRequests())
                .eventDate(FORMATTER.format(event.getEventDate()))
                .initiator(UserDtoMapper.toUserShortDto(event.getInitiator()))
                .paid(event.getPaid())
                .title(event.getTitle())
                .views(event.getViews())
                .build();
    }

    public static Event toEventForEventFull(EventFullDto eventFullDto, Category category, User user) {
        return Event.builder()
                .description(eventFullDto.getDescription())
                .createdOn(LocalDateTime.now())
                .eventDate(LocalDateTime.parse(eventFullDto.getEventDate(), FORMATTER))
                .annotation(eventFullDto.getAnnotation())
                .category(category)
                .confirmedRequests(eventFullDto.getConfirmedRequests())
                .initiator(user)
                .latitude(eventFullDto.getLocation().getLat())
                .longitude(eventFullDto.getLocation().getLon())
                .paid(eventFullDto.getPaid())
                .participantLimit(eventFullDto.getParticipantLimit())
                .publishedOn(LocalDateTime.parse(eventFullDto.getPublishedOn(), FORMATTER))
                .requestModeration(eventFullDto.getRequestModeration())
                .state(State.valueOf(eventFullDto.getState()))
                .title(eventFullDto.getTitle())
                .views(eventFullDto.getViews())
                .build();
    }
}
