package ru.ntik.book.library.view;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.beans.factory.annotation.Autowired;
import ru.ntik.book.library.domain.BookDefinition;
import ru.ntik.book.library.domain.Category;
import ru.ntik.book.library.service.BookDefinitionService;
import ru.ntik.book.library.service.CategoryService;
import ru.ntik.book.library.view.admin.BookDefinitionEditLayout;
import ru.ntik.book.library.view.admin.CategoryEditLayout;
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

    // UI components
    private final VerticalLayout leftMenu = new VerticalLayout();

    private final CategoryPicker categoryPicker;
    private final Image logo = new Image(
            "https://static.tildacdn.com/tild3262-3336-4562-a164-326236316164/Frame.svg", "logo");
    private final Button editCategoriesButton = new Button("Редактировать категории");
    private final Button editBookDefinitionsButton = new Button("Редактировать книги");
    private final VerticalLayout mainRegion = new VerticalLayout();
    private final HorizontalLayout searchRegion = new HorizontalLayout();
    private final TextField searchBox = new TextField();
    private final Button searchButton = new Button("Найти");
    private final AdaptiveBookPreviewLayout contentRegion = new AdaptiveBookPreviewLayout();

    @Autowired
    MainLayout(CategoryService categoryService, BookDefinitionService bookDefinitionService) {
        this.bookDefinitionService = bookDefinitionService;
        // UI
        // Left menu
        categoryPicker = new CategoryPicker(categoryService, true);
        categoryPicker.addSelectionListener(categories->updateContent(contentRegion, categories));
        // add navigation to "edit categories" page
        editCategoriesButton.addClickListener(e->UI.getCurrent().navigate(CategoryEditLayout.class));
        editCategoriesButton.setId("edit-categories-button");
        editBookDefinitionsButton.addClickListener(e->UI.getCurrent().navigate(BookDefinitionEditLayout.class));
        editBookDefinitionsButton.setId("edit-books-button");
        leftMenu.add(logo, categoryPicker, editCategoriesButton, editBookDefinitionsButton);
        add(leftMenu);

        // Main region
        // - Search area
        searchBox.setMinWidth("75%");
        searchBox.setId("search-bar");
        searchRegion.setWidth("100%");
        searchRegion.add(searchBox, searchButton);

        // - Content
        contentRegion.setId("content-region");
        updateContent(contentRegion, List.of());
        mainRegion.setMinWidth("75%");
        mainRegion.add(searchRegion, contentRegion);
        add(mainRegion);

        // making content grid "responsive"
        UI.getCurrent().getPage().addBrowserWindowResizeListener(e -> {
            // approximating available space
            int width = (int) Math.round(0.75 * e.getWidth()); // account for mainRegion's min width = 75% constrain
            contentRegion.updateWidth(width);
        });
    }

    private void updateContent(VerticalLayout contentRegion, List<Category> categories) {
        contentRegion.removeAll();

        List<BookDefinition> books;
        if(categories.isEmpty()) {
            books = bookDefinitionService.findAll();
        } else {
            // TODO: Implement pagination
            books = bookDefinitionService.findByCategories(categories);
        }

        if(books.isEmpty()) {
            String errorMessage = "";
            if(categories.isEmpty()) {
                errorMessage = "Книги в системе отсутствуют";
            } else if (categories.size() == 1) {
                errorMessage = "Не найдено книг в данной категории";
            } else {
                errorMessage = "Не найдено книг c заданным набором категорий";
            }
            contentRegion.add(new Span(errorMessage));
            return;
        }

        for (BookDefinition bookDefinition : books) {
            BookDefinitionPreview preview = new BookDefinitionPreview(bookDefinition);
            preview.addClickListener(e->UI.getCurrent().navigate("book/" + bookDefinition.getId()));
            contentRegion.add(preview);
        }
    }
}