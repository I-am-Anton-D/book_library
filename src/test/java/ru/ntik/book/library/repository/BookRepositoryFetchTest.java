package ru.ntik.book.library.repository;

import io.github.yashchenkon.assertsqlcount.test.AssertSqlQueriesCount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.ntik.book.library.domain.BookDefinition;
import ru.ntik.book.library.domain.Publisher;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("h2")
@Transactional

class BookRepositoryFetchTest {

    @Autowired
    BookRepository bookRepository;

    @Autowired
    PublisherRepository publisherRepository;

    @BeforeEach
    void resetSelCount() {
        AssertSqlQueriesCount.reset();
    }

    @DisplayName("Вытаскиваем издательство Lazy")
    @Test
    void lazyFetchPublisher() {
        BookDefinition bd = bookRepository.findById(1L).orElse(null);
        assertThat(bd).isNotNull();
        AssertSqlQueriesCount.assertSelectCount(1);

        //No Lazy load here
        Publisher publisher = bd.getPublisher();
        assertThat(publisher).isNotNull();
        AssertSqlQueriesCount.assertSelectCount(1);

        //Lazy load here
        String name = publisher .getName();
        assertThat(name).isNotNull();
        AssertSqlQueriesCount.assertSelectCount(2);
    }

    @DisplayName("Каскадный персист проверяем")
    @Test
    void cascadeFetchTest() {
        BookDefinition bd = bookRepository.findById(1L).orElse(null);
        assertThat(bd).isNotNull();
        AssertSqlQueriesCount.assertSelectCount(1);

        Publisher publisher = new Publisher("New Pub", null, 10L);
        bd.setPublisher(publisher);

        bd = bookRepository.save(bd);
        AssertSqlQueriesCount.assertInsertCount(1);
        AssertSqlQueriesCount.assertUpdateCount(0);

        bookRepository.flush();
        AssertSqlQueriesCount.assertUpdateCount(1);

        assertThat(bd.getPublisher().getId()).isNotNull();
        Publisher saved = publisherRepository.findById(bd.getPublisher().getId()).orElse(null);
        assertThat(saved).isNotNull();
    }


    @DisplayName("Каскадный мерж")
    @Test
    void cascadeMerge() {
        BookDefinition bd = bookRepository.findById(1L).orElse(null);
        assertThat(bd).isNotNull();
        AssertSqlQueriesCount.assertSelectCount(1);

        bd.getPublisher().setName("New Name");
        bookRepository.save(bd);
        AssertSqlQueriesCount.assertUpdateCount(0);

        bookRepository.flush();
        AssertSqlQueriesCount.assertUpdateCount(1);
    }
}