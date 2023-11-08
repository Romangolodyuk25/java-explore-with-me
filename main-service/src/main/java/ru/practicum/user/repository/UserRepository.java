package ru.practicum.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.user.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

import java.net.ContentHandler;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Page<User> findAllByIdIn(List<Long> ids, Pageable pageable);

    @Query("select u " +
            "from User u " +
            "where u.id in ?1 ")
    List<User> findAllUserForEmptyFromSize(List<Long> ids);

    Page<User> findAll(Pageable page);
}
