package ru.ntik.book.library.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.CreationTimestamp;
import ru.ntik.book.library.util.Checker;

import java.time.Instant;
import java.util.Objects;

import static ru.ntik.book.library.util.Constants.*;

@Entity

@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE,
        region = REVIEW_REGION_NAME)

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter

public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false, updatable = false)
    private Long creator;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private Instant created;

    @Column(name = COLUMN_TEXT_NAME, columnDefinition = COLUMN_TEXT_DEFINITION)
    private String text;

    @Column(name = COLUMN_RATING_NAME, columnDefinition = COLUMN_RATING_DEFINITION)
    private int rating;


    public Review(String text, int rating) {
        setText(text);
        setRating(rating);
    }

    private void setRating(int rating) {
        this.rating = Checker.checkIntegerRange(rating, 1, 5);
    }

    private void setText(String text) {
        Objects.requireNonNull(text);
        this.text = Checker.checkStringLength(text, MIN_STRING_LENGTH, LONG_STRING_LENGTH);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Review that)) return false;

        return id != null && that.getId() != null
                && Objects.equals(id, that.getId());
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    private static final String COLUMN_TEXT_NAME = "text";
    private static final String COLUMN_TEXT_DEFINITION =
            "VARCHAR(" + LONG_STRING_LENGTH + ") CHECK (length(" + COLUMN_TEXT_NAME + ") >= " + MIN_STRING_LENGTH + ") NOT NULL";
    private static final String COLUMN_RATING_NAME = "rating";
    private static final String COLUMN_RATING_DEFINITION =
            "SMALLINT CHECK(" + COLUMN_RATING_NAME + " BETWEEN 1 AND 5) default 0";
}
