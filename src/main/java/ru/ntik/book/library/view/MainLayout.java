package ru.ntik.book.library.view;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
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
import ru.ntik.book.library.view.admin.BookDefinitionEditForm;
import ru.ntik.book.library.view.admin.CategoryEditForm;
import ru.ntik.book.library.view.components.BookDefinitionPreview;
import ru.ntik.book.library.view.components.CategoryPicker;
import ru.ntik.book.library.view.components.AdaptiveBookPreviewLayout;

import java.util.List;

@Route("")
@SpringComponent
@UIScope
public class MainLayout extends HorizontalLayout {

    // services
    private final BookDefinitionService bookDefinitionService;
    private final CategoryService categoryService;

    private boolean isAdminMode = true;
    // UI components
    private final VerticalLayout leftMenu = new VerticalLayout();

    private final CategoryPicker categoryPicker;
    private final Image logo = new Image(
            "https://static.tildacdn.com/tild3262-3336-4562-a164-326236316164/Frame.svg", "logo");
    private final Button addRootCategoryButton = new Button("+");
    private final VerticalLayout mainRegion = new VerticalLayout();
    private final HorizontalLayout searchRegion = new HorizontalLayout();
    private final TextField searchBox = new TextField();
    private final Button searchButton = new Button("Найти");
    private final Dialog bookDialog = new Dialog();
    private final Dialog categoryDialog = new Dialog();
    private final AdaptiveBookPreviewLayout contentRegion = new AdaptiveBookPreviewLayout();
    private Category selectedCategory = null;

    @Autowired
    MainLayout(CategoryService categoryService, BookDefinitionService bookDefinitionService) {
        this.bookDefinitionService = bookDefinitionService;
        this.categoryService = categoryService;
        // UI
        // Left menu
        categoryPicker = new CategoryPicker(categoryService, isAdminMode);
        categoryPicker.addSelectionListener(this::updateContent);
        if (isAdminMode) {
            categoryPicker.addEditButtonListener(this::openEditCategoryDialog);
        }

        leftMenu.add(logo, categoryPicker);
        add(leftMenu);

        // Main region
        // - Search area
        searchBox.setMinWidth("75%");
        searchBox.setId("search-bar");
        searchRegion.setWidth("100%");
        searchRegion.add(searchBox, searchButton);

        // - Content
        contentRegion.setId("content-region");
        updateContent();
        mainRegion.setMinWidth("75%");
        mainRegion.add(searchRegion, contentRegion);
        add(mainRegion);

        // making content grid "responsive"
        UI.getCurrent().getPage().addBrowserWindowResizeListener(e -> {
            // approximating available space
            System.out.println("width changed");
            int width = (int) Math.round(0.75 * e.getWidth()); // account for mainRegion's min width = 75% constrain
            contentRegion.updateWidth(width);
        });

        if (isAdminMode) {
            // TODO: add edit-categories-mode
            addRootCategoryButton.setId("add-root-category-button");
            addRootCategoryButton.addClickListener(e->openAddCategoryDialog(categoryService.findRoot()));
            add(categoryDialog, bookDialog);
        }
    }

    private void updateContent() {
        updateContent(selectedCategory);
    }

