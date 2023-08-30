package ru.ntik.book.library.view.admin;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import ru.ntik.book.library.domain.Category;
import ru.ntik.book.library.service.CategoryService;
import ru.ntik.book.library.util.ObjectActionListener;

import java.util.ArrayList;
import java.util.List;

import static ru.ntik.book.library.util.Constants.*;

public class CategoryEditForm extends VerticalLayout {

    private final Category category;

    private final Binder<Category> binder = new Binder<>(Category.class);

    // UI Components
    private TextField title = new TextField();
    private TextArea description = new TextArea();
    private Button submitButton = new Button("Сохранить", e->this.save());
    private Button cancelButton = new Button("Отмена", e->this.close());
    private final List<ObjectActionListener<Category>> onSaveListeners = new ArrayList<>();
    private final List<ObjectActionListener<Category>> onCloseListeners = new ArrayList<>();

    public CategoryEditForm(CategoryService categoryService, Category category, Category parent) {
        if (category == null) {
            Category root = categoryService.findRoot();
            if (parent == null) {
                parent = root;
            }
            // TODO: logic for specifying category creator
            category = new Category("Название категории", null, 0L, parent);
        }

        if(category.getDescription() == null)
            category.setDescription("[Добавить описание]");

        binder.forField(title).withValidator(text->text.length() >= MIN_STRING_LENGTH,
                        "Слишком короткое название (должно быть >= " + MIN_STRING_LENGTH + ")").
                withValidator(text->text.length() < PO_MAX_NAME_LENGTH, "Название не может быть длиннее " +
                        PO_MAX_NAME_LENGTH + " символов").bind(Category::getName, Category::setName);
        binder.forField(description).withValidator(text->text.length() >= PO_MIN_DESC_LENGTH,
                        "Слишком короткое описание (должно быть >= " + PO_MIN_DESC_LENGTH + ")").
                withValidator(text->text.length() < LONG_STRING_LENGTH, "Описание не может быть длиннее " +
                        LONG_STRING_LENGTH + " символов").bind(Category::getDescription, Category::setDescription);
        binder.readBean(category);

        // UI
        title.setWidthFull();
        title.setLabel("Название");
        title.setPlaceholder("Название");
        description.setMinHeight("2em");
        description.setMaxHeight("6em");
        description.setWidthFull();
        description.setLabel("Описание");
        description.setPlaceholder("Описание");

        submitButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        add(title, description, new HorizontalLayout(submitButton, cancelButton));

        this.category = category;
    }
    public void addOnSaveListener(ObjectActionListener<Category> listener) {
        onSaveListeners.add(listener);
    }

    public void addOnCloseListener(ObjectActionListener<Category> listener) {
        onCloseListeners.add(listener);
    }

    private void save() {
        try {
            binder.writeBean(category);
        } catch (ValidationException e) {
            Notification.show("Введены некорректные данные").addThemeVariants(NotificationVariant.LUMO_ERROR);
        }

        for(ObjectActionListener<Category> listener : onSaveListeners) {
            listener.onPerformed(category);
        }
    }

    private void close() {
        for(ObjectActionListener<Category> listener : onCloseListeners) {
            listener.onPerformed(category);
        }
    }

    // TODO: modal dialog for picking parent category or other solution for moving sub-categories
}
