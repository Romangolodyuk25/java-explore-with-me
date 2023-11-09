package ru.practicum.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.*;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.event.EventFullDto;
import ru.practicum.event.EventShortDto;
import ru.practicum.event.NewEventDto;
import ru.practicum.event.mapper.EventDtoMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.CategoryNotExistException;
import ru.practicum.exception.EventDoesNotSatisfyRulesException;
import ru.practicum.exception.EventNotExistException;
import ru.practicum.exception.UserNotExistException;
import ru.practicum.request.UpdateEventAdminRequest;
import ru.practicum.request.UpdateEventUserRequest;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;


    private final StatsClient statsClient;

    @Override
    public EventFullDto createEvent(int userId, NewEventDto newEventDto) {
        validationEvent(newEventDto);
        Category receivedCategory = categoryRepository.findById((long) newEventDto.getCategory()).orElseThrow(() -> new CategoryNotExistException("Category not exist"));
        User receivedUser = userRepository.findById((long) userId).orElseThrow(() -> new CategoryNotExistException("User not exist"));
        Event eventForBd = EventDtoMapper.toEvent(newEventDto, receivedCategory, receivedUser);
        return EventDtoMapper.toEventFullDto(eventRepository.save(eventForBd));
    }

    @Override
    public List<EventShortDto> getEvent(int userId, Integer from, Integer size) {
        userRepository.findById((long) userId).orElseThrow(() -> new CategoryNotExistException("User not exist"));

        Pageable page = PageRequest.of(from / size, size);
        return eventRepository.findByInitiator_Id(userId, page).stream()
                .map(EventDtoMapper::toEventShortDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto getEventByIdPrivate(int userId, int eventId) {
        userRepository.findById((long) userId).orElseThrow(() -> new CategoryNotExistException("User not exist"));

        Event receivedEvent = eventRepository.findById((long) eventId).orElseThrow(() -> new EventNotExistException("event not exist"));
        return EventDtoMapper.toEventFullDto(receivedEvent);
    }

    @Override
    public EventFullDto updateEventUser(long userId, long eventId, UpdateEventUserRequest updateEventUserRequest) {
        userRepository.findById(userId).orElseThrow(() -> new CategoryNotExistException("User not exist"));
        Event receivedEvent = eventRepository.findById(eventId).orElseThrow(() -> new EventNotExistException("event not exist"));
        validationFromUpdateEventUser(updateEventUserRequest, receivedEvent);

        Event finalEvent = updateForNewEvent(updateEventUserRequest, eventId);
        log.info("Объект обновлен на {} ", finalEvent);
        return EventDtoMapper.toEventFullDto(eventRepository.save(finalEvent));
    }

    @Override
    public List<EventFullDto> searchEvents(List<Long> users, List<State> states, List<Long> categories, String rangeStart, String rangeEnd, Integer from, Integer size) {
        Pageable page = PageRequest.of(from / size, size);

        LocalDateTime startTime = rangeStart != null ? LocalDateTime.parse(rangeStart, EventDtoMapper.FORMATTER) : null;
        LocalDateTime endTime = rangeStart != null ? LocalDateTime.parse(rangeEnd, EventDtoMapper.FORMATTER) : null;
        if (users != null && users.size() != 0)
            users.forEach(userId -> userRepository.findById(userId).orElseThrow(() -> new UserNotExistException("user not exist")));
        if (categories != null && categories.size() != 0)
            categories.forEach(categoryId -> categoryRepository.findById(categoryId).orElseThrow(() -> new CategoryNotExistException("category not exist")));
        if (users == null && states == null && categories == null && rangeStart == null && rangeEnd == null) {
            return eventRepository.findAll(page).stream()
                    .map(EventDtoMapper::toEventFullDto)
                    .collect(Collectors.toList());
        }
        return eventRepository.searchEvent(users, states, categories, startTime, endTime, page).stream()
                .map(EventDtoMapper::toEventFullDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto updateEventAdmin(long eventId, UpdateEventAdminRequest adminRequest) {
        Event receivedEvent = eventRepository.findById(eventId).orElseThrow(() -> new EventNotExistException("event not exist"));
        validationForUpdateAdminRequest(adminRequest, receivedEvent);

        Event updateEvent = updateForAdminRequest(receivedEvent, adminRequest);

        return EventDtoMapper.toEventFullDto(eventRepository.save(updateEvent));
    }

    @Override
    public List<EventShortDto> getEventsWithFiltration(String text, List<Long> categories, Boolean paid,
                                                       String rangeStart, String rangeEnd,
                                                       Boolean onlyAvailable, SortEnum sort, Integer from,
                                                       Integer size, HttpServletRequest request) {
        LocalDateTime start;
        LocalDateTime end;

        Pageable page;
        if (sort != null && sort.equals(SortEnum.EVENT_DATE)) {
            page = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "eventDate"));
        } else {
            page = PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "views"));
        }

        Page<Event> receivedEvents;

        if (text == null && paid == null && rangeStart == null && rangeEnd == null && sort == null) {
            return eventRepository.findByCategory_IdIn(categories, page).stream()
                    .map(EventDtoMapper::toEventShortDto)
                    .collect(Collectors.toList());
        }
        if (rangeStart != null && rangeEnd != null) {
            start = LocalDateTime.parse(rangeStart, EventDtoMapper.FORMATTER);
            end = LocalDateTime.parse(rangeEnd, EventDtoMapper.FORMATTER);
            if (start.isAfter(end) || end.isBefore(start)) {
                throw new ValidationException();
            }
            if (onlyAvailable) {
                receivedEvents = eventRepository.findAllEventsIsOnlyAvailable(text, categories, paid, start, end, page);
            } else {
                receivedEvents = eventRepository.findAllEventsIsNotOnlyAvailable(text, categories, paid, start, end, page);
            }
        } else {
            receivedEvents = eventRepository.findByEventWithEmptyStartDate(text, categories, paid, LocalDateTime.now(), page);
        }

        statsClient.createHit(StatsDtoRequest.builder()
                .timestamp(LocalDateTime.now().format(EventDtoMapper.FORMATTER))
                .ip(request.getRemoteAddr())
                .uri(request.getRequestURI())
                .app("/ewm-main-service")
                .build());

        return incrementViewsAndMapEvent(receivedEvents);
    }

    @Override
    public EventFullDto getEventByIdPublic(Long id, HttpServletRequest request) {
        eventRepository.findById(id).orElseThrow(() -> new EventNotExistException("Event not exist"));

        statsClient.createHit(StatsDtoRequest.builder()
                .timestamp(LocalDateTime.now().format(EventDtoMapper.FORMATTER))
                .ip(request.getRemoteAddr())
                .uri(request.getRequestURI())
                .app("/ewm-main-service")
                .build());

        List<Event> receivedEvent = eventRepository.findByIdAndState(id, State.PUBLISHED);
        if (receivedEvent.size() == 0) {
            throw new EventNotExistException("event not exist");
        }

        List<Long> ids = receivedEvent.stream()
                .map(Event::getId)
                .collect(Collectors.toList());

        Map<Long, Long> veiws = getStatsForEvents(eventRepository.getMinDate(ids), ids);
        List<EventFullDto> finalList = new ArrayList<>();
        for (Event e : receivedEvent) {
            finalList.add(EventDtoMapper.toFullDtoWithViews(e, veiws.getOrDefault(e.getId(), 0L)));
        }

        if (receivedEvent.size() == 0) throw new EventNotExistException("Event not exist");
        Category category = categoryRepository.findById((long) finalList.get(0).getCategory().getId()).orElseThrow();
        User user = userRepository.findById(finalList.get(0).getInitiator().getId()).orElseThrow();
        Event finalEvent = EventDtoMapper.toEventForEventFull(finalList.get(0), category, user);
        return EventDtoMapper.toEventFullDto(finalEvent);
    }


    private void validationEvent(NewEventDto newEventDto) {
        LocalDateTime timeEventDate = LocalDateTime.parse(newEventDto.getEventDate(), EventDtoMapper.FORMATTER);
        LocalDateTime minStartEventTime = LocalDateTime.now().plusHours(2);
        if (newEventDto.getEventDate() == null || timeEventDate.isBefore(LocalDateTime.now())
                || timeEventDate.isBefore(minStartEventTime)) {
            throw new ValidationException("validation exception");
        }
    }

    private void validationFromUpdateEventUser(UpdateEventUserRequest updateEventUserRequest, Event event) {
        LocalDateTime minStartEventTime = LocalDateTime.now().plusHours(2);
        if (event.getState().equals(State.PUBLISHED)) {
            throw new EventDoesNotSatisfyRulesException("Событие не удовлетворяет правилам редактирования");
        }
        if (updateEventUserRequest.getEventDate() != null) {
            LocalDateTime timeUpdateEvent = LocalDateTime.parse(updateEventUserRequest.getEventDate(), EventDtoMapper.FORMATTER);
            if (timeUpdateEvent.isBefore(event.getCreatedOn())) {
                throw new ValidationException();
            }
            if (timeUpdateEvent.isAfter(minStartEventTime)) {
                throw new EventDoesNotSatisfyRulesException("Событие не удовлетворяет правилам редактирования");
            }
        }
    }

    private void validationForUpdateAdminRequest(UpdateEventAdminRequest updateEventAdminRequest, Event event) {
        LocalDateTime startTime = event.getEventDate().minusHours(1);
        if (event.getState().equals(State.PUBLISHED)) {
            throw new EventDoesNotSatisfyRulesException("Нельзя обновить опубликованную публикацию");
        }
        if (updateEventAdminRequest.getTitle() != null) {
            if (updateEventAdminRequest.getTitle().length() < 3 || updateEventAdminRequest.getTitle().length() > 120) {
                throw new ValidationException("Ошибка валидации title");
            }
        }
        if (updateEventAdminRequest.getDescription() != null) {
            if (updateEventAdminRequest.getDescription().length() < 20 || updateEventAdminRequest.getDescription().length() > 7000) {
                throw new ValidationException("Ошибка валидации description");
            }
        }

        if (updateEventAdminRequest.getAnnotation() != null) {
            if (updateEventAdminRequest.getAnnotation().length() < 20 || updateEventAdminRequest.getAnnotation().length() > 2000) {
                throw new ValidationException("Ошибка валидации annotation");
            }
        }
        if (updateEventAdminRequest.getEventDate() != null) {
            LocalDateTime startNewTime = LocalDateTime.parse(updateEventAdminRequest.getEventDate(), EventDtoMapper.FORMATTER);
            if (startNewTime.isBefore(LocalDateTime.now()) || startNewTime.isBefore(event.getCreatedOn())) {
                throw new ValidationException();
            }
        }

        if (updateEventAdminRequest.getStateAction() != null && (updateEventAdminRequest.getStateAction().equals(StateAction.REJECT_EVENT.name()) && !event.getState().equals(State.PENDING)) ||
                updateEventAdminRequest.getStateAction() != null && (updateEventAdminRequest.getStateAction().equals(StateAction.PUBLISH_EVENT.name()) && event.getState().equals(State.CANCELED)) ||
                event.getPublishedOn() != null && event.getPublishedOn().isAfter(startTime)) {
            throw new EventDoesNotSatisfyRulesException("event not satisfy rules");
        }
    }

    private Event updateForAdminRequest(Event receivedEvent, UpdateEventAdminRequest adminRequest) {
        if (adminRequest.getStateAction() != null && adminRequest.getStateAction().equals(StateAction.PUBLISH_EVENT.name())) {
            receivedEvent.setState(State.PUBLISHED);
        }
        if (adminRequest.getStateAction() != null && adminRequest.getStateAction().equals(StateAction.REJECT_EVENT.name())) {
            receivedEvent.setState(State.CANCELED);
        }
        if (adminRequest.getStateAction() != null && adminRequest.getStateAction().equals(StateAction.SEND_TO_REVIEW.name())) {
            receivedEvent.setState(State.PENDING);
        }
        if (adminRequest.getAnnotation() != null) receivedEvent.setAnnotation(adminRequest.getAnnotation());
        if (adminRequest.getCategory() != null) {
            Category categoryForUpdate = categoryRepository.findById((long) adminRequest.getCategory()).orElseThrow(() -> new CategoryNotExistException("category not exist"));
            receivedEvent.setCategory(categoryForUpdate);
        }
        if (adminRequest.getDescription() != null) receivedEvent.setDescription(adminRequest.getDescription());
        if (adminRequest.getEventDate() != null)
            receivedEvent.setEventDate(LocalDateTime.parse(adminRequest.getEventDate(), EventDtoMapper.FORMATTER));//????
        if (adminRequest.getLocation() != null) {
            receivedEvent.setLongitude(adminRequest.getLocation().getLon());
            receivedEvent.setLatitude(adminRequest.getLocation().getLat());
        }
        if (adminRequest.getPaid() != null) receivedEvent.setPaid(adminRequest.getPaid());
        if (adminRequest.getParticipantLimit() != null)
            receivedEvent.setParticipantLimit(adminRequest.getParticipantLimit());
        if (adminRequest.getRequestModeration() != null)
            receivedEvent.setRequestModeration(adminRequest.getRequestModeration());
        if (adminRequest.getTitle() != null) receivedEvent.setTitle(adminRequest.getTitle());

        return receivedEvent;
    }

    private Event updateForNewEvent(UpdateEventUserRequest updateEventUserRequest, long eventId) {
        Event receivedEvent = eventRepository.findById(eventId).orElseThrow(() -> new EventNotExistException("event not exist"));

        if (updateEventUserRequest.getStateAction() != null && updateEventUserRequest.getStateAction().equals(StateAction.PUBLISH_EVENT.name())) {
            receivedEvent.setState(State.PUBLISHED);
        }
        if (updateEventUserRequest.getStateAction() != null && updateEventUserRequest.getStateAction().equals(StateAction.CANCEL_REVIEW.name())) {
            receivedEvent.setState(State.CANCELED);
        }
        if (updateEventUserRequest.getStateAction() != null && updateEventUserRequest.getStateAction().equals(StateAction.SEND_TO_REVIEW.name())) {
            receivedEvent.setState(State.PENDING);
        }
        if (updateEventUserRequest.getCategory() != null) {
            Category receivedCategory = categoryRepository.findById(updateEventUserRequest.getCategory()).orElseThrow(() -> new CategoryNotExistException("category not exist"));
            receivedEvent.setCategory(receivedCategory);
        }

        if (updateEventUserRequest.getParticipantLimit() != null) {
            receivedEvent.setParticipantLimit(updateEventUserRequest.getParticipantLimit());
        }

        if (updateEventUserRequest.getAnnotation() != null) {
            receivedEvent.setAnnotation(updateEventUserRequest.getAnnotation());
        }
        if (updateEventUserRequest.getDescription() != null) {
            receivedEvent.setDescription(updateEventUserRequest.getDescription());
        }
        if (updateEventUserRequest.getEventDate() != null) {
            receivedEvent.setEventDate(LocalDateTime.parse(updateEventUserRequest.getEventDate(), EventDtoMapper.FORMATTER));
        }
        if (updateEventUserRequest.getLocation() != null) {
            receivedEvent.setLatitude(updateEventUserRequest.getLocation().getLat());
            receivedEvent.setLongitude(updateEventUserRequest.getLocation().getLon());
        }
        if (updateEventUserRequest.getPaid() != null) {
            receivedEvent.setPaid(updateEventUserRequest.getPaid());
        }
        if (updateEventUserRequest.getRequestModeration() != null) {
            receivedEvent.setRequestModeration(updateEventUserRequest.getRequestModeration());
        }
        if (updateEventUserRequest.getTitle() != null) {
            receivedEvent.setTitle(updateEventUserRequest.getTitle());
        }
        return receivedEvent;
    }

    private Map<Long, Long> getStatsForEvents(LocalDateTime start, List<Long> eventsIds) {

        List<String> uris = eventsIds.stream()
                .map(id -> "/events/" + id)
                .collect(Collectors.toList());

        List<StatsDtoResponse> statsDtoResponses = statsClient.getStats(start, LocalDateTime.now(), uris, true);

        Map<Long, Long> veiws = new HashMap<>();

        for (StatsDtoResponse response : statsDtoResponses) {
            String uri = response.getUri();
            Long eventId = Long.parseLong(uri.substring(uri.lastIndexOf("/") + 1));
            veiws.put(eventId, response.getHits());
        }
        return veiws;
    }

    private List<EventShortDto> incrementViewsAndMapEvent(Page<Event> receivedEvents) {
        List<Long> ids = receivedEvents.stream()
                .map(Event::getId)
                .collect(Collectors.toList());
        LocalDateTime minDate = eventRepository.getMinDate(ids);
        List<EventShortDto> finalList = new ArrayList<>();

        Map<Long, Long> veiws = getStatsForEvents(minDate, ids);
        for (Event event : receivedEvents) {
            finalList.add(EventDtoMapper.toEventShortDtoWithViews(event, veiws.getOrDefault(event.getId(), 0L)));
        }
        return finalList;
    }
}
