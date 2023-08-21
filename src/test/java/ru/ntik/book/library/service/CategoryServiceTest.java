package ru.ntik.book.library.service;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.ntik.book.library.domain.Category;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@SpringBootTest
@ActiveProfiles("h2")
@Transactional
class CategoryServiceTest {
    @Autowired
    CategoryService categoryService;

    @DisplayName("Поверхностный тест")
    @Test
    void smokeTest() {
        assertThat(categoryService).isNotNull();
        assertThatCode(()->categoryService.find(1)).doesNotThrowAnyException();
    }

    @DisplayName("Создание корневой категории")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    @Test
    void testRootCategory() {
        assertThatCode(categoryService::tryCreateRootCategory).doesNotThrowAnyException();
        assertThat(categoryService.findRoot()).isNotNull();
        assertThat(categoryService.tryCreateRootCategory()).isFalse();
    }

    @DisplayName("Сохранение категории")
    @DirtiesContext
    @Test
    void testSaving() {
        Category parent = categoryService.findRoot();
        Category category = new Category(
                "Test category",
                "Тестовая категория для проверки записи",
                0L,
                parent);
        assertThatCode(()->categoryService.save(category)).doesNotThrowAnyException();
        Long CategoryId = category.getId();

        Category testCategory = categoryService.find(CategoryId);
        assertThat(testCategory).isNotNull();
    }

    @DisplayName("Загрузка категории")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    @Test
    void testLoading() {
        assertThatCode(()->categoryService.find(19)).doesNotThrowAnyException();
        Category testCategory = categoryService.find(19);
        assertThat(testCategory).isNotNull();
        assertThat(testCategory.getName()).isEqualTo("cat B");
        assertThat(testCategory.getParent()).isNotNull();
    }

    @DisplayName("Удаление категории")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Commit
    @Test
    void testRemoving() {
        Category testCategory = categoryService.find(22);
        assertThat(testCategory).isNotNull();

        assertThatCode(()->categoryService.remove(categoryService.find(22))).doesNotThrowAnyException();

        testCategory = categoryService.find(22);
        assertThat(testCategory).isNull();
    }
}