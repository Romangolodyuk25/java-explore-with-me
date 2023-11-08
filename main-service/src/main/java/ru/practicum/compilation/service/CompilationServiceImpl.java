package ru.practicum.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.category.repository.CategoryRepository;
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

import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {

    private final CategoryRepository categoryRepository;
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Override
    public CompilationDto createCompilation(NewCompilationDto newCompilationDto) {
        List<Event> events = validatedCompilation(newCompilationDto);
        Compilation compilation = CompilationDtoMapper.toCompilation(newCompilationDto, events);
        return CompilationDtoMapper.toCompilationDto(compilationRepository.save(compilation));
    }

    @Override
    public CompilationDto updateCompilation(long compId, UpdateCompilationRequest updateCompilationRequest) {
        Compilation receivedCompilation = compilationRepository.findById(compId).orElseThrow(() -> new CompilationNotExistException("Подборки не существует"));

        validateForUpdateCompilation(updateCompilationRequest);

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


    //ВАЛИДАЦИЯ
    private List<Event> validatedCompilation(NewCompilationDto newCompilationDto) {
        if (newCompilationDto.getTitle() == null || newCompilationDto.getTitle().isBlank() ||
                newCompilationDto.getTitle().isEmpty() || newCompilationDto.getTitle().length() > 50) {
            throw new ValidationException("Ошибка валидации");
        }

        List<Event> events = new ArrayList<>();
        if (newCompilationDto.getEvents() != null) {
            for (Integer eventId : newCompilationDto.getEvents()) {
                Event receivedEvent = eventRepository.findById((long) eventId).orElseThrow(() -> new EventNotExistException("eventNotExist"));
                events.add(receivedEvent);
            }
        }
        return events;
    }

    private void validateForUpdateCompilation(UpdateCompilationRequest updateCompilationRequest) {
        if (updateCompilationRequest.getTitle() != null) {
            if (updateCompilationRequest.getTitle().length() > 50 || updateCompilationRequest.getTitle().isBlank()) {
                throw new ValidationException("Ошибка валидации");
            }
        }
    }
}
