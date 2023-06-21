package ru.ntik.book.library.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.ntik.book.library.domain.BookDefinition;

import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<BookDefinition, Long> {

    //EAGER ALL LOAD
    @Query("SELECT b FROM BookDefinition b " +
            "join fetch b.printInfo.publisher " +
            "join fetch b.authors " +
            "join fetch b.category " +
            "join fetch b.links " +
            "join fetch b.instances " +
            "join fetch b.reviews WHERE b.id = :id")
    Optional<BookDefinition> fetchById(@Param("id") Long id);
}