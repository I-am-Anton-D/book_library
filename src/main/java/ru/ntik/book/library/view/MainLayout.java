package ru.ntik.book.library.view;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.data.provider.hierarchy.TreeData;
import com.vaadin.flow.data.provider.hierarchy.TreeDataProvider;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.beans.factory.annotation.Autowired;
import ru.ntik.book.library.domain.BookDefinition;
import ru.ntik.book.library.domain.Category;
import ru.ntik.book.library.service.BookDefinitionService;
import ru.ntik.book.library.service.CategoryService;
import ru.ntik.book.library.view.admin.CategoryEditLayout;
import ru.ntik.book.library.view.components.BookDefinitionPreview;
import ru.ntik.book.library.view.components.PseudoAdaptiveGridLayout;

import java.util.ArrayList;
import java.util.List;

@Route("")
@SpringComponent
@UIScope
public class MainLayout extends HorizontalLayout {

    // services
    private final BookDefinitionService bookDefinitionService;

    // UI components
    private final VerticalLayout leftMenu = new VerticalLayout();
    private final TreeGrid<Category> categoryTree = new TreeGrid<>();
    private final Image logo = new Image(
            "https://static.tildacdn.com/tild3262-3336-4562-a164-326236316164/Frame.svg", "logo");
    private final Button editCategoriesButton = new Button("Изменить категории");
    private final VerticalLayout mainRegion = new VerticalLayout();
    private final HorizontalLayout searchRegion = new HorizontalLayout();
    private final TextField searchBox = new TextField();
    private final Button searchButton = new Button("Найти");
    private final PseudoAdaptiveGridLayout contentRegion = new PseudoAdaptiveGridLayout();

    private List<Category> categories = new ArrayList<>();

    @Autowired
    MainLayout(CategoryService categoryService, BookDefinitionService bookDefinitionService) {
        this.bookDefinitionService = bookDefinitionService;

        // UI
        // Left menu
        leftMenu.add(logo);
        categoryTree.setId("category-tree");
        categoryTree.addHierarchyColumn(Category::getName).setHeader("Категории");
        TreeData<Category> treeData = categoryService.fetchCategoriesAsTreeData();
        categoryTree.setDataProvider(new TreeDataProvider<>(treeData));
        categoryTree.setSelectionMode(Grid.SelectionMode.MULTI);
        categoryTree.addSelectionListener(e->{
            categories = categoryTree.getSelectedItems().stream().toList();
            updateContent(contentRegion);
        });
        leftMenu.add(categoryTree);
        // navigating to edit categories page
        editCategoriesButton.addClickListener(e->UI.getCurrent().navigate(CategoryEditLayout.class));
        editCategoriesButton.setId("edit-categories-button");
        leftMenu.add(editCategoriesButton);
        add(leftMenu);

        // Main region
        // - Search area
        mainRegion.setMinWidth("75%");
        searchRegion.setWidth("100%");
        searchBox.setMinWidth("75%");
        searchBox.setId("search-bar");
        searchRegion.add(searchBox);
        searchRegion.add(searchButton);
        mainRegion.add(searchRegion);
        // - Content
        contentRegion.setId("content-region");
        updateContent(contentRegion);
        mainRegion.add(contentRegion);
        add(mainRegion);

        // adding "responsive" grid
        UI.getCurrent().getPage().addBrowserWindowResizeListener(e -> tryResizeGrid(e.getWidth()));
    }

    private void tryResizeGrid(int width) {
        width *= 0.75;
        if(width >= 1920) {
            contentRegion.setColumnCount(5);
        } else if (width >= 1280) {
            contentRegion.setColumnCount(4);
        } else if (width >= 1024) {
            contentRegion.setColumnCount(2);
        } else if (width < 750) {
            contentRegion.setColumnCount(1);
        }
    }

    private void updateContent(VerticalLayout contentRegion) {
        contentRegion.removeAll();

        List<BookDefinition> books;
        if(categories.isEmpty()) {
            books = bookDefinitionService.findAll();
        } else {
            // TODO: Also implement pagination
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
            contentRegion.add(new BookDefinitionPreview(bookDefinition));
        }
    }
}