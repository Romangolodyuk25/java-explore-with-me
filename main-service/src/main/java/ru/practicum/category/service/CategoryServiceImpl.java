package ru.practicum.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.category.CategoryDto;
import ru.practicum.category.NewCategoryDto;
import ru.practicum.category.mapper.CategoryDtoMapper;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.CategoryNotExistException;
import ru.practicum.exception.CategoryRelationWithEventException;

import javax.validation.ValidationException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements  CategoryService {

    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    @Override
    public CategoryDto createCategory(NewCategoryDto newCategoryDto) {
        validateCategory(newCategoryDto);

        Category newCategory = CategoryDtoMapper.toCategory(newCategoryDto);
        return CategoryDtoMapper.toCategoryDto(categoryRepository.save(newCategory));
    }

    @Override
    public CategoryDto updateCategory(long catId, NewCategoryDto newCategoryDto) {
        validateCategory(newCategoryDto);
        Category receivedCategory = categoryRepository.findById(catId).orElseThrow(() -> new CategoryNotExistException("Категория не найдена"));

        receivedCategory.setTitle(newCategoryDto.getName());
        return CategoryDtoMapper.toCategoryDto(categoryRepository.save(receivedCategory));
    }

    @Override
    public void deleteCategory(long catId) {
        categoryRepository.findById(catId).orElseThrow(() -> new CategoryNotExistException("Категория не найдена"));
        if (eventRepository.findByCategory_Id(catId).size() != 0) {
            log.info("Количестов категорий связанных с событием {}", eventRepository.findByCategory_Id(catId).size());
            throw new CategoryRelationWithEventException("Категория связана с событием");
        }
        log.info("Категория с айди {} удалена", catId);
        categoryRepository.deleteById(catId);
    }

    @Override
    public List<CategoryDto> getAllCategories(Integer from, Integer size) {
        Pageable page = PageRequest.of(from, size);
        return categoryRepository.findAll(page).getContent().stream()
                .map(CategoryDtoMapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto getCategoryById(long catId) {
       Category receivedCategory = categoryRepository.findById(catId).orElseThrow(() -> new CategoryNotExistException("category not exist"));
       log.info("Категория {} получена", receivedCategory);
       return CategoryDtoMapper.toCategoryDto(receivedCategory);
    }

    private void validateCategory(NewCategoryDto newCategoryDto) {
        if (newCategoryDto.getName()==null || newCategoryDto.getName().isEmpty() || newCategoryDto.getName().isBlank()) {
            throw new ValidationException();
        }
        if(newCategoryDto.getName().length() > 50) {
            throw new ValidationException("Ошибка валидации");
        }
    }
}
