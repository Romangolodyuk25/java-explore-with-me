package ru.practicum.controller;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.StatsDtoRequest;
import ru.practicum.StatsDtoResponse;
import ru.practicum.service.StatsService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class StatsController {

    private final StatsService statsService;

    @PostMapping("/hit")
    public StatsDtoRequest createHit(@Valid @RequestBody StatsDtoRequest statsDtoRequest) {
        log.info("Объект для сохранения {} ", statsDtoRequest);
        return statsService.createHit(statsDtoRequest);
    }

    @GetMapping("/stats")
    public List<StatsDtoResponse> getStats(@Valid @NonNull @RequestParam String start,
                                           @NonNull @RequestParam String end,
                                           @RequestParam(required = false) List<String> uris,
                                           @RequestParam(required = false) Boolean unique) {
        return statsService.getStats(start, end, uris, unique);
    }
}
