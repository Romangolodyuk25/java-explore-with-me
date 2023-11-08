package ru.practicum.compilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilation.CompilationDto;
import ru.practicum.compilation.service.CompilationService;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/compilations")
public class CompilationController {

    private final CompilationService compilationService;

    @GetMapping
    public List<CompilationDto> getCompilations(@RequestParam(required = false) Boolean pinned,
                                                @RequestParam(required = false, defaultValue = "0") Integer from,
                                                @RequestParam(required = false, defaultValue = "10") Integer size) {
        log.info("Значение pinned {}, from {}, size {}", pinned, from, size);
        return compilationService.getCompilations(pinned, from, size);
    }

    @GetMapping("/{compId}")
    public CompilationDto getCompilationById(@PathVariable long compId) {
        log.info("Значение compId {}", compId);
        return compilationService.getCompilationById(compId);
    }
}
