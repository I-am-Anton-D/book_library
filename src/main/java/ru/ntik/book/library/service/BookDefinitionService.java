package ru.ntik.book.library.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ntik.book.library.domain.BookDefinition;
import ru.ntik.book.library.domain.Category;
import ru.ntik.book.library.repository.BookRepository;

import java.util.ArrayList;
import java.util.List;

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

    public List<BookDefinition> findByCategories(List<Category> categories) {
        if (!categories.isEmpty()) {
            return bookRepository.findByCategories(categories);
        } else {
          return new ArrayList<>();
        }
    }

    public List<BookDefinition> findAll() {
        return bookRepository.findAll();
    }
}