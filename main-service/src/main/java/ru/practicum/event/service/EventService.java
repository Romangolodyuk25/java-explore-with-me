package ru.practicum.event.service;

import ru.practicum.SortEnum;
import ru.practicum.State;
import ru.practicum.event.EventFullDto;
import ru.practicum.event.EventShortDto;
import ru.practicum.event.NewEventDto;
import ru.practicum.request.UpdateEventAdminRequest;
import ru.practicum.request.UpdateEventUserRequest;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    EventFullDto createEvent(int userId, NewEventDto newEventDto);

    List<EventShortDto> getEvent(int userId, Integer from, Integer size);

    EventFullDto getEventByIdPrivate(int userId, int eventId);

    EventFullDto updateEventUser(long userId, long eventId, UpdateEventUserRequest updateEventUserRequest);

    List<EventFullDto> searchEvents(List<Long> users, List<State> states, List<Long> categories, String rangeStart, String rangeEnd, Integer from, Integer size);

    EventFullDto updateEventAdmin(long eventId, UpdateEventAdminRequest adminRequest);

    List<EventShortDto> getEventsWithFiltration(String text, List<Long> categories, Boolean paid, String rangeStart,
                                                String rangeEnd, Boolean onlyAvailable, SortEnum sort,
                                                Integer from, Integer size, HttpServletRequest request);

    EventFullDto getEventByIdPublic(Long id, HttpServletRequest request);
}
