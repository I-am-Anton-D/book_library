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
import ru.ntik.book.library.view.components.CategoryPicker;

import java.util.List;

import static com.github.mvysny.kaributesting.v10.LocatorJ._get;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@SpringBootTest
@ActiveProfiles("h2")
class CategoryEditLayoutTest extends AbstractUITest {
    @DisplayName("Поверхностный тест")
    @Test
    void smokeTest() {
        assertThat(UI.getCurrent()).isNotNull();
        assertThatCode(() -> UI.getCurrent().navigate(CategoryEditLayout.class)).doesNotThrowAnyException();

        // category picker is present
        CategoryPicker categoryPicker = _get(CategoryPicker.class);
        assertThat(categoryPicker).isNotNull();
    }

    @DisplayName("Создание корневой категории открывается")
    @Test
    void addCategoryButtonTest() {
        UI.getCurrent().navigate(CategoryEditLayout.class);

        // getting "+" button and clicking it
        Button addButton =  _get(Button.class, spec->spec.withIcon("lumo", "plus"));
        assertThat(addButton).isNotNull();
        assertThatCode(addButton::click).doesNotThrowAnyException();

        // making sure dialog is opened
        Dialog addDialog = _get(Dialog.class, spec->spec.withId("category-add-dialog"));
        assertThat(addDialog).isNotNull();
        assertThat(addDialog.isOpened()).isTrue();
    }

    @DisplayName("Диалог \"Изменить категорию\" открывается")
    @Test
    void editCategoryButtonTest() {
        UI.getCurrent().navigate(CategoryEditLayout.class);
        CategoryPicker categoryPicker = _get(CategoryPicker.class);

        Grid<Category> categoryGrid = _get(categoryPicker, Grid.class);
        assertThatCode(()->GridKt._clickItem(categoryGrid, 0)).doesNotThrowAnyException();

        Dialog editDialog = _get(Dialog.class, spec->spec.withId("category-edit-dialog"));
        assertThat(editDialog).isNotNull();
        assertThat(editDialog.isOpened()).isTrue();
    }
}