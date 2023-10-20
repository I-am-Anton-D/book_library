package ru.ntik.book.library.view.admin;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
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

/**
 * Form for editing given `Category` object or creating new one.
 * <br><br>
 * NOTE: Form performs validation but doesn't save object to repository by itself.<br>
 * You should subscribe to save event (addOnSaveListener()) and do post-processing and saving yourself.
 */
public class CategoryEditForm extends VerticalLayout {

    private Category category;

    private final Binder<Category> binder = new Binder<>(Category.class);
    private final CategoryService categoryService;

    // UI Components
    private TextField title = new TextField();
    private TextArea description = new TextArea();

    private Button addChildButton = new Button("Добавить подкатегорию");
    private Button removeButton = new Button("Удалить", e->this.delete(null));
    private Button submitButton = new Button("Сохранить", e->this.save(null));
    private Button cancelButton = new Button("Отмена", e->this.close(null));
    private final List<ObjectActionListener<Category>> onSaveListeners = new ArrayList<>();
    private final List<ObjectActionListener<Category>> onDeleteListeners = new ArrayList<>();
    private final List<ObjectActionListener<Category>> onCloseListeners = new ArrayList<>();
    public CategoryEditForm(CategoryService categoryService, Category category, Category parent) {
        this.categoryService = categoryService;

        if (category == null) { // assuming creation of new category
            // disabling "remove" and "add child" buttons, as they shouldn't be accessible for freshly created category
            removeButton.setEnabled(false);
            addChildButton.setEnabled(false);

            if (parent == null) { // if parent is also unspecified assuming creation new root category
                parent = categoryService.findRoot();
            }
            // TODO: logic for specifying category creator
            category = new Category("Название категории", "Описание категории", 0L, parent);
        }
        this.category = category;

        bindBean(category);
        setupUI();
    }

    private void bindBean(Category category) {
        binder.forField(title).withValidator(text->text.length() >= MIN_STRING_LENGTH,
                        "Слишком короткое название (должно быть >= " + MIN_STRING_LENGTH + ")").
                withValidator(text->text.length() < PO_MAX_NAME_LENGTH, "Название не может быть длиннее " +
                        PO_MAX_NAME_LENGTH + " символов").bind(Category::getName, Category::setName);
        binder.forField(description).withValidator(text->text.length() >= PO_MIN_DESC_LENGTH,
                        "Слишком короткое описание (должно быть >= " + PO_MIN_DESC_LENGTH + ")").
                withValidator(text->text.length() < LONG_STRING_LENGTH, "Описание не может быть длиннее " +
                        LONG_STRING_LENGTH + " символов").bind(Category::getDescription, Category::setDescription);
        binder.readBean(category);
    }

    private void setupUI() {
        if (addChildButton.isEnabled()) {
            addChildButton.addClickListener(e->openAddChildDialog(this.category));
        }

        title.setId("category-form-name");
        title.setWidthFull();
        title.setLabel("Название");
        title.setPlaceholder("Название");
        description.setId("category-form-desc");
        description.setMinHeight("2em");
        description.setMaxHeight("6em");
        description.setWidthFull();
        description.setLabel("Описание");
        description.setPlaceholder("Описание");

        submitButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addChildButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        removeButton.addThemeVariants(ButtonVariant.LUMO_ERROR);

        add(title, description, new HorizontalLayout(addChildButton, removeButton), new HorizontalLayout(submitButton, cancelButton));
    }

    public void addOnSaveListener(ObjectActionListener<Category> listener) {
        onSaveListeners.add(listener);
    }
    public void addOnDeleteListener(ObjectActionListener<Category> listener) {
        onDeleteListeners.add(listener);
    }
    public void addOnCloseListener(ObjectActionListener<Category> listener) {
        onCloseListeners.add(listener);
    }

    private void save(Category category) {
        if (category == null) {
            category = this.category;
        }

        try {
            binder.writeBean(category);
            categoryService.save(category);
            Notification.show("Сохранено").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            for (ObjectActionListener<Category> listener : onSaveListeners) {
                listener.onPerformed(category);
            }
        } catch (ValidationException e) {
            Notification.show("Введены некорректные данные").addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void delete(Category category) {
        if (category == null) {
            category = this.category;
        }

        if (!categoryService.isEmpty(category)) {
            Notification.show("Невозможно удалить. Категория не пуста.").addThemeVariants(NotificationVariant.LUMO_ERROR);
        } else {
            categoryService.remove(category);
            Notification.show("Удалено").addThemeVariants(NotificationVariant.LUMO_PRIMARY);
            for (ObjectActionListener<Category> listener : onDeleteListeners) {
                listener.onPerformed(category);
            }
        }
    }

    private void close(Category category) {
        if (category == null) {
            category = this.category;
        }

        for (ObjectActionListener<Category> listener : onCloseListeners) {
            listener.onPerformed(category);
        }
    }

    private void openAddChildDialog(Category category) {
        CategoryEditForm addView = new CategoryEditForm(categoryService, null, category);
        Dialog addDialog = new Dialog();
        addDialog.setId("category-add-child-dialog");

        addView.addOnSaveListener(listener->{
            for (ObjectActionListener<Category> externalListener : onSaveListeners) {
                externalListener.onPerformed(category);
            }
            Notification.show("Подкатегория сохранена").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            addDialog.close();
            this.close(category);
        });
        addView.addOnCloseListener(listener->{
            addDialog.close();
            this.close(category);
        });

        addDialog.setHeaderTitle("Создать под-категорию " + "\"" + category.getName() + "\"");

        addDialog.add(addView);
        add(addDialog);
        addDialog.open();
    }

    // TODO: modal dialog for picking parent category or other solution for moving sub-categories
}
