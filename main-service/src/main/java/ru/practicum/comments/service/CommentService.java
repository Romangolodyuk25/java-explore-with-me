package ru.practicum.comments.service;

import ru.practicum.comment.CommentDtoIn;
import ru.practicum.comment.CommentDtoOut;

import java.util.List;

public interface CommentService {

    CommentDtoOut createdComment(CommentDtoIn commentDtoIn, long eventId, long userId);

    List<CommentDtoOut> getAllCommentsForCurrentEvent(long eventId, long userId, Integer from, Integer size);

    void deleteComment(long userId, long eventId, long commentId);

    List<CommentDtoOut> getCommentsForCurrentUserInCurrentEvent(long userId, long eventId);

    CommentDtoOut updateComment(CommentDtoIn commentDtoIn, long eventId, long userId, long commentId);
}
