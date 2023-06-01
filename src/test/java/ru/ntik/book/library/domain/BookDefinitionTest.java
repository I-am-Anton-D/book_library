package ru.ntik.book.library.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

@DisplayName("Тесты сущности BookDefinition")
@Execution(ExecutionMode.CONCURRENT)

class BookDefinitionTest {

    static final String BOOK_NAME = "BOOK_NAME";
    static final String BOOK_DESC = "BOOK_DESCRIPTION";
    static final Long CREATOR = 10L;
    static final int RELEASE_YEAR = 2020;
    static final String COVER_TYPE = "paperback";
    static final String ISBN = "978-5-4461-0512-0";

}