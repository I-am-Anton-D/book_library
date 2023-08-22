package ru.ntik.book.library.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import org.springframework.transaction.annotation.Transactional;
import ru.ntik.book.library.domain.Category;
import ru.ntik.book.library.repository.CategoryRepository;

import java.util.List;


@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    /**
     * If no root category exists, method creates it and returns true,<br>
     * otherwise method does nothing and returns false.
     */
    @PostConstruct
    public void tryCreateRootCategory() {
        if (this.categoryRepository.findRoot() == null) {
            this.categoryRepository.save(Category.createRootCategory("Все",
                    "Список всех доступных книг", 0L));
        }
    }

    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    /**
     * @param id for desired category
     * @return Category object or null
     */
    public Category findById(long id) {
        return categoryRepository.findById(id).orElse(null);
    }

    public Category findRoot() {
        return categoryRepository.findRoot();
    }

    public void save(Category category) {
        this.categoryRepository.save(category);
    }

    public void remove(Category category) {
        this.categoryRepository.delete(category);
    }
}
