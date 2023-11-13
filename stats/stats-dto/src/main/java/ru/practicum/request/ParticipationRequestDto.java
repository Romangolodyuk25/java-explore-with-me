package ru.practicum.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ParticipationRequestDto {
    int id;
    String created;
    int event;
    int requester;
    String status;
}
