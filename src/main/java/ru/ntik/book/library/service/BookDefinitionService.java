package ru.ntik.book.library.service;

import jakarta.persistence.EntityNotFoundException;
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

    public BookDefinition addMainImage(byte[] imageBytes,long id) {
        BookDefinition bd = findById(id);
        if (imageBytes == null)
            throw new IllegalArgumentException("Images bytes is null!");

        if (bd == null) throw
                new EntityNotFoundException("Entity with id" + id  + "not found");

        bd.setMainImage(imageBytes);
        return bookRepository.save(bd);
    }

    public BookDefinition findById(long id) {
        return bookRepository.findById(id).orElse(null);
    }
}