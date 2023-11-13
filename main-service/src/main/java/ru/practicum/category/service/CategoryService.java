package ru.practicum.category.service;

import ru.practicum.category.CategoryDto;
import ru.practicum.category.NewCategoryDto;

import java.util.List;

public interface CategoryService {

    CategoryDto createCategory(NewCategoryDto newCategoryDto);

    void deleteCategory(long catId);

    CategoryDto updateCategory(long catId, NewCategoryDto newCategoryDto);

    List<CategoryDto> getAllCategories(Integer from, Integer size);

    CategoryDto getCategoryById(long catId);
}
