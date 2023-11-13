package ru.practicum.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.request.service.RequestService;

@RestController
@Slf4j
@RequiredArgsConstructor
public class RequestController {
    private final RequestService requestService;
}
