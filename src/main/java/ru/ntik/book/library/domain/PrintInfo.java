package ru.ntik.book.library.domain;

import jakarta.persistence.*;
import lombok.Getter;
import ru.ntik.book.library.domain.enums.BookLanguage;
import ru.ntik.book.library.util.Checker;

import java.util.Objects;

import static ru.ntik.book.library.util.Constants.SMALL_STRING_LENGTH;

@Embeddable
@Getter

public class PrintInfo {

    @Column(name = COLUMN_RELEASE_YEAR_NAME, columnDefinition = COLUMN_RELEASE_YEAR_DEFINITION)
    private Integer releaseYear;

    @Column(name = COLUMN_COVER_TYPE_NAME, columnDefinition = COLUMN_COVER_TYPE_DEFINITION)
    private String coverType;

    @Column(name = COLUMN_ISBN_NAME, columnDefinition = COLUMN_ISBN_DEFINITION)
    private String isbn;

    @Column(name = COLUMN_PAGE_COUNT_NAME, columnDefinition = COLUMN_PAGE_COUNT_DEFINITION)
    private Integer pageCount;

    @Enumerated(EnumType.STRING)
    @Column(name = "language", length = 20)
    private BookLanguage language;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "publisher_id")
    public Publisher publisher;

    protected PrintInfo() {}

    public PrintInfo(Integer releaseYear, String coverType, String isbn, Integer pageCount,
                     BookLanguage language, Publisher publisher) {
        setReleaseYear(releaseYear);
        setCoverType(coverType);
        setIsbn(isbn);
        setPageCount(pageCount);
        setLanguage(language);
        setPublisher(publisher);
    }

    public void setReleaseYear(Integer releaseYear) {
        this.releaseYear = releaseYear == null ? null :
                Checker.checkIntegerRange(releaseYear, RELEASE_YEAR_MIN, RELEASE_YEAR_MAX);
    }

    public void setCoverType(String coverType) {
        this.coverType = coverType == null ? null :
                Checker.checkStringLength(coverType, 1, SMALL_STRING_LENGTH);
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn == null ? null :
                Checker.checkStringLength(isbn, ISBN_MIN_LENGTH, ISBN_MAX_LENGTH);
    }

    public void setPageCount(Integer pageCount) {
        this.pageCount = pageCount == null ? null :
                Checker.checkIntegerRange(pageCount, 1, BOOK_MAX_PAGE_COUNT);
    }

    public void setLanguage(BookLanguage language) {
        Objects.requireNonNull(language);
        this.language = language;
    }

    public void setPublisher(Publisher publisher) {
        this.publisher = publisher;
    }

    private static final String COLUMN_RELEASE_YEAR_NAME = "release_year";
    public static final int RELEASE_YEAR_MIN = 1900;
    public static final int RELEASE_YEAR_MAX = 2040;
    private static final String COLUMN_RELEASE_YEAR_DEFINITION =
            "SMALLINT CHECK(" + COLUMN_RELEASE_YEAR_NAME + " BETWEEN " + RELEASE_YEAR_MIN + " AND " + RELEASE_YEAR_MAX + ")";
    private static final String COLUMN_COVER_TYPE_NAME = "cover_type";
    private static final String COLUMN_COVER_TYPE_DEFINITION =
            "VARCHAR(" + SMALL_STRING_LENGTH + ") CHECK(length( " + COLUMN_COVER_TYPE_NAME + " ) > 0)";
    private static final String COLUMN_ISBN_NAME = "isbn";
    public static final int ISBN_MIN_LENGTH = 10;
    public static final int ISBN_MAX_LENGTH = 20;
    private static final String COLUMN_ISBN_DEFINITION = "VARCHAR(" + ISBN_MAX_LENGTH + ") " +
            "CHECK(length(" + COLUMN_ISBN_NAME + ") >= " + ISBN_MIN_LENGTH + ")";
    public static final int BOOK_MAX_PAGE_COUNT = 3000;
    private static final String COLUMN_PAGE_COUNT_NAME = "page_count";
    private static final String COLUMN_PAGE_COUNT_DEFINITION =
            "SMALLINT CHECK(" + COLUMN_PAGE_COUNT_NAME + " BETWEEN 0 AND " + BOOK_MAX_PAGE_COUNT + ")";
}
