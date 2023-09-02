package ru.ntik.book.library.view.admin;

import com.github.mvysny.kaributesting.v10.GridKt;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
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
import ru.ntik.book.library.view.components.CategoryPicker;

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
    @Test
    @DisplayName("Основные элементы диалога")
    void smokeTest() {
        UI.getCurrent().navigate(CategoryEditLayout.class);

        // open edit dialog
        assertThatCode(()->openDialog(0)).doesNotThrowAnyException();
        Dialog editDialog = _get(Dialog.class);
        // check that "save" button exists
        assertThat(_get(Button.class, spec->spec.withText(SAVE_BUTTON))).isNotNull();
        // check that "close" button exists
        assertThat(_get(Button.class, spec->spec.withText(CANCEL_BUTTON))).isNotNull();
        // check that "addChild" button exists
        assertThat(_get(Button.class, spec->spec.withText(DELETE_BUTTON))).isNotNull();
        // check that "delete" button exists
        assertThat(_get(Button.class, spec->spec.withText(ADD_CHILD_BUTTON))).isNotNull();

        // check that "name" field exists
        assertThat(_get(TextField.class, spec->spec.withId(NAME_ID))).isNotNull();
        // check that "name" field exists
        assertThat(_get(TextArea.class, spec->spec.withId(DESC_ID))).isNotNull();
    }

    @Test
    @DisplayName("Диалог открывается и закрывается")
    void testOpenClose() {
        UI.getCurrent().navigate(CategoryEditLayout.class);

        // open edit dialog
        assertThatCode(()->openDialog(0)).doesNotThrowAnyException();
        Dialog editDialog = _get(Dialog.class);
        assertThat(editDialog).isNotNull();
        assertThat(editDialog.isOpened()).isTrue();
        // and close dialog with "cancel" button
        assertThatCode(_get(Button.class, spec->spec.withText(CANCEL_BUTTON))::click).doesNotThrowAnyException();
        assertThat(editDialog.isOpened()).isFalse();

        // and same for "add" dialog
        assertThatCode(()->openDialog(0)).doesNotThrowAnyException();
        Dialog addDialog = _get(Dialog.class);
        assertThat(addDialog).isNotNull();
        assertThat(addDialog.isOpened()).isTrue();
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
        openDialog(treeRow);
        Dialog dialog = _get(Dialog.class);

        assertThat(dialog.isOpened()).isTrue();
        // open "add subcategory" dialog
        assertThatCode(_get(dialog, Button.class, spec->spec.withText(ADD_CHILD_BUTTON))::click).doesNotThrowAnyException();
        Dialog addChildDialog = _get(dialog, Dialog.class, spec->spec.withId(ADD_CHILD_DIALOG_ID));
        // change name
        _setValue(_get(addChildDialog, TextField.class, spec->spec.withId(NAME_ID)), testName);
        // change description
        _setValue(_get(addChildDialog, TextArea.class, spec->spec.withId(DESC_ID)), testDesc);
        // click save
        assertThatCode(_get(addChildDialog, Button.class, spec->spec.withText(SAVE_BUTTON))::click).doesNotThrowAnyException();
        assertThat(addChildDialog.isOpened()).isFalse();
        assertThat(dialog.isOpened()).isFalse();

        // get parent
        CategoryPicker categoryPicker = _get(CategoryPicker.class);
        Category parentCategory = (Category) GridKt._getRootItems(_get(categoryPicker, TreeGrid.class)).get(treeRow);
        assertThat(parentCategory).isNotNull();

        // get child and make sure it actually exists
        List<Category> children = categoryService.fetchChildren(parentCategory);
        assertThat(children).isNotEmpty();

        List<Category> all = categoryService.findAll();
        all.forEach(cat-> System.out.println(cat.getName()));

        assertThat(children).map(Category::getName).contains(testName);
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
        openDialog(treeRow);
        Dialog dialog = _get(Dialog.class);

        // get some category
        CategoryPicker categoryPicker = _get(CategoryPicker.class);
        Category originalCategory = (Category) _get(categoryPicker, TreeGrid.class).getTreeData().getRootItems().get(treeRow);

        // change name
        _setValue(_get(dialog, TextField.class, spec->spec.withCaption("Название")), testName);
        // change description
        _setValue(_get(dialog, TextArea.class, spec->spec.withCaption("Описание")), testDesc);
        // click save
        assertThatCode(_get(dialog, Button.class, spec->spec.withText(SAVE_BUTTON))::click).doesNotThrowAnyException();
        assertThat(dialog.isOpened()).isFalse();

        // get new version
        Category modifiedCategory = categoryService.findById(originalCategory.getId());
        assertThat(modifiedCategory).isNotNull();

        // and it was properly modified
        assertThat(modifiedCategory.getId()).isEqualTo(originalCategory.getId());
        assertThat(modifiedCategory.getName()).isEqualTo(testName);
        assertThat(modifiedCategory.getDescription()).isEqualTo(testDesc);
    }

    @Test
    @DirtiesContext
    @DisplayName("Удаление категории")
    void testDeletion() {
        UI.getCurrent().navigate(CategoryEditLayout.class);
        int treeRow = 1;

        // getting snapshot of categories
        List<Category> categories = categoryService.findAll();

        // open edit dialog
        openDialog(treeRow); // selecting sub-category of "cat A"
        Dialog dialog = _get(Dialog.class);

        CategoryPicker categoryPicker = _get(CategoryPicker.class);
        TreeGrid<Category> treeGrid = _get(categoryPicker, TreeGrid.class);
        Category deletedCategory = (Category) GridKt._get(treeGrid, treeRow);

        assertThat(dialog.isOpened()).isTrue();
        // click "Delete"
        assertThatCode(_get(dialog, Button.class, spec->spec.withText(DELETE_BUTTON))::click).doesNotThrowAnyException();
        assertThat(dialog.isOpened()).isFalse();

        List<Category> resultCategories = categoryService.findAll();

        // Checking that selected category was deleted
        assertThat(resultCategories).doesNotContain(deletedCategory);

        // Checking that ONLY selected category was deleted
        //      (simulating deletion with list)
        categories.remove(deletedCategory);
        //      asserting that actual deletion was carried out correctly
        assertThat(resultCategories).isEqualTo(categories);

        // checking that UI gets updated
        categories = categories.stream().
                filter(category->category.getParent() != null).toList(); // excluding root as it doesn't gets displayed
        GridKt._expandAll(treeGrid);
        List<Category> displayedCategories = GridKt._findAll(treeGrid);
        assertThat(displayedCategories).doesNotContain(deletedCategory).containsAll(categories);
    }

    void openDialog(int row) {
        // Getting grid
        CategoryPicker categoryPicker = _get(CategoryPicker.class);
        assertThat(categoryPicker).isNotNull();
        TreeGrid<Category> categoryGrid = _get(categoryPicker, TreeGrid.class);
        assertThat(categoryGrid).isNotNull();

        // getting all rows
        GridKt._expandAll(categoryGrid);
        // opening dialog for row `row`
        GridKt._selectRow(categoryGrid, row);
    }

    private static final String NAME_ID = "category-form-name";
    private static final String DESC_ID = "category-form-desc";
    private static final String ADD_CHILD_DIALOG_ID = "category-add-child-dialog";
    private static final String SAVE_BUTTON = "Сохранить";
    private static final String CANCEL_BUTTON = "Отмена";
    private static final String ADD_CHILD_BUTTON = "Добавить подкатегорию";
    private static final String DELETE_BUTTON = "Удалить";

    private static final String testName = "Test title";
    private static final String testDesc = "Test description";
}