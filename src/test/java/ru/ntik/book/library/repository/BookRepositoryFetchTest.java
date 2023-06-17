package ru.ntik.book.library.repository;

import io.github.yashchenkon.assertsqlcount.test.AssertSqlQueriesCount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.ntik.book.library.domain.Author;
import ru.ntik.book.library.domain.BookDefinition;
import ru.ntik.book.library.domain.Publisher;

import java.util.Collection;
import java.util.Iterator;

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

    @DisplayName("Все Lazy")
    @Test
    void lazyFetch() {
        BookDefinition bd = bookRepository.findById(1L).orElse(null);
        assertThat(bd).isNotNull();
        AssertSqlQueriesCount.assertSelectCount(1);

        //No Lazy load here
        Publisher publisher = bd.getPublisher();
        assertThat(publisher).isNotNull();
        Collection<Author> authors = bd.getAuthors();

        AssertSqlQueriesCount.assertSelectCount(1);

        //Lazy load here
        String name = publisher .getName();
        assertThat(name).isNotNull();
        assertThat(authors).isNotEmpty();
        AssertSqlQueriesCount.assertSelectCount(3);
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
        bookRepository.flush();
        //Insert publisher
        AssertSqlQueriesCount.assertInsertCount(1);
        AssertSqlQueriesCount.assertUpdateCount(1);


        Author author = new Author("New Name", null, 10L);
        bd.getAuthors().add(author);
        bd = bookRepository.save(bd);
        bookRepository.flush();

        //Insert Author
        AssertSqlQueriesCount.assertInsertCount(3);
        AssertSqlQueriesCount.assertUpdateCount(1);
        AssertSqlQueriesCount.assertDeleteCount(0);

        bookRepository.flush();

        //Insert to Join table and udpate book
        AssertSqlQueriesCount.assertInsertCount(3);
        AssertSqlQueriesCount.assertUpdateCount(1);

        assertThat(bd.getPublisher().getId()).isNotNull();
        Publisher saved = publisherRepository.findById(bd.getPublisher().getId()).orElse(null);
        assertThat(saved).isNotNull();
    }

    @DisplayName("Попытка добавить автора повторно")
    @Test
    void duplicateAuthor() {
        BookDefinition bd = bookRepository.findById(1L).orElse(null);
        assertThat(bd).isNotNull();
        assertThat(bd.getAuthors()).isNotEmpty();
        AssertSqlQueriesCount.assertSelectCount(2);

        Iterator<Author> iterator = bd.getAuthors().iterator();
        Author exist = iterator.next();

        Author duplicate = new Author(exist.getName(), null, 10L);
        boolean added = bd.getAuthors().add(duplicate);

        assertThat(added).isFalse();
        bookRepository.save(bd);

        //Nothing to save
        AssertSqlQueriesCount.assertUpdateCount(0);
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

    @DisplayName("Достаем линки")
    @Test
    void fetchLinks() {
        BookDefinition bd = bookRepository.findById(1L).orElse(null);
        assertThat(bd).isNotNull();
        AssertSqlQueriesCount.assertSelectCount(1);

        Collection<BookDefinition> links = bd.getLinks();

        assertThat(links).isNotEmpty();
        AssertSqlQueriesCount.assertSelectCount(2);
    }

    @DisplayName("Lazy load")
    @Test
    void lazyLoad() {
        BookDefinition bd = bookRepository.findById(1L).orElse(null);
        assertThat(bd).isNotNull();
        AssertSqlQueriesCount.assertSelectCount(1);

        Publisher pub = bd.getPublisher();
        assertThat(pub.getName()).isNotNull();
        AssertSqlQueriesCount.assertSelectCount(2);

        assertThat(bd.getAuthors()).isNotEmpty();
        AssertSqlQueriesCount.assertSelectCount(3);

        assertThat(bd.getLinks()).isNotEmpty();
        AssertSqlQueriesCount.assertSelectCount(4);
    }

    @DisplayName("Eager Load")
    @Test
    void eagerLoadByGraph() {
        //EAGER load
        BookDefinition eager = bookRepository.fetchById(1L).orElse(null);
        AssertSqlQueriesCount.assertSelectCount(1);
        assertThat(eager).isNotNull();

        assertThat(eager.getPublisher().getName()).isNotNull();
        assertThat(eager.getAuthors()).isNotEmpty();
        assertThat(eager.getLinks()).isNotEmpty();
        AssertSqlQueriesCount.assertSelectCount(1);
    }

    @DisplayName("Попытка добавить дубликат линка на книгу")
    @Test
    void duplicateLink() {
        BookDefinition bd = bookRepository.fetchById(1L).orElse(null);;
        AssertSqlQueriesCount.assertSelectCount(1);
        assertThat(bd).isNotNull();
        assertThat(bd.getLinks()).isNotEmpty();

        Iterator<BookDefinition> iterator = bd.getLinks().iterator();
        BookDefinition linked = iterator.next();

        boolean added = bd.getLinks().add(linked);
        assertThat(added).isFalse();
        bookRepository.flush();

        //Get From DB
        BookDefinition fromDb = bookRepository.findById(linked.getId()).orElse(null);
        assertThat(fromDb).isNotNull();
        //AssertSqlQueriesCount.assertSelectCount(2);

        added = bd.getLinks().add(fromDb);
        assertThat(added).isFalse();
    }

}