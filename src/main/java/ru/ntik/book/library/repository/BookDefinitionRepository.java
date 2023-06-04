package ru.ntik.book.library.repository;

import org.springframework.stereotype.Repository;
import ru.ntik.book.library.domain.BookDefinition;
import ru.ntik.book.library.repository.abstracts.AbstractDao;

@Repository
public class BookDefinitionRepository extends AbstractDao<BookDefinition> {

    public BookDefinitionRepository() {
        setEntityClass(BookDefinition.class);
    }
}