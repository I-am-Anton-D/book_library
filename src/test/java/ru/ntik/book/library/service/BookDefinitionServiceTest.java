package ru.ntik.book.library.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.ntik.book.library.domain.BookDefinition;
import ru.ntik.book.library.domain.Category;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@SpringBootTest
@ActiveProfiles("h2")
@Transactional
class BookDefinitionServiceTest {

    @Autowired
    BookDefinitionService bookDefinitionService;
    @Autowired
    CategoryService categoryService;

    @DisplayName("Add main image")
    @Test
    void mainImageAdd() {
//        byte[] imgBytes = getImgBytes();
//        assert imgBytes != null;
//        int size = imgBytes.length;
//        BookDefinition bd = bookDefinitionService.addMainImage(imgBytes, 1L);
//
//        assertThat(bd.getMainImage()).hasSize(size);
//        assertThrows(IllegalArgumentException.class, () -> bookDefinitionService.addMainImage(null, 1L));
//        assertThrows(EntityNotFoundException.class, () -> bookDefinitionService.addMainImage(imgBytes, 5000L));
    }

    @DisplayName("Ищем по id")
    @Test
    void findById() {
        assertThat(bookDefinitionService.findById(1L)).isNotNull();
        assertThat(bookDefinitionService.findById(10000)).isNull();
    }

    @DisplayName("Ищем по категориям")
    @Test
    void findByCategories() {
        Category catA = categoryService.findById(18L);
        Category subCat = categoryService.findById(21L);
        List<Category> singleCategory = List.of(catA);
        List<Category> multipleCategories = List.of(catA, subCat);

        assertThatCode(()->bookDefinitionService.findByCategories(singleCategory)).doesNotThrowAnyException();
        assertThat(bookDefinitionService.findByCategories(singleCategory)).size().isEqualTo(3);
        assertThat(bookDefinitionService.findByCategories(multipleCategories)).size().isEqualTo(3 + 1);
    }

    @DisplayName("Проверка на пустоту")
    @Test
    void testIsEmpty() {
        BookDefinition bookWithInstances = bookDefinitionService.findById(1);
        BookDefinition bookWithoutInstances = bookDefinitionService.findById(3);
        assertThatCode(()->bookDefinitionService.isEmpty(bookWithInstances)).doesNotThrowAnyException();
        assertThat(bookDefinitionService.isEmpty(bookWithInstances)).isFalse();
        assertThat(bookDefinitionService.isEmpty(bookWithoutInstances)).isTrue();
    }

    @DisplayName("Удаляем")
    @Test
    void testDelete() {
        BookDefinition bookWithInstances = bookDefinitionService.findById(1);
        BookDefinition bookWithoutInstances = bookDefinitionService.findById(3);
        assertThatCode(()->bookDefinitionService.remove(bookWithoutInstances)).doesNotThrowAnyException();
        assertThat(bookDefinitionService.findById(bookWithoutInstances.getId())).isNull();

        // books with instances should be deleted as well, with all of their instances
        assertThatCode(()->bookDefinitionService.remove(bookWithInstances)).doesNotThrowAnyException();
        assertThat(bookDefinitionService.findById(bookWithoutInstances.getId())).isNull();
        /* TODO: found no clean way to test that instances got removed with definition
         * Ideas:
         * - New service / method in BookDefinitionService to find instances by ID
         *      Bad, because it would be used just once - in this testq
         * - Native query
         *      Bad, because spring is all about avoiding native queries
         */
    }
}