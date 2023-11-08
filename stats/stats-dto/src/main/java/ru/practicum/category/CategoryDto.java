package ru.practicum.category;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class CategoryDto {
    int id;

    @Length(min = 1, max = 50)
    String name;
}
