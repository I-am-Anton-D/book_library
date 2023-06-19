package ru.ntik.book.library.repository;

import io.github.yashchenkon.assertsqlcount.test.AssertSqlQueriesCount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.ntik.book.library.domain.BookDefinition;
import ru.ntik.book.library.domain.Rating;
import ru.ntik.book.library.domain.Review;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("h2")
@Transactional
class ReviewRatingTest {

    @Autowired
    BookRepository bookRepository;

    @BeforeEach
    void resetSqlCount() {
        AssertSqlQueriesCount.reset();
    }


    @DisplayName("Lazy fetch review")
    @Test
    void lazyReviewFetch() {
        BookDefinition bd = bookRepository.findById(1L).orElse(null);
        assertThat(bd).isNotNull();
        AssertSqlQueriesCount.assertSelectCount(1);

        List<Review> reviews = bd.getReviews();
        AssertSqlQueriesCount.assertSelectCount(1);

        assertThat(reviews).isNotEmpty();
        AssertSqlQueriesCount.assertSelectCount(2);
    }

    @DisplayName("Rating fetch eager")
    @Test
    void testRating() {
        BookDefinition bd = bookRepository.findById(1L).orElse(null);
        assertThat(bd).isNotNull();
        AssertSqlQueriesCount.assertSelectCount(1);

        assertThat(bd.getRating().getCommonRating()).isPositive();
        assertThat(bd.getRating().getVoteCount()).isPositive();
        AssertSqlQueriesCount.assertSelectCount(1);
    }

    @DisplayName("Set wrong rating")
    @Test
    void wrongRatingSet() {
        BookDefinition bd = bookRepository.findById(1L).orElse(null);
        assertThat(bd).isNotNull();
        Rating rating = bd.getRating();

        assertThrows(IllegalArgumentException.class, ()-> rating.setCommonRating(-2.0));
        assertThrows(IllegalArgumentException.class, ()-> rating.setCommonRating(6.0));
        assertThatCode(()-> rating.setCommonRating(0.0)).doesNotThrowAnyException();
        assertThatCode(()-> rating.setCommonRating(5.0)).doesNotThrowAnyException();

        assertThrows(IllegalArgumentException.class, ()-> rating.setVoteCount(-2));
        assertThatCode(()-> rating.setVoteCount(0)).doesNotThrowAnyException();

    }
}
