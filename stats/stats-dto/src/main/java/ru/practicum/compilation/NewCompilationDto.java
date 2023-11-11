package ru.practicum.compilation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class NewCompilationDto {
    List<Integer> events;
    Boolean pinned;
    @Length(min = 1, max = 50)
    @NotBlank
    String title;
}
