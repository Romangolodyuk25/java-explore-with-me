package ru.practicum.comment;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class CommentDtoOut {
    private Long id;
    private String text;
    private LocalDateTime createdOn;
    private String authorName;
}
