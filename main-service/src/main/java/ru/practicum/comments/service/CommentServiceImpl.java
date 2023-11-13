package ru.practicum.comments.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.State;
import ru.practicum.comment.CommentDtoIn;
import ru.practicum.comment.CommentDtoOut;
import ru.practicum.comments.mapper.CommentDtoMapper;
import ru.practicum.comments.model.Comment;
import ru.practicum.comments.repository.CommentRepository;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.*;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService{

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final Sort sortByCreateDate = Sort.by(Sort.Direction.DESC, "createdOn");

    @Override
    public CommentDtoOut createdComment(CommentDtoIn commentDtoIn, long eventId, long userId) {
        User receivedUser = userRepository.findById(userId).orElseThrow(() -> new UserNotExistException("user not exist"));
        Event receivedEvent = eventRepository.findById(eventId).orElseThrow(() -> new EventNotExistException("event not exist"));

        if (!receivedEvent.getState().equals(State.PUBLISHED) || receivedEvent.getInitiator().getId() == userId) {
            throw new CommentDoesNotSatisfyRulesException("Создание комментария не удовлетворяет правилам");
        }

        Comment commentForBd = CommentDtoMapper.toComment(commentDtoIn, receivedUser, receivedEvent);

        return CommentDtoMapper.toCommentDtoOut(commentRepository.save(commentForBd));
    }

    @Override
    public List<CommentDtoOut> getAllCommentsForCurrentEvent(long eventId, long userId, Integer from, Integer size) {
        eventRepository.findById(eventId).orElseThrow(() -> new EventNotExistException("event not exist"));
        userRepository.findById(userId).orElseThrow(() -> new UserNotExistException("user not exist"));

        Pageable page = PageRequest.of(from / size, size, sortByCreateDate);

        return commentRepository.findByEvent_Id(eventId, page).stream()
                .map(CommentDtoMapper::toCommentDtoOut)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteComment(long userId, long eventId, long commentId) {
        userRepository.findById(userId).orElseThrow(() -> new UserNotExistException("user not exist"));
        eventRepository.findById(eventId).orElseThrow(() -> new EventNotExistException("event not exist"));
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new CommentNotExistException("comment not exist"));

        if (comment.getId() != userId) {
            throw new UserIsNotOwner("Юзер не является владельцем комментария");
        }
        log.info("Comment with id {} deleted", commentId);
        commentRepository.deleteById(commentId);
    }

    @Override
    public List<CommentDtoOut> getCommentsForCurrentUserInCurrentEvent(long userId, long eventId) {
        userRepository.findById(userId).orElseThrow(() -> new UserNotExistException("user not exist"));
        eventRepository.findById(eventId).orElseThrow(() -> new EventNotExistException("event not exist"));

        return commentRepository.findByAuthor_IdAndEvent_Id(userId, eventId).stream()
                .map(CommentDtoMapper::toCommentDtoOut)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDtoOut updateComment(CommentDtoIn commentDtoIn, long eventId, long userId, long commentId) {
        userRepository.findById(userId).orElseThrow(() -> new UserNotExistException("user not exist"));
        Comment receivedComment = commentRepository.findById(commentId).orElseThrow(() -> new CommentNotExistException("comment not exist"));
        Event receivedEvent = eventRepository.findById(eventId).orElseThrow(() -> new EventNotExistException("event not exist"));

        if (!receivedEvent.getState().equals(State.PUBLISHED) || receivedComment.getAuthor().getId() != userId) {
            throw new CommentDoesNotSatisfyRulesException("Обновление комментария не удовлетворяет правилам");
        }

       if (commentDtoIn.getText()!= null) {
           receivedComment.setText(commentDtoIn.getText());
       }

       return CommentDtoMapper.toCommentDtoOut(commentRepository.save(receivedComment));
    }

}
