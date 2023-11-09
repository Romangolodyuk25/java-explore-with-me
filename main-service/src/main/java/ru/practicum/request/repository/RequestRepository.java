package ru.practicum.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.Status;
import ru.practicum.request.model.Request;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {


    @Query("select r " +
            "from Request r " +
            "where r.event.initiator.id = ?1 AND r.event.id = ?2 ")
    List<Request> findByRequestEventForCurrentUser(long userId, long eventId);

    @Query("select r " +
            "from Request r " +
            "where r.requester.id = ?1 ")
    List<Request> findByRequester(long userId);

//    @Query("select r " +
//            "from Request r " +
//            "where r.status = '?1' AND r.event.id in ?2 ")
//    List<Request> findByStatusAndEvent(String status, List<Long> eventsIds);
}
