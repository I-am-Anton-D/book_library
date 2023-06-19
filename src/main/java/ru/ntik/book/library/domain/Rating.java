package ru.ntik.book.library.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.ntik.book.library.util.Checker;

@Embeddable

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)

public class Rating {
    @Column(name = COLUMN_VOTE_COUNT_NAME, columnDefinition = COLUMN_VOTE_COUNT_DEFINITION)
    int voteCount;
    @Column(name = COLUMN_COMMON_RATING_NAME, columnDefinition = COLUMN_COMMON_RATING_DEFINITION)
    double commonRating;

    Rating(int voteCount, double commonRating) {
        this.voteCount = voteCount;
        this.commonRating = commonRating;
    }

    public void setVoteCount(int voteCount) {
        Checker.checkIntegerRange(voteCount, 0, Integer.MAX_VALUE);
        this.voteCount = voteCount;
    }

    public void setCommonRating(double commonRating) {
        Checker.checkDoubleRange(commonRating, 0, 5.0);
        this.commonRating = commonRating;
    }

    public void resetToZero() {
        setCommonRating(0);
        setVoteCount(0);
    }

    public static final String COLUMN_VOTE_COUNT_NAME = "vote_count";
    public static final String COLUMN_VOTE_COUNT_DEFINITION = "SMALLINT CHECK(" + COLUMN_VOTE_COUNT_NAME + " >= 0) NOT NULL ";

    public static final String COLUMN_COMMON_RATING_NAME = "common_rating";
    public static final String COLUMN_COMMON_RATING_DEFINITION = "NUMERIC(2, 1) CHECK(" + COLUMN_COMMON_RATING_NAME + " BETWEEN 0.0 AND 5.0) NOT NULL";
}