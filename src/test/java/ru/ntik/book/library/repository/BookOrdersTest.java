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
import ru.ntik.book.library.domain.BookOrder;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("h2")
@Transactional
class BookOrdersTest {

    @Autowired
    BookRepository bookRepository;

    @BeforeEach
    void resetSqlCount() {
        AssertSqlQueriesCount.reset();
    }

    @DisplayName("Orders test")
    @Test
    void fetchSaveAndDelete() {
        BookDefinition bd = bookRepository.findById(1L).orElse(null);
        assertThat(bd).isNotNull();
        AssertSqlQueriesCount.assertSelectCount(1);

        assertThat(bd.getBookOrders()).isNotEmpty();
        AssertSqlQueriesCount.assertSelectCount(2);

        bd.getBookOrders().add(new BookOrder(12L, bd));
        bd = bookRepository.save(bd);
        bookRepository.flush();

        AssertSqlQueriesCount.assertInsertCount(1);
        AssertSqlQueriesCount.assertUpdateCount(0);


        BookOrder removed = bd.getBookOrders().iterator().next();
        assertThat(removed.getBookDefinition()).isNotNull();
        AssertSqlQueriesCount.assertSelectCount(3);

        bd.getBookOrders().remove(removed);
        bd = bookRepository.save(bd);
        bookRepository.flush();
        AssertSqlQueriesCount.assertDeleteCount(1);
        AssertSqlQueriesCount.assertUpdateCount(0);
    }

}
