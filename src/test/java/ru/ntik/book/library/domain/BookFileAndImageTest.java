package ru.ntik.book.library.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import ru.ntik.book.library.domain.enums.FileType;
import ru.ntik.book.library.testutils.TestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Execution(ExecutionMode.CONCURRENT)
class BookFileAndImageTest {

    @DisplayName("Book file test")
    @Test
    void bookFileTest() {
        BookFile bf = new BookFile("Cool Book", null, 10L,
                FileType.EPUB, "C:/", "myBook.epub");
        assertThat(bf).isNotNull();
        assertThat(bf.getType()).isEqualTo(FileType.EPUB);
        assertThat(bf.getLocation()).isEqualTo("C:/");
        assertThat(bf.getFileName()).isEqualTo("myBook.epub");


        assertThrows(NullPointerException.class, ()-> new BookFile("Cool Book", null, 10L,
                null, "C:/", "myBook.epub"));
        assertThrows(NullPointerException.class, ()-> new BookFile("Cool Book", null, 10L,
                FileType.EPUB, null, "myBook.epub"));
        assertThrows(NullPointerException.class, ()-> new BookFile("Cool Book", null, 10L,
                FileType.EPUB, "C:/", null));
    }

    @DisplayName("Book image test")
    @Test
    void bookImageTest() {
        String fileName = "main.jpg";
        byte[] bytes = TestUtils.getImgBytes(fileName, getClass().getClassLoader());
        BookImage image = new BookImage(10L, fileName, bytes, true, new BookDefinition());
        assertThat(image).isNotNull();

        assertThrows(NullPointerException.class, ()-> new BookImage(10L, null, bytes, true, new BookDefinition()));
        assertThrows(NullPointerException.class, ()-> new BookImage(10L, fileName, null, true, new BookDefinition()));
        assertThrows(NullPointerException.class, ()-> new BookImage(10L, fileName, bytes, true, null));
    }
}