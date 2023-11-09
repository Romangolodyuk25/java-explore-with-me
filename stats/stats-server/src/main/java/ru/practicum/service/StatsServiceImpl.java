package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.StatsDtoRequest;
import ru.practicum.StatsDtoResponse;
import ru.practicum.mapper.StatsDtoMapper;
import ru.practicum.model.Hit;
import ru.practicum.repository.StatsRepository;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final StatsRepository statsRepository;

    @Override
    public StatsDtoRequest createHit(StatsDtoRequest statsDtoRequest) {
        Hit newHit = StatsDtoMapper.toHit(statsDtoRequest);
        return StatsDtoMapper.toStatsDtoRequest(statsRepository.save(newHit));
    }

    @Override
    public List<StatsDtoResponse> getStats(String start, String end, List<String> uris, Boolean unique) {
        LocalDateTime timeStart = LocalDateTime.parse(start, StatsDtoMapper.FORMAT);
        LocalDateTime timeEnd = LocalDateTime.parse(end, StatsDtoMapper.FORMAT);

        if (timeStart.isAfter(timeEnd)) {
            throw new ValidationException("Ошибка валидации");
        }

        if (uris == null) {
                return statsRepository.findHitsWithoutUris(timeStart, timeEnd);
        } else {
            if (unique == null || !unique) {
                List<StatsDtoResponse> responses = statsRepository.findHitsWithUris(uris, timeStart, timeEnd);
                List<StatsDtoResponse> newResponses = new ArrayList<>();
                for (StatsDtoResponse r : responses) {
                    long count = r.getHits();
                    r.setHits(++count);
                    newResponses.add(r);
                }
                return newResponses;
                //return statsRepository.findHitsWithUris(uris, timeStart, timeEnd);
            }
            return statsRepository.findHitsWithIsUnique(uris, timeStart, timeEnd);
        }
    }
}
