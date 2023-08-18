package ru.ntik.book.library.view;

import com.github.mvysny.kaributesting.v10.PrettyPrintTree;
import com.github.mvysny.kaributesting.v10.PrettyPrintTreeKt;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.treegrid.TreeGrid;

import com.vaadin.flow.data.provider.hierarchy.HierarchicalQuery;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.ntik.book.library.domain.Category;
import ru.ntik.book.library.service.CategoryService;

import java.util.List;

import static com.github.mvysny.kaributesting.v10.LocatorJ.*;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DirtiesContext
class MainLayoutTest extends AbstractUITest{
    @Autowired
    CategoryService categoryService;

    @DisplayName("Поверхностный тест")
    @Test
    void smokeTest() {
        assertThat(UI.getCurrent()).isNotNull();
        assertThatCode(() -> UI.getCurrent().navigate(MainLayout.class)).doesNotThrowAnyException();
        assertThatCode(UI.getCurrent()::getChildren).doesNotThrowAnyException();
    }

    @DisplayName("Наличие основных элементов")
    @Test
    void uiIntegrityTest() {
        UI.getCurrent().navigate(MainLayout.class);
        // has logo
        assertThat(_get(Image.class, spec -> {
            spec.withPredicate(
                    img -> img.getAlt().orElse("").equals("logo")
            );
        })).isNotNull();

        // has categories menu
        assertThat(_get(TreeGrid.class, spec->spec.withId("category-tree"))).isNotNull();

        // has "add category" button
        assertThat(_get(Button.class, spec -> {
            spec.withText("Добавить категорию");
        })).isNotNull();

        // has search menu
        assertThat(_get(TextField.class, spec -> {
            spec.withId("search-bar");
        })).isNotNull();
        assertThat(_get(Button.class, spec -> {
            spec.withText("Найти");
        })).isNotNull();
    }

    @DisplayName("Корректность загрузки категорий")
    @Test
    void categoryTreeIntegrity() {
        UI.getCurrent().navigate(MainLayout.class);
        // has categories menu
        //categoryTree.setId("category-tree");
        TreeGrid<Category> tree = _get(TreeGrid.class, spec->spec.withId("category-tree"));

        // has data column
        List<Component> treeElements = tree.getChildren().toList();
        assertThat(treeElements).isNotEmpty();

        // that has actual header
        assertThat(((Grid.Column<Category>)treeElements.get(0)).getHeaderText()).isNotEmpty();

        /* Note: can't check that data is correctly rendered by vaadin,
            but at least can check that it was properly loaded into component */
        assertThat(tree.getTreeData().getRootItems()).asList().isNotEmpty();
        // |- has root categories
        List<Category> rootElements = tree.getTreeData().getRootItems();
        assertThat(rootElements).map(Category::getName).contains("cat A", "cat B");
        // \- and sub-categories
        List<Category> childrenOfFirstRoot = tree.getTreeData().getChildren(rootElements.get(0));
        assertThat(childrenOfFirstRoot).map(Category::getName).contains("child of first child");
    }

    @DisplayName("Нажатие кнопки \"Создать категорию\"")
    @Test
    void addCategoryButtonTest() {
        UI.getCurrent().navigate(MainLayout.class);
        Button addButton = _get(Button.class, spec -> {
            spec.withText("Добавить категорию");
        });

        assertThatCode(addButton::click).doesNotThrowAnyException();
        // TODO: update test when there is an actual functionality

        // current test method just updates button text
        assertThat(addButton.getText()).isEqualTo("Категория добавлена");
    }

    // TODO: add tests for "search" and "add category" buttons when respective functionality is added
}