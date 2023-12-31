package ru.practicum.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.compilation.CompilationDto;
import ru.practicum.compilation.NewCompilationDto;
import ru.practicum.compilation.UpdateCompilationRequest;
import ru.practicum.compilation.mapper.CompilationDtoMapper;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.CompilationNotExistException;
import ru.practicum.exception.EventNotExistException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Override
    public CompilationDto createCompilation(NewCompilationDto newCompilationDto) {
        List<Event> events = getEvents(newCompilationDto);
        Compilation compilation = CompilationDtoMapper.toCompilation(newCompilationDto, events);
        return CompilationDtoMapper.toCompilationDto(compilationRepository.save(compilation));
    }

    @Override
    public CompilationDto updateCompilation(long compId, UpdateCompilationRequest updateCompilationRequest) {
        Compilation receivedCompilation = compilationRepository.findById(compId).orElseThrow(() -> new CompilationNotExistException("Подборки не существует"));

        List<Event> events = new ArrayList<>();
        if (updateCompilationRequest.getEvents() != null) {
            for (Integer eventId : updateCompilationRequest.getEvents()) {
                Event receivedEvent = eventRepository.findById((long) eventId).orElseThrow(() -> new EventNotExistException("eventNotExist"));
                events.add(receivedEvent);
            }
        }

        if (updateCompilationRequest.getTitle() != null) {
            receivedCompilation.setTitle(updateCompilationRequest.getTitle());
        }
        if (updateCompilationRequest.getPinned() != null) {
            receivedCompilation.setPinned(updateCompilationRequest.getPinned());
        }
        if (updateCompilationRequest.getEvents() != null) {
            receivedCompilation.setEvents(events);
        }
        return CompilationDtoMapper.toCompilationDto(compilationRepository.save(receivedCompilation));
    }

    @Override
    public void deleteCompilation(long compId) {
        compilationRepository.findById(compId).orElseThrow(() -> new CompilationNotExistException("Подборки не существует"));
        log.info("Объект с id {} удален", compId);
        compilationRepository.deleteById(compId);
    }

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        Pageable page = PageRequest.of(from / size, size);
        return compilationRepository.findByPinned(pinned, page).stream()
                .map(CompilationDtoMapper::toCompilationDto)
                .collect(Collectors.toList());
    }

    @Override
    public CompilationDto getCompilationById(long compId) {
        Compilation receivedCompilation = compilationRepository.findById(compId).orElseThrow(() -> new CompilationNotExistException("compilation Not Exist"));
        return CompilationDtoMapper.toCompilationDto(receivedCompilation);
    }


    private List<Event> getEvents(NewCompilationDto newCompilationDto) {

        List<Event> events = new ArrayList<>();
        if (newCompilationDto.getEvents() != null) {
            for (Integer eventId : newCompilationDto.getEvents()) {
                Event receivedEvent = eventRepository.findById((long) eventId).orElseThrow(() -> new EventNotExistException("eventNotExist"));
                events.add(receivedEvent);
            }
        }
        return events;
    }
}
