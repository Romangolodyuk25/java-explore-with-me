package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.StatsDtoRequest;
import ru.practicum.StatsDtoResponse;
import ru.practicum.mapper.StatsDtoMapper;
import ru.practicum.model.Hit;
import ru.practicum.repository.StatsRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final StatsRepository statsRepository;
    public static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public StatsDtoRequest createHit(StatsDtoRequest statsDtoRequest) {
        Hit newHit = StatsDtoMapper.toHit(statsDtoRequest);
        return StatsDtoMapper.toStatsDtoRequest(statsRepository.save(newHit));
    }

    @Override
    public List<StatsDtoResponse> getStats(String start, String end, String[] uris, Boolean unique) {
        LocalDateTime timeStart = LocalDateTime.parse(start, FORMAT);
        LocalDateTime timeEnd = LocalDateTime.parse(end, FORMAT);

        if (uris == null) {
            return statsRepository.findHitsWithoutUris(timeStart, timeEnd);
        } else {
            if (unique == null || !unique) {
                return statsRepository.findHitsWithUris(uris, timeStart, timeEnd);
            }
            return statsRepository.findHitsWithIsUnique(uris, timeStart, timeEnd);
        }
    }
}
