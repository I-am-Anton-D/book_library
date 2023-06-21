package ru.ntik.book.library.repository;

import io.github.yashchenkon.assertsqlcount.test.AssertSqlQueriesCount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.ntik.book.library.domain.BookDefinition;
import ru.ntik.book.library.domain.Category;

import java.util.Iterator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("h2")
@Transactional

class CategoryRepositoryTest {


    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    BookRepository bookRepository;

    @BeforeEach
    void resetSqlCount() {
        AssertSqlQueriesCount.reset();
    }

    @DisplayName("Find root")
    @Test
    void findRoot() {
        Category cat = categoryRepository.findRoot();
        assertThat(cat).isNotNull();
        AssertSqlQueriesCount.assertSelectCount(1);

        assertThat(cat.getChildren()).isNotEmpty();
        AssertSqlQueriesCount.assertSelectCount(2);

        assertThat(cat.getParent()).isNull();
        AssertSqlQueriesCount.assertSelectCount(2);
    }

    @DisplayName("Find lazy with books and child and parent")
    @Test
    void lazyLoad() {
        Category cat = categoryRepository.findById(18L).orElse(null);
        assertThat(cat).isNotNull();
        AssertSqlQueriesCount.assertSelectCount(1);

        assertThat(cat.getParent().getName()).isNotNull();
        AssertSqlQueriesCount.assertSelectCount(2);

        assertThat(cat.getChildren()).isNotEmpty();
        AssertSqlQueriesCount.assertSelectCount(3);

        assertThat(cat.getBooks()).isNotEmpty();
        AssertSqlQueriesCount.assertSelectCount(4);
    }

    @DisplayName("Eager load")
    @Test
    void eagerLoad() {
        Category cat = categoryRepository.fetchById(18L).orElse(null);
        assertThat(cat).isNotNull();
        AssertSqlQueriesCount.assertSelectCount(1);

        assertThat(cat.getParent().getName()).isNotNull();
        AssertSqlQueriesCount.assertSelectCount(1);

        assertThat(cat.getChildren()).isNotEmpty();
        AssertSqlQueriesCount.assertSelectCount(1);

        assertThat(cat.getBooks()).isNotEmpty();
        AssertSqlQueriesCount.assertSelectCount(1);
    }

    @DisplayName("Eeger Load root but lazy children")
    @Test void eagerRootButLazyLoadChildren() {
        Category cat = categoryRepository.fetchRoot();
        assertThat(cat).isNotNull();
        AssertSqlQueriesCount.assertSelectCount(1);

        assertThat(cat.getChildren()).isNotEmpty();
        AssertSqlQueriesCount.assertSelectCount(1);

        Iterator<Category> iterator = cat.getChildren().iterator();
        Category childWithChildren = iterator.next();

        assertThat(childWithChildren.getChildren()).isNotEmpty();
        AssertSqlQueriesCount.assertSelectCount(2);

        assertThat(childWithChildren.getBooks()).isNotEmpty();
        AssertSqlQueriesCount.assertSelectCount(3);
    }

    @DisplayName("Извлекаем дерево категорий 1 нативным запросом")
    @Test
    void fetchCategoryTree() {
        List<Object[]> result = categoryRepository.fetchCategoryTree();
        assertThat(result).isNotEmpty();
    }

    @DisplayName("Создаем категорию от рута")
    @Test
    void createCategoryFromRoot() {
        Category root = categoryRepository.findRoot();

        Category newCategory = new Category("New cat", null, 10L, root );

        categoryRepository.save(newCategory);
        categoryRepository.flush();

        AssertSqlQueriesCount.assertInsertCount(1);
        AssertSqlQueriesCount.assertUpdateCount(0);
    }

    @DisplayName("Не удалить с дочерними категориями")
    @Test
    void canNotRemoveWithChildren() {
        Category cat = categoryRepository.findById(18L).orElse(null);
        assertThat(cat).isNotNull();

        assertThrows(InvalidDataAccessApiUsageException.class, ()-> categoryRepository.delete(cat));
    }

    @DisplayName("Не удалить с книгами")
    @Test
    void canNotRemoveWithBooks() {
        Category cat = categoryRepository.findById(21L).orElse(null);
        assertThat(cat).isNotNull();

        assertThrows(InvalidDataAccessApiUsageException.class, ()-> categoryRepository.delete(cat));
    }

    @DisplayName("Пустую удалить можно")
    @Test
    void deleteEmptyCategory() {
        Category cat = categoryRepository.findById(22L).orElse(null);
        assertThat(cat).isNotNull();

        assertThatCode(()-> categoryRepository.delete(cat)).doesNotThrowAnyException();
    }

    @DisplayName("Lazy load категории у книги")
    @Test
    void lazyLoadBookCategory() {
        final BookDefinition bd = bookRepository.findById(1L).orElse(null);
        assertThat(bd).isNotNull();
        AssertSqlQueriesCount.assertSelectCount(1);

        assertThat(bd.getCategory().getName()).isNotNull();
        AssertSqlQueriesCount.assertSelectCount(2);
    }

    @DisplayName("Save with reference")
    @Test
    void saveByParentReference() {
        Category parent = categoryRepository.getReferenceById(22L);
        assertThat(parent).isNotNull();
        AssertSqlQueriesCount.assertSelectCount(0);

        Category newCategory = new Category("New Category", null, 10L, parent);

        categoryRepository.save(newCategory);
        categoryRepository.flush();
        AssertSqlQueriesCount.assertInsertCount(1);
        AssertSqlQueriesCount.assertUpdateCount(0);
    }

    @DisplayName("Save with reference to not exist object")
    @Test
    void saveByParentWithWrongReference() {
        Category parent = categoryRepository.getReferenceById(220L);
        assertThat(parent).isNotNull();
        AssertSqlQueriesCount.assertSelectCount(0);

        Category newCategory = new Category("New Category", null, 10L, parent);

        categoryRepository.save(newCategory);
        assertThrows(DataIntegrityViolationException.class,() -> categoryRepository.flush());
    }

    @DisplayName("N + 1 select")
    @Test
    void NplusOneNoProblem() {
        Category cat = categoryRepository.findById(18L).orElse(null);
        assertThat(cat).isNotNull();
        AssertSqlQueriesCount.assertSelectCount(1);


        cat.getBooks().forEach(b->{
            assertThat(b.getName()).isNotNull();
        });

        AssertSqlQueriesCount.assertSelectCount(2);
    }
}
