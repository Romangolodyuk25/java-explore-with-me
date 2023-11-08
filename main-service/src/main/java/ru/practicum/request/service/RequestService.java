package ru.practicum.request.service;

import ru.practicum.request.EventRequestStatusUpdateRequest;
import ru.practicum.request.EventRequestStatusUpdateResult;
import ru.practicum.request.ParticipationRequestDto;

import java.util.List;

public interface RequestService {
    List<ParticipationRequestDto> getRequestsForCurrentUser(long userId, long eventId);

    EventRequestStatusUpdateResult updateStatusForCurrentUser(EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest, int userId, int eventId);

    ParticipationRequestDto createRequest(long userId, long eventId);

    List<ParticipationRequestDto> getRequestsForUserOtherEvent(long userId);

    ParticipationRequestDto cancelRequest(long userId, long requestId);
}
