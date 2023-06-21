package ru.ntik.book.library.util;

public class Constants {
    public static final int DEFAULT_VARCHAR_LENGTH = 255;
    public static final int SMALL_STRING_LENGTH = 64;
    public static final int MIDDLE_STRING_LENGTH = 128;
    public static final int MIN_STRING_LENGTH = 1;
    public static final int PO_MAX_NAME_LENGTH = MIDDLE_STRING_LENGTH;
    public static final int PO_MIN_DESC_LENGTH = 5;
    public static final int LONG_STRING_LENGTH = 4095;
    public static final int PO_BATCH_SIZE = 20;
    public static final String BOOK_DEFINITION_REGION_NAME = "ru.ntik.book.library.domain.BookDefinition";
    public static final String PUBLISHER_REGION_NAME = "ru.ntik.book.library.domain.Publisher";
    public static final String AUTHOR_REGION_NAME = "ru.ntik.book.library.domain.Author";
    public static final String REVIEW_REGION_NAME = "ru.ntik.book.library.domain.Review";
    public static final String CATEGORY_REGION_NAME = "ru.ntik.book.library.domain.Category";
    public static final String BOOK_INSTANCE_REGION_NAME = "ru.ntik.book.library.domain.BookInstance";
    public static final String BOOK_INSTANCE_STATUS_REGION_NAME = "ru.ntik.book.library.domain.BookInstanceStatus";
    public static final String ID_GENERATOR = "ID_GENERATOR";

    private Constants() {
    }
}
