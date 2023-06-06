package ru.ntik.book.library.util;

import java.util.Objects;

public class Checker {
    public static String checkStringLength(String s, int min, int max) {
        Objects.requireNonNull(s);
        checkLimits(min, max);

        if (s.length() < min || s.length() > max) {
            throw new IllegalArgumentException(
                    String.format("The length of parameter is incorrect. " +
                            "Min length = %d, max length = %d, current length = %d", min, max, s.length()));
        }
        return s;
    }

    public static Integer checkIntegerRange(Integer n, int min, int max) {
        Objects.requireNonNull(n);
        checkLimits(min, max);

        if (n < min || n > max) {
            throw new IllegalArgumentException(
                    String.format("Number out of range. Min = %d, max = %d , current = %d", min, max, n));

        }
        return n;
    }

    private static void checkLimits(int min, int max) {
        if (min >= max) {
            throw new IllegalArgumentException("min >= max");
        }

    }

    private Checker() {
    }
}
