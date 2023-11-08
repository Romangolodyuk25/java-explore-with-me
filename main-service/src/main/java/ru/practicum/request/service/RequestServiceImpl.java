package ru.practicum.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.State;
import ru.practicum.Status;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.EventNotExistException;
import ru.practicum.exception.RequestDoesNotSatisfyRulesException;
import ru.practicum.exception.RequestNotExistException;
import ru.practicum.exception.UserNotExistException;
import ru.practicum.request.EventRequestStatusUpdateRequest;
import ru.practicum.request.EventRequestStatusUpdateResult;
import ru.practicum.request.ParticipationRequestDto;
import ru.practicum.request.mapper.RequestDtoMapper;
import ru.practicum.request.model.Request;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;


    @Override
    public ParticipationRequestDto createRequest(long userId, long eventId) {
        User receivedUser = userRepository.findById(userId).orElseThrow(() -> new UserNotExistException("user not exist"));
        Event receivedEvent = eventRepository.findById(eventId).orElseThrow(() -> new EventNotExistException("event not exist"));

        if (receivedEvent.getParticipantLimit() != 0 && (receivedEvent.getConfirmedRequests() == receivedEvent.getParticipantLimit())) {
            throw new RequestDoesNotSatisfyRulesException("Ошибка данных при создании запроса");
        }
        if (requestRepository.findByRequester(userId).size() != 0 || receivedEvent.getInitiator().getId() == userId ||
                receivedEvent.getConfirmedRequests() > receivedEvent.getParticipantLimit() ||
                receivedEvent.getState() != State.PUBLISHED) {
            throw new RequestDoesNotSatisfyRulesException("Ошибка данных при создании запроса");
        }

        Request newRequest = Request.builder()
                .created(LocalDateTime.now())
                .event(receivedEvent)
                .requester(receivedUser)
                .status(State.PENDING)
                .build();

        if (!receivedEvent.getRequestModeration() || receivedEvent.getParticipantLimit() == 0) {
            newRequest.setStatus(State.CONFIRMED);
            receivedEvent.setConfirmedRequests(receivedEvent.getConfirmedRequests() + 1);
            eventRepository.save(receivedEvent);
        }

        return RequestDtoMapper.toParticipationRequestDto(requestRepository.save(newRequest));
    }

    @Override
    public List<ParticipationRequestDto> getRequestsForUserOtherEvent(long userId) {
        userRepository.findById(userId).orElseThrow(() -> new UserNotExistException("user not exist"));
        return requestRepository.findByRequester(userId).stream()
                .map(RequestDtoMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public ParticipationRequestDto cancelRequest(long userId, long requestId) {
        userRepository.findById(userId).orElseThrow(() -> new UserNotExistException("user not exist"));

        Request receivedRequest = requestRepository.findById(requestId).orElseThrow(() -> new RequestNotExistException("request not exist"));
        receivedRequest.setStatus(State.CANCELED);

        Event receivedEvent = eventRepository.findById(receivedRequest.getEvent().getId()).orElseThrow(() -> new EventNotExistException("event not exist"));
        receivedEvent.setConfirmedRequests(receivedEvent.getConfirmedRequests() - 1);
        eventRepository.save(receivedEvent);

        return RequestDtoMapper.toParticipationRequestDto(requestRepository.save(receivedRequest));
    }

    @Override
    public List<ParticipationRequestDto> getRequestsForCurrentUser(long userId, long eventId) {
        userRepository.findById(userId).orElseThrow(() -> new UserNotExistException("user not exist"));
        eventRepository.findById(eventId).orElseThrow(() -> new EventNotExistException("event not exist"));

        List<Request> requests = requestRepository.findByRequestEventForCurrentUser(userId, eventId);
        return requests.stream()
                .map(RequestDtoMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }


    @Override
    public EventRequestStatusUpdateResult updateStatusForCurrentUser(EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest, int userId, int eventId) {
        userRepository.findById((long) userId).orElseThrow(() -> new UserNotExistException("user not exist"));
        Event receivedEvent = eventRepository.findById((long) eventId).orElseThrow(() -> new EventNotExistException("event not exist"));

        EventRequestStatusUpdateResult eventRequestStatusUpdateResult = new EventRequestStatusUpdateResult(new ArrayList<>(), new ArrayList<>());

        if (receivedEvent.getConfirmedRequests() >= receivedEvent.getParticipantLimit()) {
            throw new RequestDoesNotSatisfyRulesException("Ошибка данных при создании запроса");
        }

        if (receivedEvent.getParticipantLimit() == 0 || !receivedEvent.getRequestModeration()) {
            for (Integer r : eventRequestStatusUpdateRequest.getRequestIds()) {
                Request receivedRequest = requestRepository.findById((long) r).orElseThrow(() -> new RequestNotExistException("request not esixt"));
                receivedRequest.setStatus(State.CONFIRMED);
                receivedEvent.setConfirmedRequests(receivedEvent.getConfirmedRequests() + 1);
                eventRequestStatusUpdateResult.getConfirmedRequests().add(RequestDtoMapper.toParticipationRequestDto(receivedRequest));
            }
            eventRepository.save(receivedEvent);
            return eventRequestStatusUpdateResult;
        }
        int limit = receivedEvent.getParticipantLimit();
        int countRequests = receivedEvent.getConfirmedRequests();
        if (eventRequestStatusUpdateRequest.getStatus().equals(Status.CONFIRMED)) {
            for (Integer r : eventRequestStatusUpdateRequest.getRequestIds()) {
                Request receivedRequest = requestRepository.findById((long) r).orElseThrow(() -> new RequestNotExistException("request not exist"));
                if (!receivedRequest.getStatus().equals(State.PENDING)) {
                    throw new RequestDoesNotSatisfyRulesException("Запрос имеет статус некорректный статус");
                }
                if (countRequests < limit) {
                    receivedRequest.setStatus(State.CONFIRMED);
                    receivedEvent.setConfirmedRequests(receivedEvent.getConfirmedRequests() + 1);
                    eventRequestStatusUpdateResult.getConfirmedRequests().add(RequestDtoMapper.toParticipationRequestDto(receivedRequest));
                    requestRepository.save(receivedRequest);
                    countRequests++;
                } else {
                    receivedRequest.setStatus(State.REJECTED);
                    eventRequestStatusUpdateResult.getRejectedRequests().add(RequestDtoMapper.toParticipationRequestDto(receivedRequest));
                    requestRepository.save(receivedRequest);
                }
            }
        } else {
            for (Integer r : eventRequestStatusUpdateRequest.getRequestIds()) {
                Request receivedRequest = requestRepository.findById((long) r).orElseThrow(() -> new RequestNotExistException("request not exist"));
                if (!receivedRequest.getStatus().equals(State.PENDING)) {
                    throw new RequestDoesNotSatisfyRulesException("Запрос имеет статус некорректный статус");
                }
                receivedRequest.setStatus(State.REJECTED);
                requestRepository.save(receivedRequest);
                eventRequestStatusUpdateResult.getRejectedRequests().add(RequestDtoMapper.toParticipationRequestDto(receivedRequest));
            }
        }
        eventRepository.save(receivedEvent);
        return eventRequestStatusUpdateResult;
    }
}
