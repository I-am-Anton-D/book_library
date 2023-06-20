package ru.ntik.book.library.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import ru.ntik.book.library.testutils.TestUtils;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


@Execution(ExecutionMode.CONCURRENT)
class CategoryTest {

    @DisplayName("Создаем экземпляр")
    @Test
    void createInstance() {
        Category category = new Category("Cat 1", null, 10L, new Category());
        assertThat(category).isNotNull();

        assertThrows(NullPointerException.class, ()-> new Category("Cat 2", null, 20L, null));
    }

    @DisplayName("Неизменяемые колекции")
    @Test
    void unmodifiedCollections() {
        Category category = new Category("Cat 1", null, 10L, new Category());

        Set<Category> children = category.getChildren();
        assertThrows(UnsupportedOperationException.class, () -> children.add(new Category()));

        Set<BookDefinition> books = category.getBooks();
        assertThrows(UnsupportedOperationException.class, ()->books.add(TestUtils.createBookDefinition()));
    }

}