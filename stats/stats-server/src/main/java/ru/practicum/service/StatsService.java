package ru.practicum.service;

import ru.practicum.StatsDtoRequest;
import ru.practicum.StatsDtoResponse;

import java.util.List;


public interface StatsService {

    StatsDtoRequest createHit(StatsDtoRequest statsDtoRequest);

    List<StatsDtoResponse> getStats(String start, String end, String[] unis, Boolean unique);

}
