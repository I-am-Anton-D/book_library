package ru.ntik.book.library.view.admin;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import ru.ntik.book.library.domain.BookDefinition;
import ru.ntik.book.library.domain.Category;
import ru.ntik.book.library.service.BookDefinitionService;
import ru.ntik.book.library.service.CategoryService;
import ru.ntik.book.library.view.MainLayout;
import ru.ntik.book.library.view.components.CategoryPicker;

import java.util.List;

@Route("edit-books")
@SpringComponent
@UIScope
public class BookDefinitionEditLayout extends VerticalLayout {
    private final BookDefinitionService bookDefinitionService;
    private final CategoryService categoryService;

    private List<Category> categories = List.of();
    // UI
    private final Button backButton = new Button("< На главную", e-> UI.getCurrent().navigate(MainLayout.class));
    private final TextField searchComponent = new TextField("Placeholder for search component");
    private final Grid<BookDefinition> bookGrid = new Grid<>();
    private Button addButton = new Button("+");
    private CategoryPicker categoryPicker;
    private Dialog bookDialog = new Dialog();
    private VerticalLayout booksList;

    @Autowired
    public BookDefinitionEditLayout(CategoryService categoryService, BookDefinitionService bookDefinitionService) {
        this.bookDefinitionService = bookDefinitionService;
        this.categoryService = categoryService;
        categoryPicker = new CategoryPicker(categoryService, true);

        updateBookGrid();

        backButton.setId("back-navigation-button");

        bookGrid.setId("book-grid");
        bookGrid.addSelectionListener(event->
                event.getFirstSelectedItem().ifPresent(this::openEditDialog)
        );
        bookGrid.addColumn(BookDefinition::getName, "Книги");

        categoryPicker.addSelectionListener(cats->{
            categories = cats;
            updateBookGrid();
        });

        addButton.setEnabled(false);

        bookDialog.setMinWidth("60%");
        bookDialog.setCloseOnOutsideClick(false);
        bookDialog.setCloseOnEsc(false);

        HorizontalLayout topLayout = new HorizontalLayout(backButton, searchComponent);
        booksList = new VerticalLayout(bookGrid, addButton);
        HorizontalLayout content = new HorizontalLayout(categoryPicker, booksList);

        booksList.setMinWidth("75%");
        topLayout.setWidth("100%");
        content.setWidth("100%");
        add(topLayout, content);
    }
    private void updateBookGrid() {
        List<BookDefinition> bookDefinitions = bookDefinitionService.findByCategories(categories);
        bookGrid.setItems(bookDefinitions);
        bookGrid.getDataProvider().refreshAll();
        if(categories.size() == 1) {
            /*  bookList.replace() has weird bug where it creates duplicates of Component
                so instead of it will be used remove-add cycle */
            booksList.remove(addButton);
            addButton = new Button("+", e -> openAddDialog(categories.get(0)));
            booksList.add(addButton);
        } else {
            addButton.setEnabled(false);
        }
    }

    private void openEditDialog(@NotNull BookDefinition bookDefinition) {
        BookDefinitionEditForm editForm = initBookDefinitionEditForm(
                new BookDefinitionEditForm(bookDefinitionService, categoryService, bookDefinition)
        );

        bookDialog.setId("book-definition-edit-dialog");
        bookDialog.removeAll();
        bookDialog.add(editForm);
        bookDialog.setHeaderTitle("Изменить " + "\"" + bookDefinition.getName() + "\"");
        add(bookDialog);
        bookDialog.open();
    }

    private void openAddDialog(@NotNull Category category) {
        BookDefinitionEditForm addForm = initBookDefinitionEditForm(
                new BookDefinitionEditForm(bookDefinitionService, categoryService, category)
        );
        bookDialog.setId("book-definition-add-dialog");
        bookDialog.removeAll();
        bookDialog.add(addForm);
        bookDialog.setHeaderTitle("Создать новую книгу");
        add(bookDialog);
        bookDialog.open();
    }

    private BookDefinitionEditForm initBookDefinitionEditForm(BookDefinitionEditForm editForm) {
        editForm.addOnSaveListener(cat -> {
            updateBookGrid();
            bookDialog.close();
        });
        editForm.addOnDeleteListener(cat->{
            updateBookGrid();
            bookDialog.close();
        });
        editForm.addOnCloseListener(cat->bookDialog.close());
        return editForm;
    }
}
