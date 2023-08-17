package ru.ntik.book.library.repository;

import io.github.yashchenkon.assertsqlcount.test.AssertSqlQueriesCount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.annotation.Commit;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.ntik.book.library.domain.BookDefinition;
import ru.ntik.book.library.domain.Category;
import ru.ntik.book.library.domain.PrintInfo;
import ru.ntik.book.library.domain.enums.BookLanguage;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.ntik.book.library.testutils.TestUtils.*;

@SpringBootTest
@ActiveProfiles("h2")
@Transactional
@DirtiesContext

class BookRepositoryTest {

    @Autowired
    BookRepository bookRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @BeforeEach
    void resetSqlCount() {
        AssertSqlQueriesCount.reset();
    }

    @DisplayName("Поищем по id")
    @Test
    void findByIdTest() {
         BookDefinition bd = bookRepository.findById(1L).orElse(null);
        assertThat(bd).isNotNull();

        AssertSqlQueriesCount.assertSelectCount(1);

        bd = bookRepository.findById(2L).orElse(null);
        assertThat(bd).isNotNull();

        bd = bookRepository.findById(-2L).orElse(null);
        assertThat(bd).isNull();

        AssertSqlQueriesCount.assertSelectCount(3);
    }

    @DisplayName("Проверим L1 cache")
    @Test
    void checkL1cache() {
        BookDefinition bd = bookRepository.findById(1L).orElse(null);
        assertThat(bd).isNotNull();
        bd.setName(bd.getName() + bd.getName());

        BookDefinition bd2 = bookRepository.findById(1L).orElse(null);
        assertThat(bd2).isNotNull();
        bd2.setName(bd2.getName() + bd2.getName());

        //Те же самые объекты
        assertThat(bd).isEqualTo(bd2);
        assertThat(bd.getName()).isEqualTo(bd.getName());
        assertThat(bd).isSameAs(bd2);

        BookDefinition bd3 = bookRepository.findById(1L).orElse(null);
        assertThat(bd3).isNotNull();
        assertThat(bd).isSameAs(bd3);

        AssertSqlQueriesCount.assertSelectCount(1);
    }

    @DisplayName("Поищем все")
    @Test
    void findAllTest() {
        List<BookDefinition> bds = bookRepository.findAll();
        assertThat(bds).isNotEmpty();

        AssertSqlQueriesCount.assertSelectCount(1);
    }


    @DisplayName("Должен сохранить объект")
    @Test
    void saveEntityTest() {
        Category category = categoryRepository.findRoot();
        assertThat(category).isNotNull();
        AssertSqlQueriesCount.reset();

        BookDefinition bd = new BookDefinition(BOOK_NAME, BOOK_DESC, CREATOR,
                new PrintInfo(RELEASE_YEAR, COVER_TYPE, ISBN, PAGE_COUNT, BOOK_LANGUAGE, null), Collections.emptyList(), category);

        bd = bookRepository.save(bd);
        bookRepository.flush();

        assertThat(bd.getId()).isNotNull();
        assertThat(bd.getCreator()).isNotNull().isPositive();

        AssertSqlQueriesCount.assertInsertCount(1);
        AssertSqlQueriesCount.assertSelectCount(1);
    }

    @DisplayName("Вносим изменения")
    @Test
    void mergeTest() {
        BookDefinition bd = bookRepository.findById(1L).orElse(null);
        assertThat(bd).isNotNull();
        AssertSqlQueriesCount.assertSelectCount(1);

        bd.setName("New name");
        bd.getPrintInfo().setPageCount(100);

        //no flush here, no update
        bd = bookRepository.save(bd);
        AssertSqlQueriesCount.assertInsertCount(0);

        //here 1 update. save dirty and flush
        bookRepository.saveAndFlush(bd);
        AssertSqlQueriesCount.assertUpdateCount(1);
    }

    @DisplayName("Удаляем объект")
    @Test
    void removeTest() {
        BookDefinition bd = bookRepository.findById(1L).orElse(null);
        assertThat(bd).isNotNull();

        bookRepository.delete(bd);
        AssertSqlQueriesCount.assertDeleteCount(0);
        bookRepository.flush();
        //1 - Один DELETE удалит книгу
        //2 - Удаляет связь авторов с книгами
        //3 - Удаляет связь с другой книгой
        //4 - Удаляет instance и status
        AssertSqlQueriesCount.assertDeleteCount(4);

        assertThatCode(() -> bookRepository.deleteById(-20L)).doesNotThrowAnyException();
    }

    @DisplayName("Проверяем Optimistic lock на версии")
    @Test
    void checkOptimisticLock() throws InterruptedException {

        BookDefinition bd = bookRepository.findById(3L).orElse(null);
        assertThat(bd).isNotNull();

        //Start in new tread fon new persistence context
        Thread one = new Thread(this::updateInNewTransaction);
        one.start();
        one.join();

        bd.setName("Cool name");
        bd.getPrintInfo().setPageCount(1000);
        assertThrows(ObjectOptimisticLockingFailureException.class, () -> bookRepository.saveAndFlush(bd));
    }

    @Commit
    //АККУРАТНО, объект с id = 3 сохранится в ходе тестов происходит КОММИТ, не роллбек
    void updateInNewTransaction() {
        //Select here
        BookDefinition bd = bookRepository.findById(3L).orElse(null);
        assertThat(bd).isNotNull();
        bd.setName("new version");

        //Select here again
        bookRepository.saveAndFlush(bd);
    }

    @DisplayName("Нет каскада на Links")
    @Test
    void noCascadeForLinks() {
        BookDefinition bd = bookRepository.findById(1L).orElse(null);
        assertThat(bd).isNotNull();

        Category category = categoryRepository.findRoot();
        assertThat(category).isNotNull();

        BookDefinition newBook = new BookDefinition("Some book", null, 10L,
                new PrintInfo(2020, null,null,10, BookLanguage.RUSSIAN, null), Collections.emptyList(), category);
        boolean added = bd.getLinks().add(newBook);
        assertThat(added).isTrue();

        bookRepository.save(bd);

        //Nothing changing, only persisted book can be linked
        AssertSqlQueriesCount.assertInsertCount(0);
        AssertSqlQueriesCount.assertUpdateCount(0);

    }

}