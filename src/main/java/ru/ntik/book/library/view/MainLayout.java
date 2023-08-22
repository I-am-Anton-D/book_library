package ru.ntik.book.library.view;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.data.provider.hierarchy.TreeData;
import com.vaadin.flow.data.provider.hierarchy.TreeDataProvider;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.beans.factory.annotation.Autowired;
import ru.ntik.book.library.domain.Category;
import ru.ntik.book.library.service.CategoryService;

import java.util.ArrayList;
import java.util.List;

@Route("")
@SpringComponent
@UIScope
public class MainLayout extends HorizontalLayout {

    private final CategoryService categoryService;
    private final TreeData<Category> treeData = new TreeData<>();
    private final TreeGrid<Category> categoryTree = new TreeGrid<>();
    private final List<Category> categories = new ArrayList<>();

    // UI components
    private final VerticalLayout leftMenu = new VerticalLayout();
    private final Image logo = new Image(
            "https://static.tildacdn.com/tild3262-3336-4562-a164-326236316164/Frame.svg", "logo");
    private final Button addCategoryButton = new Button("Добавить категорию");
    private final TextField searchBox = new TextField();
    private final Button searchButton = new Button("Найти");
    private final Image contentMock = new Image("https://www.mrw.it/img/cope/0iwkf4_1609360688.jpg", "placeholder");

    @Autowired
    MainLayout(CategoryService categoryService) {
        this.categoryService = categoryService;
        initCategories();

        // Left menu
        leftMenu.add(logo);
        leftMenu.add(categoryTree);
        leftMenu.add(addCategoryButton);

        addCategoryButton.addClickListener(this::openCreateCategoryView);

        // - Search area
        searchBox.setMinWidth("75%");
        searchBox.setId("search-bar");

        HorizontalLayout searchRegion = new HorizontalLayout();
        searchRegion.setWidth("100%");
        searchRegion.add(searchBox);
        searchRegion.add(searchButton);

        // - Content
        contentMock.setWidth("80%");
        VerticalLayout contentRegion = new VerticalLayout();
        contentRegion.add(contentMock);

        VerticalLayout mainRegion = new VerticalLayout();
        mainRegion.setMinWidth("75%");
        mainRegion.add(searchRegion, contentRegion);
        add(leftMenu, mainRegion);
    }

    private void openCreateCategoryView(ClickEvent<Button> e) {
        // test method, replace with actual content
        addCategoryButton.setText("Категория добавлена");
    }

    private void initCategories() {
        categories.addAll(categoryService.findAll());

        // getting first children of root
        List<Category> rootCategories = categories.stream().
                filter(cat -> cat.getParent() != null && cat.getParent().getParent() == null).toList();
        treeData.addRootItems(rootCategories);
        addChildrenRecursively(rootCategories, this::getSubcategories);

        categoryTree.setId("category-tree");
        categoryTree.addHierarchyColumn(Category::getName).setHeader("Категории");
        categoryTree.setDataProvider(new TreeDataProvider<>(treeData));
    }

    private void addChildrenRecursively(List<Category> categories, ValueProvider<Category, List<Category>> childProvider) {
        categories.forEach(category -> {
            List<Category> children = childProvider.apply(category);
            treeData.addItems(category, children);
            if (!children.isEmpty()) {
                addChildrenRecursively(children, childProvider);
            }
        });
    }

    private List<Category> getSubcategories(Category category) {
        return categories.stream().filter(cat -> cat.getParent() == category).toList();
    }
}