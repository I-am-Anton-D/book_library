package ru.ntik.book.library.view.components;

import com.github.mvysny.kaributesting.v10.GridKt;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.treegrid.TreeGrid;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import ru.ntik.book.library.domain.Category;
import ru.ntik.book.library.service.CategoryService;
import ru.ntik.book.library.view.AbstractUITest;
import ru.ntik.book.library.view.MainLayout;

import java.util.ArrayList;
import java.util.List;

import static com.github.mvysny.kaributesting.v10.LocatorJ._get;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("h2")
class CategoryPickerTest extends AbstractUITest {
    @Autowired
    CategoryService categoryService;

    private List<Category> categories = new ArrayList<>();
    @DisplayName("Базовый тест")
    @Test
    void smokeTest() {
        UI.getCurrent().navigate(MainLayout.class);
        // has categories menu
        CategoryPicker categoryPicker = _get(CategoryPicker.class);
        assertThat(categoryPicker).isNotNull();

        TreeGrid<Category> tree = _get(categoryPicker, TreeGrid.class);
        assertThat(tree).isNotNull();

        // has exactly one data column
        assertThat(tree.getColumns()).hasSize(1);

        // that has actual header
        assertThat(tree.getColumns().get(0).getHeaderText()).isNotEmpty();
    }

    /*
    Categories in expanded form:
    --[Categories]--
    0:     ├── cat A
    1:     │   ├── child of first child
    2:     │   ├── child of first child
    3:     │   └── child of first child
    4:     └── cat B
     */
    @DisplayName("Корректность загрузки категорий")
    @Test
    void categoryTreeIntegrity() {
        UI.getCurrent().navigate(MainLayout.class);
        CategoryPicker categoryPicker = _get(CategoryPicker.class);
        TreeGrid<Category> treeGrid = _get(categoryPicker, TreeGrid.class);

        // parent categories are loaded
        GridKt.expectRow(treeGrid, 0, "cat A");
        GridKt.expectRow(treeGrid, 1, "cat B");

        // child categories loaded correctly
        GridKt._expandAll(treeGrid);
        GridKt.expectRows(treeGrid, 5);
        GridKt.expectRow(treeGrid, 1, "child of first child");
        GridKt.expectRow(treeGrid, 4, "cat B");
    }

    @DisplayName("Корректно обрабатываются нажатия")
    @Test
    void testClickCallback() {
        UI.getCurrent().navigate(MainLayout.class);
        CategoryPicker categoryPicker = _get(CategoryPicker.class);
        TreeGrid<Category> treeGrid = _get(categoryPicker, TreeGrid.class);
        GridKt._expandAll(treeGrid);

        // clearing categories list
        categories = new ArrayList<>();

        // adding callback
        categoryPicker.addSelectionListener(selectedCategories->{categories = selectedCategories;});

        // State: no categories selected
        assertThat(categories).isEmpty();

        // select second
        GridKt._selectRow(treeGrid, 1);
        assertThat(categories).map(Category::getName).contains("child of first child");

        // KaribuTestig doesn't seem to support multi-select, but it can select all elements at once
        GridKt._selectAll(treeGrid);
        assertThat(categories).map(Category::getName).contains(
                "cat A",
                "child of first child", "child of first child", "child of first child",
                "cat B");
    }

    @DisplayName("Обновление")
    @DirtiesContext
    @Test
    void testUpdating() {
        UI.getCurrent().navigate(MainLayout.class);
        CategoryPicker categoryPicker = _get(CategoryPicker.class);
        TreeGrid<Category> treeGrid = _get(categoryPicker, TreeGrid.class);
        GridKt._expandAll(treeGrid);

        List<Category> categories = GridKt._findAll(treeGrid);
        assertThat(categories).map(Category::getName).contains(
                "cat A",
                "child of first child", "child of first child", "child of first child",
                "cat B");
        Category childOfA = categories.stream().filter(cat->cat.getName().equals("child of first child")).findFirst().orElse(null);
        categoryService.remove(childOfA);

        categoryPicker.update();
        assertThat(categories).map(Category::getName).contains(
                "cat A",
                "child of first child", "child of first child",
                "cat B");
    }
}