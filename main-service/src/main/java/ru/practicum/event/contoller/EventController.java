package ru.practicum.event.contoller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.SortEnum;
import ru.practicum.comment.CommentDtoOut;
import ru.practicum.comments.service.CommentService;
import ru.practicum.event.EventFullDto;
import ru.practicum.event.EventShortDto;
import ru.practicum.event.service.EventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;
    private final CommentService commentService;

    @GetMapping
    public List<EventShortDto> getEventsWithFiltration(@RequestParam(required = false) String text,
                                                       @RequestParam(required = false) List<Long> categories,
                                                       @RequestParam(required = false) Boolean paid,
                                                       @RequestParam(required = false) String rangeStart,
                                                       @RequestParam(required = false) String rangeEnd,
                                                       @RequestParam(required = false, defaultValue = "false") Boolean onlyAvailable,
                                                       @RequestParam(required = false) SortEnum sort,
                                                       @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                       @RequestParam(defaultValue = "10") @Positive int size,
                                                       HttpServletRequest request
    ) {
        log.info("Переданные параметры: text {}, categories {}, paid {}, rangeStart {}, rangeEnd {}, onlAvailable {}," +
                " sort {}, from {}, size {}", text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
        log.info("client ip: {}", request.getRemoteAddr());
        log.info("endpoint path: {}", request.getRequestURI());
        return eventService.getEventsWithFiltration(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size, request);
    }

    @GetMapping("/{id}")
    public EventFullDto getEventByIdPublic(@PathVariable long id, HttpServletRequest request) {
        log.info("eventId {}", id);
        log.info("client ip: {}", request.getRemoteAddr());
        log.info("endpoint path: {}", request.getRequestURI());
        return eventService.getEventByIdPublic(id, request);
    }


    @GetMapping("/{eventId}/comments/{userId}")
    public List<CommentDtoOut> getAllCommentsForCurrentEvent(@PathVariable long eventId,
                                                             @PathVariable long userId,
                                                             @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                             @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Переданный праметр eventId {}, userId, {}, from {}, size {}", eventId, userId, from, size);
        return commentService.getAllCommentsForCurrentEvent(eventId, userId, from, size);
    }

}
