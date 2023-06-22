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
import ru.ntik.book.library.domain.BookInstance;
import ru.ntik.book.library.domain.enums.BookInstanceState;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("h2")
@Transactional
class BookInstanceAndStatusTest {

    @Autowired
    BookRepository bookRepository;

    @BeforeEach
    void resetSelCount() {
        AssertSqlQueriesCount.reset();
    }

    @DisplayName("Lazy load")
    @Test
    void lazyLoadBookInstance() {
        final BookDefinition bd = bookRepository.findById(1L).orElse(null);
        assertThat(bd).isNotNull();
        AssertSqlQueriesCount.assertSelectCount(1);

        assertThat(bd.getInstances()).isNotEmpty();
        AssertSqlQueriesCount.assertSelectCount(2);

        BookInstance bi = bd.getInstances().iterator().next();
        assertThat(bi.getBookDefinition().getName()).isNotNull();
        AssertSqlQueriesCount.assertSelectCount(2);

        assertThat(bi.getOwner()).isNotNull();
        assertThat(bi.isCompany()).isFalse();
    }

    @DisplayName("Каскадно создаем BookInstance")
    @Test
    void cascadeCreateBookInstance() {
        final BookDefinition bd = bookRepository.findById(1L).orElse(null);
        assertThat(bd).isNotNull();
        AssertSqlQueriesCount.assertSelectCount(1);

        int instanceCountBefore = bd.getInstancesInfo().getInstanceCount();

        BookInstance bi = new BookInstance(12L, 10L, false, bd);
        BookInstance duplicate = bd.getInstances().iterator().next();

        //Not added duplicate in set
        bd.addBookInstance(duplicate);
        assertThat(bd.getInstancesInfo().getInstanceCount()).isEqualTo(instanceCountBefore);

        bd.addBookInstance(bi);
        assertThat(bd.getInstancesInfo().getInstanceCount()).isEqualTo(instanceCountBefore + 1);

        bookRepository.save(bd);
        bookRepository.flush();

        AssertSqlQueriesCount.assertInsertCount(2);
        AssertSqlQueriesCount.assertUpdateCount(1);
    }

    @DisplayName("Каскадно удаляем BookInstance")
    @Test
    void cascadeDeleteBookInstance() {
        final BookDefinition bd = bookRepository.findById(1L).orElse(null);
        assertThat(bd).isNotNull();
        AssertSqlQueriesCount.assertSelectCount(1);

        int instanceCountBefore = bd.getInstancesInfo().getInstanceCount();

        assertThrows(NullPointerException.class, ()->bd.removeReview(null));
        assertThat(bd.removeBookInstance(new BookInstance(12L, 10L, false, bd))).isFalse();

        assertThat(bd.getInstances()).isNotEmpty();
        BookInstance bi = bd.getInstances().iterator().next();
        bd.removeBookInstance(bi);
        assertThat(bd.getInstancesInfo().getInstanceCount()).isEqualTo(instanceCountBefore - 1);
        bookRepository.save(bd);
        bookRepository.flush();

        AssertSqlQueriesCount.assertDeleteCount(2);
        AssertSqlQueriesCount.assertUpdateCount(1);
    }

    @DisplayName("Move to User and back to Owner")
    @Test
    void moveToUserAndBack() {
        BookDefinition bd = bookRepository.findById(1L).orElse(null);
        assertThat(bd).isNotNull();
        AssertSqlQueriesCount.assertSelectCount(1);

        assertThat(bd.getInstancesInfo().getInstanceCount()).isNotZero();
        int free = bd.getInstancesInfo().getFreeCount();
        int count = bd.getInstancesInfo().getInstanceCount();

        BookInstance bi = bd.getInstances().iterator().next();
        AssertSqlQueriesCount.assertSelectCount(2);
        bi.moveToUser(12L);

        assertThat(bd.getInstancesInfo().getFreeCount()).isEqualTo(free - 1);
        bd = bookRepository.save(bd);
        bookRepository.flush();

        AssertSqlQueriesCount.assertUpdateCount(2);

        bi = bd.getInstances().iterator().next();
        assertThat(bi.getStatus().getState()).isEqualTo(BookInstanceState.ON_USER);
        assertThat(bi.getStatus().getToUser()).isEqualTo(12L);
        assertThat(bi.getStatus().getMovedToUserDate()).isNotNull();
        assertThat(bi.getStatus().getMovedToOwnerDate()).isNull();

        BookInstance finalBi = bi;
        assertThrows(IllegalStateException.class, () -> finalBi.moveToUser(12L));
        assertThrows(NullPointerException.class, ()-> finalBi.moveToUser(null));
        assertThatCode(()->finalBi.moveToUser(14L)).doesNotThrowAnyException();

        bi.moveToOwner();
        assertThat(bd.getInstancesInfo().getFreeCount()).isEqualTo(free);

        assertThat(bi.getStatus().getState()).isEqualTo(BookInstanceState.ON_OWNER);
        assertThat(bi.getStatus().getToUser()).isNull();
        assertThat(bi.getStatus().getMovedToUserDate()).isNull();
        assertThat(bi.getStatus().getMovedToOwnerDate()).isNotNull();

        bookRepository.save(bd);
        bookRepository.flush();
        AssertSqlQueriesCount.assertUpdateCount(4);

        bi.moveToUser(12L);
        bd.removeBookInstance(bi);
        bd = bookRepository.save(bd);
        bookRepository.flush();

        assertThat(bd.getInstancesInfo().getInstanceCount()).isEqualTo(count - 1);
    }
}
