package ru.practicum.comments.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.comment.CommentDtoIn;
import ru.practicum.comment.CommentDtoOut;
import ru.practicum.comments.model.Comment;
import ru.practicum.event.model.Event;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;

@UtilityClass
public class CommentDtoMapper {

    public static Comment toComment(CommentDtoIn commentDtoIn, User user, Event event) {
        return Comment.builder()
                .text(commentDtoIn.getText())
                .createdOn(LocalDateTime.now())
                .author(user)
                .event(event)
                .build();
    }

    public static CommentDtoOut toCommentDtoOut(Comment comment) {
        return CommentDtoOut.builder()
                .id(comment.getId())
                .text(comment.getText())
                .createdOn(comment.getCreatedOn())
                .authorName(comment.getAuthor().getName())
                .build();
    }
}
