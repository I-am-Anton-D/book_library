package ru.ntik.book.library.util;

import ru.ntik.book.library.domain.enums.BookLanguage;

public class BookLanguageTranslated {
    private BookLanguageTranslated() {}
    public static String getTranslatedName(BookLanguage bookLanguage) {

        switch (bookLanguage) {
            case RUSSIAN -> {return "Русский";}
            case ENGLISH -> {return "Английский";}
        }
        return "Не указано";
    }
}
