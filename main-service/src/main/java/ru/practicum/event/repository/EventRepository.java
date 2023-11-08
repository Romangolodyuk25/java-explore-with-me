package ru.practicum.event.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.State;
import ru.practicum.event.model.Event;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    Page<Event> findByInitiator_Id(long initiatorId, Pageable pageable);

    List<Event> findByCategory_Id(long categoryId);

    @Query("select e " +
            "from Event e " +
            "where e.category.id in ?1 ")
    Page<Event> findByCategory_IdIn(List<Long> categories, Pageable pageable);


    @Query("select e " +
            "from Event e " +
            "where e.initiator.id in ?1 AND e.state in ?2 AND e.category.id in ?3 AND e.createdOn >= ?4 AND e.eventDate <= ?5 ")
    Page<Event> searchEvent(List<Long> users, List<State> states, List<Long> categories, LocalDateTime rangeStart, LocalDateTime rangeEnd, Pageable pageable);

    @Query("select e " +
            "from Event as e " +
            "where upper(e.annotation) like upper(concat('%', ?1, '%')) " +
            "AND e.state = 'PUBLISHED' " +
            "AND e.category.id in ?2 AND e.paid = ?3 " +
            "AND e.createdOn >= ?4 AND e.eventDate <= ?5 " +
            "AND e.confirmedRequests = e.participantLimit ")
    Page<Event> findAllEventsIsOnlyAvailable(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart, LocalDateTime rangeEnd, Pageable pageable);

    @Query("select e " +
            "from Event as e " +
            "where upper(e.annotation) like upper(concat('%', ?1, '%')) " +
            "AND e.state = 'PUBLISHED' " +
            "AND e.category.id in ?2 AND e.paid = ?3 " +
            "AND e.createdOn >= ?4 AND e.eventDate <= ?5 " +
            "AND e.confirmedRequests < e.participantLimit ")
    Page<Event> findAllEventsIsNotOnlyAvailable(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart, LocalDateTime rangeEnd, Pageable pageable);

    @Query("select e " +
            "from Event as e " +
            "where upper(e.annotation) like upper(concat('%', ?1, '%')) " +
            "AND e.state = 'PUBLISHED' " +
            "AND e.category.id in ?2 AND e.paid = ?3 " +
            "AND e.eventDate <= ?4 " +
            "AND e.confirmedRequests < e.participantLimit ")
    Page<Event> findByEventWithEmptyStartDate(String text, List<Long> categories, Boolean paid, LocalDateTime time, Pageable pageable);

    List<Event> findByIdAndState(long id, State state);

    @Query("select min(e.publishedOn) from Event as e where e.id in ?1")
    LocalDateTime getMinDate(List<Long> ids);
}
