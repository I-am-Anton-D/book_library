package ru.ntik.book.library.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import ru.ntik.book.library.util.Checker;

import static ru.ntik.book.library.util.Constants.SMALL_STRING_LENGTH;

@Entity

@Getter
public class BookDefinition extends PersistentObject {
    //-------------------------
    //TABLE, COLUMN DEFINITIONS
    public static final String COLUMN_RELEASE_YEAR_NAME = "release_year";
    public static final int RELEASE_YEAR_MIN = 1900;
    public static final int RELEASE_YEAR_MAX = 2040;
    public static final String COLUMN_RELEASE_YEAR_DEFINITION =
            "SMALLINT CHECK(" + COLUMN_RELEASE_YEAR_NAME + " BETWEEN " + RELEASE_YEAR_MIN + " AND " + RELEASE_YEAR_MAX + ")";
    public static final String COLUMN_COVER_TYPE_NAME = "cover_type";
    public static final String COLUMN_COVER_TYPE_DEFINITION =
            "VARCHAR(" + SMALL_STRING_LENGTH + ") CHECK(length( " + COLUMN_COVER_TYPE_NAME + " ) > 0)";
    private static final String COLUMN_ISBN_NAME = "isbn";
    private static final int ISBN_MIN_LENGTH = 10;
    private static final String COLUMN_ISBN_DEFINITION = "VARCHAR(" + SMALL_STRING_LENGTH + ") " +
            "CHECK(length(" + COLUMN_ISBN_NAME + ") >= " + ISBN_MIN_LENGTH + ")";
    @Column(name = COLUMN_RELEASE_YEAR_NAME, columnDefinition = COLUMN_RELEASE_YEAR_DEFINITION)
    private int releaseYear;
    @Column(name = COLUMN_COVER_TYPE_NAME, columnDefinition = COLUMN_COVER_TYPE_DEFINITION)
    private String coverType;
    @Column(name = COLUMN_ISBN_NAME, columnDefinition = COLUMN_ISBN_DEFINITION)
    private String isbn;

    protected BookDefinition() {
    }

    protected BookDefinition(Long id, String name, String description,
                             Long creator, int releaseYear, String coverType, String isbn) {
        super(id, name, description, creator);

        setReleaseYear(releaseYear);
        setCoverType(coverType);
        setIsbn(isbn);
    }

    public void setReleaseYear(int releaseYear) {
        if (releaseYear < RELEASE_YEAR_MIN || releaseYear > RELEASE_YEAR_MAX)
            throw new IllegalArgumentException(String.format(
                    "Release year not in bounds (min = %d, max = %d)", RELEASE_YEAR_MIN, RELEASE_YEAR_MAX));
        this.releaseYear = releaseYear;
    }

    public void setCoverType(String coverType) {
        this.coverType = coverType == null ? null : Checker.checkStringLength(coverType, 1, SMALL_STRING_LENGTH);
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn == null ? null : Checker.checkStringLength(isbn, ISBN_MIN_LENGTH, SMALL_STRING_LENGTH);
    }

}
