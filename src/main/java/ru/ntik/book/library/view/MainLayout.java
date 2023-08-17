package ru.ntik.book.library.view;

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

import java.util.List;

@Route("")
@SpringComponent
@UIScope
public class MainLayout extends HorizontalLayout {
    @Autowired
    private CategoryService categoryService;
    TreeData<Category> treeData = new TreeData<>();
    TreeGrid<Category> categoryTree = new TreeGrid<>();
    private List<Category> categories = null;

    // UI components
    VerticalLayout leftMenu = new VerticalLayout();
    Image logo = new Image(
            "https://static.tildacdn.com/tild3262-3336-4562-a164-326236316164/Frame.svg", "logo");
    Button addCategoryButton = new Button("Добавить категорию");
    VerticalLayout mainRegion = new VerticalLayout();
    HorizontalLayout searchRegion = new HorizontalLayout();
    TextField searchBox = new TextField();
    Button searchButton = new Button("Найти");
    VerticalLayout contentRegion = new VerticalLayout();
    Image contentMock = new Image("https://www.mrw.it/img/cope/0iwkf4_1609360688.jpg", "placeholder");

    @Autowired
    MainLayout(CategoryService categoryService) {
        this.categoryService = categoryService;

        // Left menu
        leftMenu.add(logo);
        initCategories();
        categoryTree.addHierarchyColumn(Category::getName).setHeader("Категории");
        categoryTree.setDataProvider(new TreeDataProvider<>(treeData));
        leftMenu.add(categoryTree);
        leftMenu.add(addCategoryButton);
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
        contentMock.setWidth("80%");
        contentRegion.add(contentMock);
        mainRegion.add(contentRegion);
        add(mainRegion);
    }

    private void initCategories() {
        categoryService.tryCreateRootCategory();
        categories = categoryService.fetchAll();

        // getting first children of root
        List<Category> rootCategories = categories.stream().
                filter(cat -> cat.getParent() != null && cat.getParent().getParent() == null).toList();
        treeData.addRootItems(rootCategories);
        addChildrenRecursivley(rootCategories, this::getSubcategories);
    }

    private void addChildrenRecursivley(List<Category> categories, ValueProvider<Category, List<Category>> childProvider) {
        categories.forEach(category ->
        {
            List<Category> chidren = childProvider.apply(category);
            treeData.addItems(category, chidren);
            if (!chidren.isEmpty()) {
                addChildrenRecursivley(chidren, childProvider);
            }
        });
    }

    private List<Category> getSubcategories(Category category) {
        return categories.stream().filter(cat -> cat.getParent() == category).toList();
    }
}