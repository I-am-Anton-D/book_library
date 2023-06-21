package ru.ntik.book.library.repository;

import io.github.yashchenkon.assertsqlcount.test.AssertSqlQueriesCount;
import org.hibernate.SessionFactory;
import org.hibernate.stat.CacheRegionStatistics;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.ntik.book.library.domain.BookDefinition;
import ru.ntik.book.library.domain.Category;
import ru.ntik.book.library.domain.PrintInfo;
import ru.ntik.book.library.util.Constants;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.ntik.book.library.testutils.TestUtils.*;


@SpringBootTest
@ActiveProfiles("h2l2on")
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)

class BookRepositoryCacheTest {

    @Autowired
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    private SessionFactory sessionFactory;
    @Autowired
    BookRepository bookRepository;
    @Autowired
    CategoryRepository categoryRepository;

    private CacheRegionStatistics cache;

    @BeforeAll
    void init() {
        cache = sessionFactory.getStatistics().getCacheRegionStatistics(Constants.BOOK_DEFINITION_REGION_NAME);
    }

    @BeforeEach
    void resetSqlCount() {
        AssertSqlQueriesCount.reset();
    }


    @DisplayName("Заполняем кешь через findAll")
    @Order(1)
    @Test

    void findAllInCache() {
        List<BookDefinition> bds = bookRepository.findAll();
        assertThat(bds).isNotEmpty();
        int len = bds.size();

        assertThat(cache.getPutCount()).isEqualTo(len);
        AssertSqlQueriesCount.assertSelectCount(1);
    }

    @DisplayName("Все в кеше")
    @Order(2)
    @Test
    void findByIdInCache() {
        BookDefinition bd1 = bookRepository.findById(1L).orElse(null);
        BookDefinition bd2 = bookRepository.findById(2L).orElse(null);
        assertThat(bd1).isNotNull();
        assertThat(bd2).isNotNull();

        //All in cache
        AssertSqlQueriesCount.assertSelectCount(0);
        assertThat(cache.getHitCount()).isEqualTo(2);
    }

    @DisplayName("Мимо кеша, да и базы тоже")
    @Order(3)
    @Test
    void phantomFind() {
        BookDefinition bd1 = bookRepository.findById(Long.MAX_VALUE).orElse(null);
        assertThat(bd1).isNull();

        //All in cache
        AssertSqlQueriesCount.assertSelectCount(1);
        assertThat(cache.getMissCount()).isEqualTo(1);
    }

    @DisplayName("Добавляем новый элемент в базу и в кешь")
    @Order(4)
    @Test
    void addNewObject() {
        Category category = categoryRepository.findRoot();
        assertThat(category).isNotNull();
        AssertSqlQueriesCount.reset();

        BookDefinition bd = new BookDefinition(BOOK_NAME, BOOK_DESC, CREATOR,
                new PrintInfo(RELEASE_YEAR, COVER_TYPE, ISBN, PAGE_COUNT, BOOK_LANGUAGE, null), Collections.emptyList(),  category);
        bd = bookRepository.save(bd);
        bookRepository.flush();

        AssertSqlQueriesCount.assertInsertCount(1);
        BookDefinition bd2 = bookRepository.findById(bd.getId()).orElse(null);
        assertThat(bd2).isNotNull();

        AssertSqlQueriesCount.assertSelectCount(1);
    }

    @DisplayName("Удаляем элемент из базы и кеша")
    @Order(5)
    @Test
    void deleteObject() {
        bookRepository.deleteById(1L);
        bookRepository.flush();
        AssertSqlQueriesCount.assertDeleteCount(4);

        BookDefinition bd = bookRepository.findById(1L).orElse(null);
        assertThat(bd).isNull();
    }

    @DisplayName("Меняем элемент в базе и в кешу")
    @Order(6)
    @Test
    void mergeElement() {
        BookDefinition bd = bookRepository.findById(2L).orElse(null);
        assertThat(bd).isNotNull();
        AssertSqlQueriesCount.assertSelectCount(0);

        bd.setName("New Name");
        bookRepository.save(bd);
        bookRepository.flush();

        BookDefinition bd2 = bookRepository.findById(2L).orElse(null);
        assertThat(bd2).isNotNull();
        AssertSqlQueriesCount.assertSelectCount(0);
        assertThat(bd2.getName()).isEqualTo("New Name");
    }
}
