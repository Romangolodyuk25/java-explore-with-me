package ru.practicum.request.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.request.ParticipationRequestDto;
import ru.practicum.request.model.Request;

import java.time.format.DateTimeFormatter;

@UtilityClass
public class RequestDtoMapper {
    DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static ParticipationRequestDto toParticipationRequestDto(Request request) {
        return ParticipationRequestDto.builder()
                .id((int)request.getId())
                .created(FORMATTER.format(request.getCreated()))
                .event((int)request.getEvent().getId())
                .requester(Math.toIntExact(request.getRequester().getId()))
                .status(request.getStatus().name())
                .build();
    }
}
