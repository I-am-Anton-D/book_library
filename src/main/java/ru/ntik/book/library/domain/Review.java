package ru.ntik.book.library.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import ru.ntik.book.library.util.Checker;

import java.util.Objects;

import static ru.ntik.book.library.util.Constants.*;

@Entity

@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE,
        region = REVIEW_REGION_NAME)

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter

public class Review extends StoredObject {

    @Column(name = COLUMN_TEXT_NAME, columnDefinition = COLUMN_TEXT_DEFINITION)
    private String text;

    @Column(name = COLUMN_RATING_NAME, columnDefinition = COLUMN_RATING_DEFINITION)
    private int rating;

    @ManyToOne(fetch = FetchType.LAZY)
    private BookDefinition bookDefinition;

    public Review(String text, int rating, long creator, BookDefinition bookDefinition) {
        super(creator);
        setText(text);
        setRating(rating);
        setBookDefinition(bookDefinition);
    }

    public void setRating(int rating) {
        this.rating = Checker.checkIntegerRange(rating, 1, 5);
    }

    public void setText(String text) {
        Objects.requireNonNull(text);
        this.text = Checker.checkStringLength(text, MIN_STRING_LENGTH, LONG_STRING_LENGTH);
    }

    public void setBookDefinition(BookDefinition bookDefinition) {
        Objects.requireNonNull(bookDefinition);
        this.bookDefinition = bookDefinition;
    }

    private static final String COLUMN_TEXT_NAME = "text";
    private static final String COLUMN_TEXT_DEFINITION =
            "VARCHAR(" + LONG_STRING_LENGTH + ") CHECK (length(" + COLUMN_TEXT_NAME + ") >= " + MIN_STRING_LENGTH + ") NOT NULL";
    private static final String COLUMN_RATING_NAME = "rating";
    private static final String COLUMN_RATING_DEFINITION =
            "SMALLINT CHECK(" + COLUMN_RATING_NAME + " BETWEEN 1 AND 5)";
}
