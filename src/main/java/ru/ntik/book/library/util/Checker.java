package ru.ntik.book.library.util;

import java.util.Objects;

public class Checker {
    public static String checkStringLength(String s, int min, int max) {
        Objects.requireNonNull(s);

        if (min >= max) {
            throw new IllegalArgumentException("min >= max");
        }

        if (s.length() < min || s.length() > max) {
            throw new IllegalArgumentException(
                    "The length of parameter is incorrect. " +
                            "Min length = " + min + ", " +
                            "max length = " + max + ", " +
                            "current length = " + s.length());
        }
        return s;
    }

    private Checker() {
    }
}
