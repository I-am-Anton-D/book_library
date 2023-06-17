package ru.ntik.book.library.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.ntik.book.library.domain.Author;

import java.util.Optional;

public interface AuthorRepository extends JpaRepository<Author, Long> {
    @EntityGraph(value = Author.GRAPH_FETCH_ALL)
    @Query("SELECT a FROM Author a WHERE a.id = :id")
    Optional<Author> fetchById(@Param("id") Long id);
}