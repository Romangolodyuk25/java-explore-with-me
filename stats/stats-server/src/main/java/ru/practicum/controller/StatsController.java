package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.StatsDtoRequest;
import ru.practicum.StatsDtoResponse;
import ru.practicum.service.StatsService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;

    @PostMapping("/hit")
    public StatsDtoRequest createHit(@Valid @RequestBody StatsDtoRequest statsDtoRequest) {
        return statsService.createHit(statsDtoRequest);
    }

    @GetMapping("/stats")
    public List<StatsDtoResponse> getStats(@Valid @RequestParam String start,
                                           @RequestParam String end,
                                           @RequestParam(required = false) String[] uris,
                                           @RequestParam(required = false) Boolean unique) {
        return statsService.getStats(start, end, uris, unique);
    }
}
