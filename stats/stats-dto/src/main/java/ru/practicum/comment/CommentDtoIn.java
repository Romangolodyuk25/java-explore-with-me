package ru.practicum.comment;

import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDtoIn {
    @Length(min = 10, max = 2000)
    @NotBlank
    String text;
}
