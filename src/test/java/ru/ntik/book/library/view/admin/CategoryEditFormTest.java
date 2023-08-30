package ru.ntik.book.library.view.admin;

import com.github.mvysny.kaributesting.v10.GridKt;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
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

import static com.github.mvysny.kaributesting.v10.LocatorJ.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@SpringBootTest
@ActiveProfiles("h2")
@DirtiesContext
class CategoryEditFormTest extends AbstractUITest {
    @Autowired
    CategoryService categoryService;

    enum DialogMode{add, edit}
    private final String SAVE_BUTTON = "Сохранить";
    private final String CANCEL_BUTTON = "Отмена";
    private final String testName = "Test title";
    private final String testDesc = "Test description";
    @Test
    @DisplayName("Диалог открывается и закрывается")
    void smokeTest() {
        UI.getCurrent().navigate(CategoryEditLayout.class);

        // open edit dialog
        assertThatCode(()->openDialog(0, DialogMode.edit)).doesNotThrowAnyException();
        Dialog editDialog = _get(Dialog.class);
        assertThat(editDialog).isNotNull();
        assertThat(editDialog.isOpened()).isTrue();
        // check that "save" button exists
        assertThat(_get(Button.class, spec->spec.withText(SAVE_BUTTON))).isNotNull();
        // and close dialog with "cancel" button
        assertThatCode(_get(Button.class, spec->spec.withText(CANCEL_BUTTON))::click).doesNotThrowAnyException();
        assertThat(editDialog.isOpened()).isFalse();

        // and same for "add" dialog
        assertThatCode(()->openDialog(0, DialogMode.add)).doesNotThrowAnyException();
        Dialog addDialog = _get(Dialog.class);
        assertThat(addDialog).isNotNull();
        assertThat(addDialog.isOpened()).isTrue();
        assertThat(_get(Button.class, spec->spec.withText(SAVE_BUTTON))).isNotNull();
        assertThatCode(_get(Button.class, spec->spec.withText(CANCEL_BUTTON))::click).doesNotThrowAnyException();
        assertThat(addDialog.isOpened()).isFalse();
    }

    @Test
    @DirtiesContext
    @DisplayName("Создание подкатегории")
    void testCreatingNew() {
        UI.getCurrent().navigate(CategoryEditLayout.class);
        int treeRow = 0;

        // open edit dialog
        openDialog(treeRow, DialogMode.add);
        Dialog dialog = _get(Dialog.class);
        // change name
        _setValue(_get(dialog, TextField.class, spec->spec.withCaption("Название")), testName);
        // change description
        _setValue(_get(dialog, TextArea.class, spec->spec.withCaption("Описание")), testDesc);
        // click save
        assertThatCode(_get(Button.class, spec->spec.withText(SAVE_BUTTON))::click).doesNotThrowAnyException();
        assertThat(dialog.isOpened()).isFalse();

        // get parent
        Category parentCategory = (Category) _get(TreeGrid.class, spec->spec.withId("category-edit-tree"))
                .getTreeData().getRootItems().get(treeRow);

        // get child and make sure it actually exists
        List<Category> children = categoryService.fetchChildren(parentCategory);
        assertThat(children).isNotEmpty();
        Category createdCategory = children.stream().filter(cat->cat.getName().equals(testName)).
                findFirst().orElse(null);
        assertThat(createdCategory).isNotNull();

        // and it was properly modified
        assertThat(createdCategory.getParent()).isEqualTo(parentCategory);
        assertThat(createdCategory.getName()).isEqualTo(testName);
        assertThat(createdCategory.getDescription()).isEqualTo(testDesc);
    }

    @Test
    @DirtiesContext
    @DisplayName("Редактирование категории")
    void testEdit() {
        UI.getCurrent().navigate(CategoryEditLayout.class);
        int treeRow = 0;

        // open edit dialog
        openDialog(treeRow, DialogMode.edit);
        Dialog dialog = _get(Dialog.class);

        // get some category
        Category originalCategory = (Category) _get(TreeGrid.class, spec->spec.withId("category-edit-tree"))
                .getTreeData().getRootItems().get(treeRow);

        // change name
        _setValue(_get(dialog, TextField.class, spec->spec.withCaption("Название")), testName);
        // change description
        _setValue(_get(dialog, TextArea.class, spec->spec.withCaption("Описание")), testDesc);
        // click save
        assertThatCode(_get(Button.class, spec->spec.withText(SAVE_BUTTON))::click).doesNotThrowAnyException();
        assertThat(dialog.isOpened()).isFalse();

        // get new version
        Category modifiedCategory = categoryService.findById(originalCategory.getId());
        assertThat(modifiedCategory).isNotNull();

        // and it was properly modified
        assertThat(modifiedCategory.getId()).isEqualTo(originalCategory.getId());
        assertThat(modifiedCategory.getName()).isEqualTo(testName);
        assertThat(modifiedCategory.getDescription()).isEqualTo(testDesc);
    }

    void openDialog(int row, DialogMode mode) {
        // Getting grid
        TreeGrid<Category> grid = _get(TreeGrid.class, spec->spec.withId("category-edit-tree"));
        // Getting Buttons in first row
        HorizontalLayout buttons = (HorizontalLayout) GridKt._getCellComponent(grid, 0, "buttons");

        // getting button according to selected mode and clicking it
        switch (mode) {
            case add -> _get(buttons, Button.class,
                    spec -> spec.withIcon("lumo", "plus")).click();
            case edit -> _get(buttons, Button.class,
                    spec -> spec.withIcon("lumo", "edit")).click();
        }
    }
}