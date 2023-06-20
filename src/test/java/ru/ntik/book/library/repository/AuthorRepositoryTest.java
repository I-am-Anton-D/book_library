package ru.ntik.book.library.repository;

import io.github.yashchenkon.assertsqlcount.test.AssertSqlQueriesCount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.ntik.book.library.domain.Author;
import ru.ntik.book.library.domain.BookDefinition;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("h2")
@Transactional
class AuthorRepositoryTest {

    @Autowired
    AuthorRepository authorRepository;

    @BeforeEach
    void resetSqlCount() {
        AssertSqlQueriesCount.reset();
    }

    @DisplayName("Тест lazy fetch для Book Definition")
    @Test
    void lazyFetchForBookDefinition() {
        Author author = authorRepository.findById(6L).orElse(null);
        assertThat(author).isNotNull();
        AssertSqlQueriesCount.assertSelectCount(1);

        final Set<BookDefinition> bookDefinitions = author.getBookDefinitions();
        AssertSqlQueriesCount.assertSelectCount(1);

        assertThrows(UnsupportedOperationException.class, () -> bookDefinitions.add(null));

        for (BookDefinition bd : bookDefinitions) {
            assertThat(bd.getName()).isNotNull();
        }

        AssertSqlQueriesCount.assertSelectCount(2);
    }

    @DisplayName("Забираем книги в eager c помощью графа")
    @Test
    void eagerFetchBooks() {
        Author author = authorRepository.fetchById(6L).orElse(null);
        assertThat(author).isNotNull();
        AssertSqlQueriesCount.assertSelectCount(1);

        final Set<BookDefinition> bookDefinitions = author.getBookDefinitions();
        AssertSqlQueriesCount.assertSelectCount(1);

        for (BookDefinition bd : bookDefinitions) {
            assertThat(bd.getName()).isNotNull();
        }
        AssertSqlQueriesCount.assertSelectCount(1);
    }

    @DisplayName("Проверяем batch на insert")
    @Test
    void batchUpdateTest() {

        for (int i = 0; i < 5; i++) {
            Author author = new Author("Name " + i, null, 10L);
            authorRepository.save(author);
            AssertSqlQueriesCount.assertInsertCount(0);
        }
        authorRepository.flush();
        AssertSqlQueriesCount.assertInsertCount(1);
    }

    @DisplayName("Вставляем дубликат")
    @Test
    void uniqAuthorNameAndEqualsAndHashCode() {
        Author author = authorRepository.fetchById(6L).orElse(null);
        assertThat(author).isNotNull().isNotNull().isNotEqualTo("SOME");
        assertThat(author.equals(null)).isFalse(); //Coverage

        Author another =  authorRepository.findById(7L).orElse(null);
        assertThat(another).isNotNull().isNotEqualTo(author);
        assertThat(another.hashCode()).isNotEqualTo(author.hashCode());

        Author same = authorRepository.findById(6L).orElse(null);
        assertThat(same).isNotNull().isEqualTo(same).hasSameHashCodeAs(same.hashCode());

        Author newAuthor = new Author(author.getName(), null, 10L);
        authorRepository.save(newAuthor);

        assertThrows(DataIntegrityViolationException.class, ()-> authorRepository.flush());
    }
}

