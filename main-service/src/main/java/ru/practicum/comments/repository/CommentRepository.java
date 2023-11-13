package ru.practicum.comments.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.comments.model.Comment;

import java.util.List;


@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    Page<Comment> findByEvent_Id(long eventId, Pageable pageable);

    List<Comment> findByAuthor_IdAndEvent_Id(long authorId, long eventId);
}
