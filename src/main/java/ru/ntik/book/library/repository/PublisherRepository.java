package ru.ntik.book.library.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.ntik.book.library.domain.Publisher;

import java.util.Optional;



@Repository
public interface PublisherRepository extends JpaRepository<Publisher, Long> {

    @EntityGraph(value = Publisher.GRAPH_FETCH_ALL)
    @Query("SELECT p FROM Publisher p WHERE p.id = :id")
    Optional<Publisher> fetchById(@Param("id") Long id);
}