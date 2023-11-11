package ru.practicum.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.State;
import ru.practicum.category.CategoryDto;
import ru.practicum.category.NewCategoryDto;
import ru.practicum.category.service.CategoryService;
import ru.practicum.compilation.CompilationDto;
import ru.practicum.compilation.NewCompilationDto;
import ru.practicum.compilation.UpdateCompilationRequest;
import ru.practicum.compilation.service.CompilationService;
import ru.practicum.event.EventFullDto;
import ru.practicum.event.service.EventService;
import ru.practicum.request.UpdateEventAdminRequest;
import ru.practicum.user.NewUserRequest;
import ru.practicum.user.UserDto;
import ru.practicum.user.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final CategoryService categoryService;
    private final CompilationService compilationService;
    private final EventService eventService;

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@Valid @RequestBody NewUserRequest newUserRequest) {
        log.info("Пользователь создан {}", newUserRequest);
        return userService.createUser(newUserRequest);
    }

    @GetMapping("/users")
    public List<UserDto> getUsers(@RequestParam(required = false) List<Long> ids,
                                  @RequestParam(required = false, defaultValue = "0") Integer from,
                                  @RequestParam(required = false, defaultValue = "10") Integer size
    ) {
        log.info("Получен список id пользователей размером {}, from {}, size {}", ids == null ? null : ids.size(), from, size);
        return userService.getUsers(ids, from, size);
    }

    @DeleteMapping("/users/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable long id) {
        log.info("Айди пользователя для удаления {}", id);
        userService.deleteUser(id);
    }

    @PostMapping("/categories")
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto createCategory(@Valid @RequestBody NewCategoryDto newCategoryDto) {
        log.info("Категория создана {}", newCategoryDto);
        return categoryService.createCategory(newCategoryDto);
    }

    @PatchMapping("/categories/{catId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto updateCategory(@PathVariable long catId,
                                      @Valid @RequestBody NewCategoryDto newCategoryDto) {
        return categoryService.updateCategory(catId, newCategoryDto);
    }

    @DeleteMapping("/categories/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable long catId) {
        log.info("Удалена категория c айди {}", catId);
        categoryService.deleteCategory(catId);
    }

    @PostMapping("/compilations")
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto createCompilation(@Valid @RequestBody NewCompilationDto newCompilationDto) {
        log.info("Объект для создания {}", newCompilationDto);
        return compilationService.createCompilation(newCompilationDto);
    }

    @PatchMapping("/compilations/{compId}")
    @ResponseStatus(HttpStatus.OK)
    public CompilationDto updateCompilation(@PathVariable long compId,
                                            @Valid @RequestBody UpdateCompilationRequest updateCompilationRequest) {
        log.info("id для обновления {} объект для обновления {}", compId, updateCompilationRequest);
        return compilationService.updateCompilation(compId, updateCompilationRequest);
    }

    @DeleteMapping("/compilations/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable long compId) {
        log.info("id для удаления события {}", compId);
        compilationService.deleteCompilation(compId);
    }

    @GetMapping("/events")
    public List<EventFullDto> searchEvents(@RequestParam(required = false) List<Long> users,
                                           @RequestParam(required = false) List<State> states,
                                           @RequestParam(required = false) List<Long> categories,
                                           @RequestParam(required = false) String rangeStart,
                                           @RequestParam(required = false) String rangeEnd,
                                           @RequestParam(defaultValue = "0")  @PositiveOrZero int from,
                                           @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Список айди пользователей {} , Список статусов {}, список айди категорий {} , дата старта {}, дата окончания {} , from {}, size {}", users, states, categories, rangeStart, rangeEnd, from, size);
        return eventService.searchEvents(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/events/{eventId}")
    public EventFullDto updateEventAdmin(@PathVariable long eventId,
                                         @Valid @RequestBody UpdateEventAdminRequest adminRequest) {
        log.info("Id Ивента {} , Объект для обновления {} ", eventId, adminRequest);
        return eventService.updateEventAdmin(eventId, adminRequest);
    }
}
