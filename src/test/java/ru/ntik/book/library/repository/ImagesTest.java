package ru.ntik.book.library.repository;

import io.github.yashchenkon.assertsqlcount.test.AssertSqlQueriesCount;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Commit;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.ntik.book.library.domain.BookDefinition;
import ru.ntik.book.library.domain.BookImage;
import ru.ntik.book.library.testutils.TestUtils;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("h2")
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ImagesTest {
    @Autowired
    BookRepository bookRepository;

    @BeforeEach
    void resetSelCount() {
        AssertSqlQueriesCount.reset();
    }


    @DisplayName("Add mainImage and not")
    @Test
    @Commit
    @Order(1)
    void addImages() {
        BookDefinition bd = bookRepository.findById(1L).orElse(null);
        assertThat(bd).isNotNull();
        AssertSqlQueriesCount.assertSelectCount(1);

        MediaType type = MediaType.IMAGE_JPEG;

        String fileName = "main.jpg";
        byte[] bytes = TestUtils.getImgBytes(fileName, getClass().getClassLoader());
        BookImage mainImage = new BookImage(10L, fileName, bytes, true, bd);

        fileName = "image2.jpg";
        bytes = TestUtils.getImgBytes(fileName, getClass().getClassLoader());
        BookImage anotherImage = new BookImage(10L, fileName, bytes, false, bd);

        bd.getImages().add(mainImage);
        bd.getImages().add(anotherImage);
        bookRepository.save(bd);
        bookRepository.flush();
        AssertSqlQueriesCount.assertInsertCount(1);
        AssertSqlQueriesCount.assertUpdateCount(0);
    }

    @DisplayName("get main image")
    @Test
    @Commit
    @Order(2)
    void getMainImage() {
        BookDefinition bd = bookRepository.findById(1L).orElse(null);
        assertThat(bd).isNotNull();
        AssertSqlQueriesCount.assertSelectCount(1);

        BookImage mainImage = bd.getMainImage();
        assertThat(mainImage.getFileName()).isEqualTo("main.jpg");
        assertThat(mainImage.getBytes()).isNotEmpty();
        assertThat(mainImage.getBookDefinition().getName()).isNotNull();
        assertThat(mainImage.isMainImage()).isTrue();
    }



}
