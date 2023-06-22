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
import ru.ntik.book.library.domain.Review;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
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

        Set<Review> reviews = bd.getReviews();

        assertThrows(UnsupportedOperationException.class, () -> reviews.add(null));
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

    @DisplayName("Update review")
    @Test
    void updateReview() {
        BookDefinition bd = bookRepository.findById(1L).orElse(null);
        assertThat(bd).isNotNull();
        double beforeRating = bd.getRating().getCommonRating();

        Set<Review> reviews = bd.getReviews();
        assertThat(reviews).isNotEmpty();

        Review review = reviews.stream().toList().get(0);

        review.setText("New Text");
        review.setRating(5);


        bookRepository.save(bd);
        bookRepository.flush();
        AssertSqlQueriesCount.assertUpdateCount(2);

        BookDefinition fromDb = bookRepository.findById(1L).orElse(null);
        assertThat(fromDb).isNotNull();
        assertThat(fromDb.getRating().getVoteCount()).isEqualTo(3);
        assertThat(fromDb.getRating().getCommonRating()).isNotEqualTo(beforeRating);
    }


    @DisplayName("Add review")
    @Test
    void addNewReview() {
        BookDefinition bd = bookRepository.findById(1L).orElse(null);
        assertThat(bd).isNotNull();
        double beforeRating = bd.getRating().getCommonRating();
        int beforeCount = bd.getRating().getVoteCount();

        Review withoutRating = new Review("New Review", 0,10L, bd);
        bd.addReview(withoutRating);
        bookRepository.save(bd);
        bookRepository.flush();
        AssertSqlQueriesCount.assertInsertCount(1);
        AssertSqlQueriesCount.assertUpdateCount(0);
        AssertSqlQueriesCount.reset();

        BookDefinition fromDb = bookRepository.findById(1L).orElse(null);
        assertThat(fromDb).isNotNull();

        assertThat(fromDb.getRating().getCommonRating()).isEqualTo(beforeRating);
        assertThat(fromDb.getRating().getVoteCount()).isEqualTo(beforeCount);


        Review withRating = new Review("New Review", 5, 10L, fromDb);
        fromDb.addReview(withRating);
        fromDb = bookRepository.save(bd);
        bookRepository.flush();
        AssertSqlQueriesCount.assertInsertCount(1);
        AssertSqlQueriesCount.assertUpdateCount(1);

        assertThat(fromDb.getRating().getCommonRating()).isNotEqualTo(beforeRating).isGreaterThan(beforeRating);
        assertThat(fromDb.getRating().getVoteCount()).isNotEqualTo(beforeCount).isEqualTo(beforeCount + 1);
    }

    @DisplayName("Remove review")
    @Test
    void removeReview() {
        BookDefinition bd = bookRepository.findById(1L).orElse(null);
        assertThat(bd).isNotNull();
        double beforeRating = bd.getRating().getCommonRating();
        int beforeCount = bd.getRating().getVoteCount();

        Review withoutRating = new Review("New Review Without Rating", 0,10L, bd);
        bd.addReview(withoutRating);
        bookRepository.save(bd);
        bookRepository.flush();


        bd = bookRepository.findById(1L).orElse(null);
        assertThat(bd).isNotNull();
        assertThat(bd.getReviews()).isNotEmpty();

        //Oder test to
        Review savedBefore = bd.getReviews().stream().filter(r->r.getText().equals("New Review Without Rating")).findFirst().orElse(null);
        assertThat(savedBefore).isNotNull();

        bd.removeReview(savedBefore);
        bd = bookRepository.save(bd);
        bookRepository.flush();
        AssertSqlQueriesCount.assertDeleteCount(1);
        AssertSqlQueriesCount.assertUpdateCount(0);

        assertThat(bd.getRating().getCommonRating()).isEqualTo(beforeRating);
        assertThat(bd.getRating().getVoteCount()).isEqualTo(beforeCount);

        Review existReview = bd.getReviews().stream().toList().get(0);
        bd.removeReview(existReview);
        bookRepository.save(bd);
        bookRepository.flush();
        AssertSqlQueriesCount.assertDeleteCount(2);
        AssertSqlQueriesCount.assertUpdateCount(1);

        BookDefinition fromDb =  bookRepository.findById(1L).orElse(null);
        assertThat(fromDb).isNotNull();
        assertThat(fromDb.getRating().getCommonRating()).isNotEqualTo(beforeRating);
        assertThat(fromDb.getRating().getVoteCount()).isEqualTo(beforeCount - 1);
    }

    @DisplayName("Remove all reviews")
    @Test
    void removeAll() {
        BookDefinition bd = bookRepository.findById(1L).orElse(null);
        assertThat(bd).isNotNull();

        while (!bd.getReviews().isEmpty()) {
            Review review = bd.getReviews().stream().toList().get(0);
            bd.removeReview(review);
        }

        assertThat(bd.getReviews()).isEmpty();
        assertThat(bd.getRating().getVoteCount()).isZero();
        assertThat(bd.getRating().getCommonRating()).isEqualTo(0.0);
    }

    @DisplayName("Remove not exist")
    @Test
    void removeNotExist() {
        BookDefinition bd = bookRepository.findById(1L).orElse(null);
        assertThat(bd).isNotNull();
        boolean removed = bd.removeReview(new Review("ASD", 5, 10L, bd));
        assertThat(removed).isFalse();
    }
}
