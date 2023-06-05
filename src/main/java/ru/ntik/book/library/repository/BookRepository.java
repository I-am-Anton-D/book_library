package ru.ntik.book.library.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.ntik.book.library.domain.BookDefinition;
@Repository
public interface BookRepository extends JpaRepository<BookDefinition, Long> {}