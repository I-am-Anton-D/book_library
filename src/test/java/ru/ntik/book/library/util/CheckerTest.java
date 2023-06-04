package ru.ntik.book.library.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Проверяем методы чекера")
@Execution(ExecutionMode.CONCURRENT)
class CheckerTest {

    @DisplayName("Максимум строго больше минимума")
    @Test
    void checkLengthTest() {
        int min = 5;
        int max = 3;

        assertThrows(IllegalArgumentException.class, () ->Checker.checkStringLength("String", min, max));

        assertThrows(IllegalArgumentException.class, () ->Checker.checkStringLength("String", min, min));

    }

    @DisplayName("Верная длина")
    @Test
    void correctLength() {
        String s = "String";
        int min = s.length();
        int max = min + 1;
        assertThat(Checker.checkStringLength(s, min, max)).isEqualTo(s);
        assertThat(Checker.checkStringLength(s, 0, max)).isEqualTo(s);
        assertThat(Checker.checkStringLength(s, 0, s.length())).isEqualTo(s);

        assertThrows(IllegalArgumentException.class, () -> Checker.checkStringLength(s, 20, 20+1));
    }

    @DisplayName("Пустая строка")
    @Test
    void emptyString() {
        assertThrows(IllegalArgumentException.class, ()->Checker.checkStringLength("",1, 2));
        assertThat(Checker.checkStringLength("", 0, 2)).isEmpty();
    }

    @DisplayName("null string")
    @Test
    void nullInParams() {
        assertThrows(NullPointerException.class, () -> Checker.checkStringLength(null, 10, 10));
    }

}