    private void updateContent(Category category) {
        if(category != null) {
            selectedCategory = category;
        }
        contentRegion.removeAll();

        List<BookDefinition> books = List.of();
        if(selectedCategory != null) {
            books = bookDefinitionService.findByCategories(categoryPicker.getCategoryWithChildren(selectedCategory));
        }

        for (BookDefinition bookDefinition : books) {
            VerticalLayout previewLayout = new VerticalLayout();
            BookDefinitionPreview preview = new BookDefinitionPreview(bookDefinition);
            if (isAdminMode) {
                // adding edit button

                Button editBookButton = new Button(new Icon("lumo", "edit"));
                editBookButton.getElement().getStyle().set("position","relative");
                editBookButton.getElement().getStyle().set("float","right");
                editBookButton.addClickListener(e->openEditBookDialog(bookDefinition));
                preview.floatMenu.add(editBookButton);
            }

            preview.addClickListener(e->UI.getCurrent().navigate("book/" + bookDefinition.getId()));
            previewLayout.add(preview);
            contentRegion.add(previewLayout);
        }

        if (isAdminMode) {
            if (selectedCategory != null) {
                Button addBookButton = new Button("+");
                addBookButton.addClickListener(e -> openAddBookDialog(selectedCategory));

                addBookButton.getStyle().setBoxShadow("inset 0 0 0 1px var(--lumo-contrast-30pct)");

                addBookButton.setWidth("250px");
                addBookButton.setHeight("300px");

                contentRegion.add(addBookButton);
            }
        } else {
            if (books.isEmpty()) {
                contentRegion.add(new Span("Не найдено книг в данной категории"));
            }
        }
    }

    // BookDefinitions
    private void openEditBookDialog(@NotNull BookDefinition bookDefinition) {
        BookDefinitionEditForm editForm = initBookDefinitionEditForm(
                new BookDefinitionEditForm(bookDefinitionService, categoryService, bookDefinition)
        );

        categoryDialog.setId("book-definition-edit-dialog");
        categoryDialog.removeAll();
        categoryDialog.add(editForm);
        categoryDialog.setHeaderTitle("Изменить " + "\"" + bookDefinition.getName() + "\"");
        categoryDialog.open();
    }

    private void openAddBookDialog(@NotNull Category category) {
        if(category == null) {
            throw new IllegalArgumentException("Category can't be null");
        }

        BookDefinitionEditForm addForm = initBookDefinitionEditForm(
                new BookDefinitionEditForm(bookDefinitionService, categoryService, category)
        );
        categoryDialog.setId("book-definition-add-dialog");
        categoryDialog.removeAll();
        categoryDialog.add(addForm);
        categoryDialog.setHeaderTitle("Создать новую книгу");
        categoryDialog.open();
    }
    private BookDefinitionEditForm initBookDefinitionEditForm(BookDefinitionEditForm editForm) {
        editForm.addOnSaveListener(cat -> {
            updateContent();
            categoryDialog.close();
        });
        editForm.addOnDeleteListener(cat->{
            updateContent();
            categoryDialog.close();
        });
        editForm.addOnCloseListener(cat-> categoryDialog.close());
        return editForm;
    }

    // Categories
    private void openEditCategoryDialog(Category category) {
        CategoryEditForm editForm = getCategoryEditForm(category, null);

        categoryDialog.setId("category-edit-dialog");
        categoryDialog.removeAll();
        categoryDialog.add(editForm);
        categoryDialog.setHeaderTitle("Изменить категорию " + "\"" + category.getName() + "\"");
        add(categoryDialog);
        categoryDialog.open();
    }

    private void openAddCategoryDialog(Category parent) {
        CategoryEditForm addForm = getCategoryEditForm(null, parent);

        categoryDialog.setId("category-add-dialog");
        categoryDialog.removeAll();
        categoryDialog.add(addForm);
        if(parent.getParent() == null)
        {
            categoryDialog.setHeaderTitle("Создать новую корневую категорию");
        } else {
            categoryDialog.setHeaderTitle("Создать под-категорию " + "\"" + parent.getName() + "\"");
        }
        add(categoryDialog);
        categoryDialog.open();
    }

    private CategoryEditForm getCategoryEditForm(Category category, Category parent) {
        CategoryEditForm editForm = new CategoryEditForm(this.categoryService, category, parent);
        editForm.addOnSaveListener(cat -> {
            categoryPicker.update();
            categoryDialog.close();
        });
        editForm.addOnDeleteListener(cat->{
            categoryPicker.update();
            categoryDialog.close();
        });
        editForm.addOnCloseListener(cat->categoryDialog.close());
        return editForm;
    }
}