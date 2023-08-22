package ru.ntik.book.library.repository;

import io.github.yashchenkon.assertsqlcount.test.AssertSqlQueriesCount;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.ntik.book.library.domain.BookDefinition;
import ru.ntik.book.library.domain.BookInstance;
import ru.ntik.book.library.domain.BookStatus;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("h2")
@Transactional

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AuditStatusTest {

    @PersistenceContext
    EntityManager em;

    @Autowired
    BookRepository bookRepository;

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    @DisplayName("One update")
    @Commit
    @Order(1)
    @Test
    /* TODO: Find out why Select queries count is different
        when running tests sequentially (via mvn test)
        rather than individually */
    void updateFirst() {
        BookDefinition bd = bookRepository.findById(1L).orElse(null);
        assertThat(bd).isNotNull();
        AssertSqlQueriesCount.assertSelectCount(2); // was equal to 1


        BookInstance bi = bd.getInstances().iterator().next();
        AssertSqlQueriesCount.assertSelectCount(3); // was equal to 2
        bi.moveToUser(12L);
        bookRepository.save(bd);
        bookRepository.flush();

        AssertSqlQueriesCount.assertUpdateCount(3); // was equal to 2
    }

    @DisplayName("Second update")
    @Commit
    @Order(2)
    @Test
    void updateSecond() {
        BookDefinition bd = bookRepository.findById(1L).orElse(null);
        assertThat(bd).isNotNull();

        BookInstance bi = bd.getInstances().iterator().next();
        bi.moveToOwner();
        bookRepository.save(bd);
        bookRepository.flush();
    }

    @DisplayName("Third update")
    @Commit
    @Order(3)
    @Test
    void updateThree() {
        BookDefinition bd = bookRepository.findById(1L).orElse(null);
        assertThat(bd).isNotNull();

        BookInstance bi = bd.getInstances().iterator().next();
        bi.moveToUser(15L);
        bookRepository.save(bd);
        bookRepository.flush();
    }


    @DisplayName("Read audit")
    @Commit
    @Order(4)
    @Test
    void readAudit() {
        BookDefinition bd = bookRepository.findById(1L).orElse(null);
        assertThat(bd).isNotNull();

        BookInstance bi = bd.getInstances().iterator().next();

        AuditReader auditReader = AuditReaderFactory.get(em);
        final List<Number> revNums = auditReader.getRevisions(BookStatus.class, bi.getStatus().getId());
        assertThat(revNums).isNotEmpty();

        final Map<Number, BookStatus> revisions = auditReader.findRevisions(BookStatus.class, new HashSet<>(revNums));
        assertThat(revisions).isNotEmpty();
    }

}
