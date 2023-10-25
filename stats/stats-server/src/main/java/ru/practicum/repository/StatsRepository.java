package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.StatsDtoResponse;
import ru.practicum.model.Hit;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<Hit, Long> {


    @Query("select new ru.practicum.StatsDtoResponse(h.app, h.uri, count(h.ip)) " +
            "from Hit h " +
            "where h.timestamp >= ?1 AND h.timestamp <= ?2 " +
            "group by h.app, h.uri, h.ip " +
            "order by count(h.ip) desc")
    List<StatsDtoResponse> findHitsWithoutUris(LocalDateTime start, LocalDateTime end);


    @Query("select new ru.practicum.StatsDtoResponse(h.app, h.uri, count(h.ip)) " +
            "from Hit h " +
            "where h.uri in ?1 AND h.timestamp >= ?2 AND h.timestamp <= ?3 " +
            "group by h.app, h.uri, h.ip " +
            "order by count(h.ip) desc")
    List<StatsDtoResponse> findHitsWithUris(String[] uri, LocalDateTime start, LocalDateTime end);

    @Query("select new ru.practicum.StatsDtoResponse(h.app, h.uri, count(distinct(h.ip)))" +
            "from Hit h " +
            "where h.uri in ?1 AND h.timestamp >= ?2 AND h.timestamp <= ?3 " +
            "group by h.app, h.uri, h.ip " +
            "order by count(h.ip) desc")
    List<StatsDtoResponse> findHitsWithIsUnique(String[] uri, LocalDateTime start, LocalDateTime end);
}
