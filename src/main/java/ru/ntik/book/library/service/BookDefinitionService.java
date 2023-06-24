package ru.ntik.book.library.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ntik.book.library.domain.BookDefinition;
import ru.ntik.book.library.repository.BookRepository;

@Service
@Transactional
public class BookDefinitionService {

    private final BookRepository bookRepository;

    public BookDefinitionService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public BookDefinition findById(long id) {
        return bookRepository.findById(id).orElse(null);
    }
}