package ru.ntik.book.library.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.ntik.book.library.domain.BookDefinition;

import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<BookDefinition, Long> {

    @EntityGraph(value = BookDefinition.GRAPH_FETCH_ALL)
    @Query("SELECT b FROM BookDefinition b WHERE b.id = :id")
    Optional<BookDefinition> fetchById(@Param("id") Long id);
}