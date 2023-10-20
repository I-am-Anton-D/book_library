package ru.ntik.book.library.view.admin;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.server.StreamResource;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.math.NumberUtils;
import ru.ntik.book.library.domain.BookDefinition;
import ru.ntik.book.library.domain.Category;
import ru.ntik.book.library.domain.PrintInfo;
import ru.ntik.book.library.domain.enums.BookLanguage;
import ru.ntik.book.library.service.BookDefinitionService;
import ru.ntik.book.library.service.CategoryService;
import ru.ntik.book.library.util.BookLanguageTranslated;
import ru.ntik.book.library.util.ObjectActionListener;
import ru.ntik.book.library.view.components.CategoryPicker;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class BookDefinitionEditForm extends VerticalLayout {

    private Binder<BookDefinition> binder = new Binder<>();
    private BookDefinition bookDefinition = null;
    private CategoryService categoryService;
    private BookDefinitionService bookDefinitionService;

    // UI
    private final Image image = new Image(new StreamResource("book-cover.png",
            ()->getClass().getResourceAsStream("/book-cover.png")), "book-cover");
    private final TextField title = new TextField("Название");
    private final TextArea description = new TextArea("Описание");
    private final IntegerField releaseYearPicker = new IntegerField("Год выпуска");
    private final IntegerField pagesCountInput = new IntegerField("Число страниц");
    private final Select<BookLanguage> languageSelect = new Select<>();
    private final Span authorsList = new Span();
    private final TextField coverTypeInput = new TextField("Переплет");
    private final TextField isbnInput = new TextField("ISBN");
    private final Button selectPublisherButton = new Button("Издатель: Не задано");
    private final Button selectCategoryButton = new Button(getCategoryButtonText("Не задано"), e->openCategoryPickDialog());

    private String getCategoryButtonText(String text) {
        return "Категория: " + text;
    }

    // TODO: logic to specify creator
    // TODO: Authors picker

    private Button removeButton = new Button("Удалить", e->this.delete(null));
    private Button submitButton = new Button("Сохранить", e->this.save(null));
    private Button cancelButton = new Button("Отмена", e->this.close(null));
    private final List<ObjectActionListener<BookDefinition>> onSaveListeners = new ArrayList<>();
    private final List<ObjectActionListener<BookDefinition>> onDeleteListeners = new ArrayList<>();
    private final List<ObjectActionListener<BookDefinition>> onCloseListeners = new ArrayList<>();

    /**
     * Constructor used for creating new BookDefinitions
     */
    public BookDefinitionEditForm(BookDefinitionService bookDefinitionService, CategoryService categoryService, @NotNull Category category) {
        this(bookDefinitionService, categoryService, null, category);
    }
    public BookDefinitionEditForm(BookDefinitionService bookDefinitionService, CategoryService categoryService, @NotNull BookDefinition bookDefinition) {
        this(bookDefinitionService, categoryService, bookDefinition, null);
    }

    /**
     * Constructor used for editing existing BookDefinitions
     */
    private BookDefinitionEditForm(BookDefinitionService bookDefinitionService, CategoryService categoryService, BookDefinition bookDefinition, Category category) {
        this.bookDefinitionService = bookDefinitionService;
        this.categoryService = categoryService;
        if(bookDefinition == null) {
            bookDefinition = generateBookDefinition(category);
        }
        this.bookDefinition = bookDefinition;

        defineUI();
        bindBean(bookDefinition);
    }

    private void bindBean(BookDefinition bookDefinition) {
        binder.forField(title).bind(BookDefinition::getName, BookDefinition::setName);
        binder.forField(description).bind(BookDefinition::getDescription, BookDefinition::setDescription);

        binder.forField(releaseYearPicker).
                withValidator(year->year > 1445, "Слишком ранняя дата для печатной книги").
                withValidator(year->year <= Year.now().getValue(), "Невозможно добавить книгу из будущего").
                bind(book->book.getPrintInfo().getReleaseYear(),
                (book, value)->book.getPrintInfo().setReleaseYear(value));

        binder.forField(pagesCountInput).
                withValidator(Objects::nonNull, "Введите число страниц").
                withValidator(pages->pages > 0, "Невозможно добавить книгу без страниц").
                withValidator(pages->pages < Integer.MAX_VALUE, "Слишком большое число страниц").
                bind(book->book.getPrintInfo().getPageCount(),
                (book, value)->book.getPrintInfo().setPageCount(value));

        binder.forField(languageSelect).
                withValidator(Objects::nonNull, "Выберите язык").
                bind(book->book.getPrintInfo().getLanguage(),
                (book, value)->book.getPrintInfo().setLanguage(value));

        binder.forField(coverTypeInput).
                withValidator(cover->!(cover.isEmpty() || cover.isBlank()), "Укажите тип переплета").
                bind(book->book.getPrintInfo().getCoverType(),
                (book, value)->book.getPrintInfo().setCoverType(value));

        binder.forField(isbnInput).
                withValidator(isbn->(NumberUtils.isParsable(isbn)
                        && isbn.compareTo("1000000000") >= 0 && isbn.compareTo("9999999999") <= 0), // 1 000 000 000 <= isbn <= 9 999 999 999
                        "Некорректный ISBN").
                bind(book->book.getPrintInfo().getIsbn(),
                (book, value)->book.getPrintInfo().setIsbn(value));

        binder.readBean(bookDefinition);
    }

    /**
     * Defines UI components and binds them
     */
    private void defineUI() {
        image.setHeight("320px");
        image.setWidth("320px");

        title.setId("book-form-name");
        title.setWidthFull();
        title.setPlaceholder("Название");
        description.setId("book-form-desc");
        description.setMinHeight("4em");
        description.setMaxHeight("10em");
        description.setWidthFull();
        description.setPlaceholder("Описание");

        releaseYearPicker.setId("book-form-year");
        releaseYearPicker.setPlaceholder("2000");
        pagesCountInput.setId("book-form-pages");
        pagesCountInput.setPlaceholder("128");

        languageSelect.setId("book-form-lang");
        languageSelect.setLabel("Язык");
        languageSelect.setItems(BookLanguage.values());
        languageSelect.setItemLabelGenerator(BookLanguageTranslated::getTranslatedName);

        coverTypeInput.setId("book-form-cover");
        isbnInput.setId("book-form-isbn");

        selectCategoryButton.setId("book-category-button");
        if (bookDefinition.getCategory() != null) {
            selectCategoryButton.setText(getCategoryButtonText(bookDefinition.getCategory().getName()));
        }

        submitButton.setId("dialog-submit-button");
        cancelButton.setId("dialog-cancel-button");
        removeButton.setId("dialog-remove-button");

        submitButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        removeButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        add(
                image,
                title,
                description,
                new HorizontalLayout(releaseYearPicker, pagesCountInput, languageSelect, coverTypeInput),
                new HorizontalLayout(isbnInput, selectCategoryButton, authorsList, selectPublisherButton),
                new HorizontalLayout(submitButton, cancelButton, removeButton)
        );
    }

    private static BookDefinition generateBookDefinition(Category category) {
        return new BookDefinition("Название книги", "Описание", 0L,
                new PrintInfo(2000, "Твердый переплет", "9783161484100",
                        10, BookLanguage.RUSSIAN, null), List.of(), category
        );
    }

    private void save(BookDefinition bookDefinition) {
        if (bookDefinition == null) {
            bookDefinition = this.bookDefinition;
        }

        try {
            binder.writeBean(bookDefinition);
            bookDefinitionService.save(bookDefinition);
            Notification.show("Сохранено").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            for (ObjectActionListener<BookDefinition> listener : onSaveListeners) {
                listener.onPerformed(bookDefinition);
            }
        } catch (ValidationException e) {
            Notification.show("Введены некорректные данные").addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void delete(BookDefinition bookDefinition) {
        if (bookDefinition == null) {
            bookDefinition = this.bookDefinition;
        }

        if (!bookDefinitionService.isEmpty(bookDefinition)) {
            Notification.show("Невозможно удалить. Сперва удалите экземпляры.").addThemeVariants(NotificationVariant.LUMO_ERROR);
        } else {
            bookDefinitionService.remove(bookDefinition);
            Notification.show("Удалено").addThemeVariants(NotificationVariant.LUMO_PRIMARY);
            for (ObjectActionListener<BookDefinition> listener : onDeleteListeners) {
                listener.onPerformed(bookDefinition);
            }
        }
    }

    private void close(BookDefinition bookDefinition) {
        if (bookDefinition == null) {
            bookDefinition = this.bookDefinition;
        }

        for (ObjectActionListener<BookDefinition> listener : onCloseListeners) {
            listener.onPerformed(bookDefinition);
        }
    }
    public void addOnSaveListener(ObjectActionListener<BookDefinition> listener) {
        onSaveListeners.add(listener);
    }
    public void addOnDeleteListener(ObjectActionListener<BookDefinition> listener) {
        onDeleteListeners.add(listener);
    }
    public void addOnCloseListener(ObjectActionListener<BookDefinition> listener) {
        onCloseListeners.add(listener);
    }

    void openCategoryPickDialog() {
        Dialog categoryPickerDialog = new Dialog();
        categoryPickerDialog.setHeaderTitle("Выбрать категорию");
        CategoryPicker categoryPicker = new CategoryPicker(categoryService);
        Button categoryPickerCancelButton = new Button("Отмена", e->categoryPickerDialog.close());

        categoryPickerDialog.setMinWidth("30em");
        categoryPicker.addSelectionListener(category->Optional.of(category).ifPresent(cat->
                    {
                        bookDefinition.setCategory(cat);
                        selectCategoryButton.setText(getCategoryButtonText(cat.getName()));
                        categoryPickerDialog.close();
                    }));

        categoryPickerDialog.add(categoryPicker,categoryPickerCancelButton);
        add(categoryPickerDialog);
        categoryPickerDialog.open();
    }
}
