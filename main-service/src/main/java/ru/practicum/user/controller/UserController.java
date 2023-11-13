package ru.practicum.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.EventFullDto;
import ru.practicum.event.EventShortDto;
import ru.practicum.event.NewEventDto;
import ru.practicum.event.service.EventService;
import ru.practicum.request.EventRequestStatusUpdateRequest;
import ru.practicum.request.EventRequestStatusUpdateResult;
import ru.practicum.request.ParticipationRequestDto;
import ru.practicum.request.UpdateEventUserRequest;
import ru.practicum.request.service.RequestService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/users")
public class UserController {

    private final EventService eventService;
    private final RequestService requestService;

    @PostMapping("/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(@PathVariable long userId,
                                    @Valid @RequestBody NewEventDto newEventDto) {
        log.info("id Пользователя {}, объект для создания {}", userId, newEventDto);
        return eventService.createEvent(userId, newEventDto);
    }

    @GetMapping("/{userId}/events")
    public List<EventShortDto> getEvent(@PathVariable long userId,
                                        @RequestParam(required = false, defaultValue = "0") Integer from,
                                        @RequestParam(required = false, defaultValue = "10") Integer size
                                  ) {
        log.info("Айди юзера {}, from {}, size {}", userId, from, size);
        return eventService.getEvent(userId, from, size);
    }

    @GetMapping("/{userId}/events/{eventId}")
    public EventFullDto getEventById(@PathVariable long userId, @PathVariable long eventId) {
        log.info("id пользователя {}, eventId {}", userId, eventId);
        return eventService.getEventByIdPrivate(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}")
    public EventFullDto updateEvent(@PathVariable long userId,
                                    @PathVariable long eventId,
                                    @Valid @RequestBody UpdateEventUserRequest updateEventUserRequest) {
        log.info("id пользователя {}, eventId {}, объект для обновления {}", userId, eventId, updateEventUserRequest);
        return eventService.updateEventUser(userId, eventId, updateEventUserRequest);
    }

    @PostMapping("/{userId}/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto createRequest(@PathVariable long userId,
                                                 @RequestParam long eventId) {
        log.info("userId {}, eventId {}", userId, eventId);
        return requestService.createRequest(userId, eventId);
    }

    @GetMapping("/{userId}/requests")
    public List<ParticipationRequestDto> getRequestForUserOtherEvent(@PathVariable long userId) {
        return requestService.getRequestsForUserOtherEvent(userId);
    }

    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    public ParticipationRequestDto cancelRequest(@PathVariable long userId, @PathVariable long requestId) {
       return requestService.cancelRequest(userId, requestId);
    }


    @GetMapping("/{userId}/events/{eventId}/requests")
    public List<ParticipationRequestDto> getRequestsForCurrentUser(@PathVariable long userId, @PathVariable long eventId) {
        log.info("id пользователя {}, eventId {}", userId, eventId);
        return requestService.getRequestsForCurrentUser(userId, eventId);
    }


    @PatchMapping("/{userId}/events/{eventId}/requests")
    public EventRequestStatusUpdateResult updateStatusForCurrentUser(@RequestBody EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest,
                                                                     @PathVariable long userId,
                                                                     @PathVariable long eventId) {
        log.info("id пользователя {}, eventId {}", userId, eventId);
        return requestService.updateStatusForCurrentUser(eventRequestStatusUpdateRequest, userId, eventId);
    }
}
