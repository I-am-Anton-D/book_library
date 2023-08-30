package ru.ntik.book.library.view.admin;

import com.github.mvysny.kaributesting.v10.GridKt;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
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

import java.util.List;

import static com.github.mvysny.kaributesting.v10.LocatorJ._get;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@ActiveProfiles("h2")
@DirtiesContext
class CategoryEditLayoutTest extends AbstractUITest {
    @DisplayName("Поверхностный тест")
    @Test
    void smokeTest() {
        assertThat(UI.getCurrent()).isNotNull();
        assertThatCode(() -> UI.getCurrent().navigate(CategoryEditLayout.class)).doesNotThrowAnyException();
        assertThatCode(UI.getCurrent()::getChildren).doesNotThrowAnyException();
    }

    @DisplayName("Наличие основных элементов")
    @Test
    void uiIntegrityTest() {
        UI.getCurrent().navigate(CategoryEditLayout.class);
        // TODO: Implement, when UI is fully realized
    }

    @DisplayName("Корректность загрузки категорий")
    @Test
    void categoryTreeIntegrity() {
        UI.getCurrent().navigate(CategoryEditLayout.class);
        // has categories list
        TreeGrid<Category> tree = _get(TreeGrid.class, spec->spec.withId("category-edit-tree"));

        // has data column
        List<Component> treeElements = tree.getChildren().toList();
        assertThat(treeElements).size().isEqualTo(3);

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

    @DisplayName("Диалог \"Создать категорию\" открывается")
    @Test
    void addCategoryButtonTest() {
        UI.getCurrent().navigate(CategoryEditLayout.class);

        // Getting grid
        TreeGrid<Category> grid = _get(TreeGrid.class, spec->spec.withId("category-edit-tree"));
        // Getting Buttons in first row
        HorizontalLayout buttons = (HorizontalLayout) GridKt._getCellComponent(grid, 0, "buttons");

        // getting "+" button and clicking it
        Button addButton =  _get(buttons, Button.class, spec->spec.withIcon("lumo", "plus"));
        assertThat(addButton).isNotNull();
        assertThatCode(addButton::click).doesNotThrowAnyException();

        // making sure dialog is opened
        Dialog addDialog = _get(Dialog.class, spec->{
            spec.withPredicate(el->el.getHeaderTitle().toLowerCase().contains("создать"));
        });
        assertThat(addDialog).isNotNull();
        assertThat(addDialog.isOpened()).isTrue();
    }

    @DisplayName("Диалог \"Изменить категорию\" открывается")
    @Test
    void editCategoryButtonTest() {
        UI.getCurrent().navigate(CategoryEditLayout.class);

        // Getting grid
        TreeGrid<Category> grid = _get(TreeGrid.class, spec->spec.withId("category-edit-tree"));
        // Getting Buttons in first row
        HorizontalLayout buttons = (HorizontalLayout) GridKt._getCellComponent(grid, 0, "buttons");

        // getting edit button and clicking it
        Button editButton =  _get(buttons, Button.class, spec->spec.withIcon("lumo", "edit"));
        assertThat(editButton).isNotNull();
        assertThatCode(editButton::click).doesNotThrowAnyException();

        // making sure dialog is opened
        Dialog addDialog = _get(Dialog.class, spec->{
            spec.withPredicate(el->el.getHeaderTitle().toLowerCase().contains("изменить"));
        });
        assertThat(addDialog).isNotNull();
    }

    @DisplayName("Диалог \"Удалить категорию\" открывается")
    @DirtiesContext
    @Test
    void removeCategoryButtonTest() {
        UI.getCurrent().navigate(CategoryEditLayout.class);

        // Getting grid
        TreeGrid<Category> grid = _get(TreeGrid.class, spec->spec.withId("category-edit-tree"));
        // Getting Buttons in first row
        HorizontalLayout buttons = (HorizontalLayout) GridKt._getCellComponent(grid, 0, "buttons");

        // getting "x" button and clicking it
        Button addButton =  _get(buttons, Button.class, spec->spec.withIcon("lumo", "cross"));
        assertThat(addButton).isNotNull();
        assertThatCode(addButton::click).doesNotThrowAnyException();

        // making sure dialog is opened
        Dialog addDialog = _get(Dialog.class, spec->{
            spec.withPredicate(el->el.getHeaderTitle().toLowerCase().contains("удалить"));
        });
        assertThat(addDialog).isNotNull();
    }
}