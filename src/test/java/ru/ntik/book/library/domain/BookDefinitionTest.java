package ru.ntik.book.library.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.util.ReflectionUtils;
import ru.ntik.book.library.domain.enums.BookLanguage;
import ru.ntik.book.library.testutils.TestUtils;
import ru.ntik.book.library.util.Constants;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.ntik.book.library.testutils.TestUtils.*;

@DisplayName("Тесты сущности BookDefinition")
@Execution(ExecutionMode.CONCURRENT)
class BookDefinitionTest {

    @DisplayName("Создание экземпляра")
    @Test
    void createInstance() {
        BookDefinition clear = new BookDefinition();
        assertThat(clear).isNotNull();

        BookDefinition bd = TestUtils.createBookDefinition();
        assertThat(bd).isNotNull();

        BookDefinition bd2 =
                new BookDefinition(BOOK_NAME, BOOK_DESC, CREATOR, null, null, null, null, null);
        assertThat(bd2).isNotNull();
    }

    @DisplayName("Год релиза должен быть в диапозоне")
    @Test
    void checkReleaseYear() {
        BookDefinition bd = TestUtils.createBookDefinition();

        bd.setReleaseYear(2020);
        assertThat(bd.getReleaseYear()).isEqualTo(2020);

        bd.setReleaseYear(null);
        assertThat(bd.getReleaseYear()).isNull();

        int min = BookDefinition.RELEASE_YEAR_MIN;
        int max = BookDefinition.RELEASE_YEAR_MAX;

        bd.setReleaseYear(min);
        assertThat(bd.getReleaseYear()).isEqualTo(min);

        bd.setReleaseYear(max);
        assertThat(bd.getReleaseYear()).isEqualTo(max);

        assertThrows(IllegalArgumentException.class, () -> bd.setReleaseYear(1899));
        assertThrows(IllegalArgumentException.class, () -> bd.setReleaseYear(2041));
    }

    @DisplayName("Тип обложки должен быть в пределах")
    @Test
    void checkCoverType() {
        BookDefinition bd = TestUtils.createBookDefinition();

        bd.setCoverType(null);
        assertThat(bd.getCoverType()).isNull();

        assertThrows(IllegalArgumentException.class, () -> bd.setCoverType(""));

        bd.setCoverType("A");
        assertThat(bd.getCoverType()).isEqualTo("A");


        String s63 = "YqVvHpGHiwlpbstMXg2bZbgLw0L0rj0GqMCP4qOmico3LIY6RIGaACJVOKqUh7U";
        String s64 = "YqVvHpGHiwlpbstMXg2bZbgLw0L0rj0GqMCP4qOmico3LIY6RIGaACJVOKqUh7U0";
        String s65 = "YqVvHpGHiwlpbstMXg2bZbgLw0L0rj0GqMCP4qOmico3LIY6RIGaACJVOKqUh7U01";

        int max = Constants.SMALL_STRING_LENGTH;
        assertThat(s64).hasSize(max);

        assertThatCode(() -> {
            bd.setCoverType(s63);
            bd.setCoverType(s64);
        }).doesNotThrowAnyException();

        assertThrows(IllegalArgumentException.class, () -> bd.setCoverType(s65));
    }

    @DisplayName("ISBN должен быть в прделах")
    @Test
    void checkIsbn() {
        BookDefinition bd = TestUtils.createBookDefinition();
        bd.setIsbn(null);
        assertThat(bd.getIsbn()).isNull();

        int min = BookDefinition.ISBN_MIN_LENGTH;
        int max = BookDefinition.ISBN_MAX_LENGTH;

        String s10 = "dBbxemDFfx";
        String s20 = "r1SFz1SMIicTg6aYwNXI";

        assertThat(s10).hasSize(min);
        assertThat(s20).hasSize(max);

        assertThatCode(() -> {
            bd.setIsbn(s10);
            bd.setIsbn(s20);
        }).doesNotThrowAnyException();

        assertThrows(IllegalArgumentException.class, () -> bd.setIsbn(""));
        assertThrows(IllegalArgumentException.class, () -> bd.setIsbn("AD"));
        assertThrows(IllegalArgumentException.class, () -> bd.setIsbn(ISBN + ISBN));
    }

    @DisplayName("Адекватное число страниц")
    @Test
    void checkPageCount() {
        BookDefinition bd = TestUtils.createBookDefinition();
        assertThat(bd.getPageCount()).isEqualTo(PAGE_COUNT);

        assertThrows(IllegalArgumentException.class, () -> bd.setPageCount(0));
        assertThrows(IllegalArgumentException.class, () -> bd.setPageCount(-10));
        int max = BookDefinition.BOOK_MAX_PAGE_COUNT;
        assertThrows(IllegalArgumentException.class, ()-> bd.setPageCount(max + 1));

        assertThatCode(() -> {
            bd.setPageCount(1);
            bd.setPageCount(100);
            bd.setPageCount(max);

        }).doesNotThrowAnyException();
    }

    @Test
    void languageTest() {
        BookDefinition bd = TestUtils.createBookDefinition();
        assertThat(bd.getLanguage()).isEqualTo(BOOK_LANGUAGE);

        bd.setLanguage(BookLanguage.ENGLISH);
        assertThat(bd.getLanguage()).isEqualTo(BookLanguage.ENGLISH);
    }


    @Test
    void toStringTest() {
        BookDefinition bd = TestUtils.createBookDefinition();
        assertThat(bd.toString()).contains("BookDefinition");
    }

    @Test
    void equalsAndHashCode() {
        //Check simple
        BookDefinition bd = TestUtils.createBookDefinition();
        assertThat(bd.hashCode()).isZero();
        assertThat(bd.equals("STRING")).isFalse();
        assertThat(bd.equals(bd)).isTrue();

        //Only with id can be equals
        BookDefinition bd2 = TestUtils.createBookDefinition();
        assertThat(bd2.hashCode()).isZero();
        assertThat(bd.equals(bd2)).isFalse();

        final Field id = ReflectionUtils.findField(BookDefinition.class, "id", Long.class);
        assert id != null;
        id.setAccessible(true);
        final Long value = 10L;

        ReflectionUtils.setField(id, bd, value);
        assertThat(bd.getId()).isEqualTo(value);

        //Still not equals
        assertThat(bd.hashCode()).isNotZero();
        assertThat(bd.equals(bd2)).isFalse();

        ReflectionUtils.setField(id, bd2, value);
        assertThat(bd.getId()).isEqualTo(value);

        //Now its equal
        assertThat(bd.equals(bd2)).isTrue();

        //And hashcodes are same
        assertThat(bd).hasSameHashCodeAs(bd2);

        //Check hash collection;

        Set<BookDefinition> set = new HashSet<>();
        set.add(bd);
        set.add(bd2);

        assertThat(set).hasSize(1);
        bd.setName("New Name");
        bd2.setName("New name 2");
        assertThat(set).contains(bd, bd2);

        ReflectionUtils.setField(id, bd2, 20L);

        set.add(bd2);
        assertThat(set).hasSize(2);

        //Not equals with dif ids
        assertThat(bd2.equals(bd)).isFalse();
    }
}