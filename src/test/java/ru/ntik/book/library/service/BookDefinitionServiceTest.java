package ru.ntik.book.library.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("h2")
@Transactional
class BookDefinitionServiceTest {

    @Autowired
    BookDefinitionService bookDefinitionService;

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


}