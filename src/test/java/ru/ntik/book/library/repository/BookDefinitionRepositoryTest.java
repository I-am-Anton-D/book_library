package ru.ntik.book.library.repository;

import io.github.yashchenkon.assertsqlcount.test.AssertSqlQueriesCount;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import ru.ntik.book.library.domain.BookDefinition;
import ru.ntik.book.library.testutils.TestUtils;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("h2")

class BookDefinitionRepositoryTest {

    @Autowired BookDefinitionRepository bookDefinitionRepository;


    @DisplayName("Должен сохранить объект")
    @Test
    void persistObject() {
        BookDefinition bd = TestUtils.createBookDefinition();
        bd = bookDefinitionRepository.save(bd);

        AssertSqlQueriesCount.assertInsertCount(1);
        assertThat(bd.getId()).isNotNull();
        assertThat(bd.getCreated()).isNotNull().isBefore(Instant.now());

    }
}