package ru.practicum.category.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.category.CategoryDto;
import ru.practicum.category.NewCategoryDto;
import ru.practicum.category.model.Category;

@UtilityClass
public class CategoryDtoMapper {
    public static Category toCategory(NewCategoryDto newCategoryDto) {
        return Category.builder()
                .title(newCategoryDto.getName())
                .build();
    }

    public static CategoryDto toCategoryDto(Category category) {
        return CategoryDto.builder()
                .name(category.getTitle())
                .id(Math.toIntExact(category.getId()))
                .build();
    }
}
