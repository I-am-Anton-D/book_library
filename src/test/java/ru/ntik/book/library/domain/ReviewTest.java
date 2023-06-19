package ru.ntik.book.library.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.ntik.book.library.testutils.TestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ReviewTest {

    @DisplayName("Создаем экземпляр")
    @Test
    void createInstance() {
        BookDefinition bd = TestUtils.createBookDefinition();
        Review review = new Review("Cool Book", 5, 10L, bd);
        assertThat(review).isNotNull();
        assertThat(review.getText()).isEqualTo("Cool Book");
        assertThat(review.getRating()).isEqualTo(5);
        assertThat(review.getBookDefinition().getId()).isEqualTo(TestUtils.createBookDefinition().getId());

        assertThatCode(()-> new Review("Cool Book", 0, 10L, bd)).doesNotThrowAnyException();
        assertThatCode(()-> new Review("C", 0, 10L, bd)).doesNotThrowAnyException();
    }

    @DisplayName("Ломаный объект не создать")
    @Test
    void createWrongObject() {
        BookDefinition bd = TestUtils.createBookDefinition();
        assertThrows(IllegalArgumentException.class, ()-> new Review("", 5, 10L, bd));
        assertThrows(IllegalArgumentException.class, ()-> new Review("", -1, 10L, bd));
        assertThrows(IllegalArgumentException.class, ()-> new Review("Some Text", 6, 10L, bd));
        assertThrows(NullPointerException.class, ()-> new Review(null, -1, 10L, bd));
        assertThrows(NullPointerException.class, ()-> new Review("Some text exist", 5, 10L, null));
    }
}