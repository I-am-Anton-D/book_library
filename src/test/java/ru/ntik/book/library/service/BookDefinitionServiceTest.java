package ru.ntik.book.library.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.ntik.book.library.domain.BookDefinition;

import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("h2")
@Transactional
class BookDefinitionServiceTest {

    @Autowired
    BookDefinitionService bookDefinitionService;

    @DisplayName("Add main image")
    @Test
    void mainImageAdd() {
        byte[] imgBytes = getImgBytes();
        assert imgBytes != null;
        int size = imgBytes.length;
        BookDefinition bd = bookDefinitionService.addMainImage(imgBytes, 1L);

        assertThat(bd.getMainImage()).hasSize(size);
        assertThrows(IllegalArgumentException.class, () -> bookDefinitionService.addMainImage(null, 1L));
        assertThrows(EntityNotFoundException.class, () -> bookDefinitionService.addMainImage(imgBytes, 5000L));
    }

    @DisplayName("Ищем по id")
    @Test
    void findById() {
        assertThat(bookDefinitionService.findById(1L)).isNotNull();
        assertThat(bookDefinitionService.findById(10000)).isNull();
    }

    private byte[] getImgBytes() {
        String fileName = "main.jpg";
        try (InputStream stream = getClass().getClassLoader().getResourceAsStream(fileName)) {
            assert stream != null;
            return stream.readAllBytes();
        } catch (Exception ignored) {
        }
        return null;
    }
}