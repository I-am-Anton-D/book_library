package ru.ntik.book.library.service;

import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import org.springframework.transaction.annotation.Transactional;
import ru.ntik.book.library.domain.Category;
import ru.ntik.book.library.repository.CategoryRepository;

import java.util.List;
import java.util.Optional;


@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;
    /**
     * If no root category exists, method creates it and returns true,<br>
     * otherwise method does nothing and returns false.
     */
    @PostConstruct
    public boolean tryCreateRootCategory() {
        if(this.categoryRepository.findRoot() == null) {
            this.categoryRepository.save(Category.createRootCategory("Все",
                    "Список всех доступных книг", 0L));
            return true;
        }
        return false;
    }

    public CategoryService(CategoryRepository categoryRepository) { this.categoryRepository = categoryRepository;}

    @Transactional
    public List<Category> fetchAll() {
        return categoryRepository.findAll();
    }

    public Optional<Category> find(long id) { return categoryRepository.findById(id); }
    public Category findRoot() { return categoryRepository.findRoot(); }
    public void save(Category category) {
        this.categoryRepository.saveAndFlush(category);
    }

    public void remove(Category category) {
        this.categoryRepository.delete(category);
        this.categoryRepository.flush();
    }
}
