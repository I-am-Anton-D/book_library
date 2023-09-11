package ru.ntik.book.library.view.admin;

import com.github.mvysny.kaributesting.v10.GridKt;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.spring.annotation.UIScope;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import ru.ntik.book.library.domain.BookDefinition;
import ru.ntik.book.library.domain.Category;
import ru.ntik.book.library.view.AbstractUITest;
import ru.ntik.book.library.view.components.CategoryPicker;

import java.util.List;

import static com.github.mvysny.kaributesting.v10.LocatorJ.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@ActiveProfiles("h2")
@UIScope
class BookDefinitionEditFormTest extends AbstractUITest {

    @DisplayName("Диалог открывается")
    @Test
    void smokeTest() {
        UI.getCurrent().navigate(BookDefinitionEditLayout.class);
        assertThatCode(()->openDialog("cat A", "BOOK_C")).doesNotThrowAnyException();
    }

    @DisplayName("Основные элементы присутствуют")
    @Test
    void uiIntegrityTest() {
        UI.getCurrent().navigate(BookDefinitionEditLayout.class);
        Dialog editDialog = openDialog("cat A", "BOOK_C");

        // basic edit fields
        assertThat(_get(editDialog, TextField.class, spec->spec.withId("book-form-name"))).isNotNull();
        assertThat(_get(editDialog, TextArea.class, spec->spec.withId("book-form-desc"))).isNotNull();
        assertThat(_get(editDialog, IntegerField.class, spec->spec.withId("book-form-year"))).isNotNull();
        assertThat(_get(editDialog, IntegerField.class, spec->spec.withId("book-form-pages"))).isNotNull();
        assertThat(_get(editDialog, Select.class, spec->spec.withId("book-form-lang"))).isNotNull();

        // sub-dialog buttons
        assertThat(_get(editDialog, Button.class, spec->spec.withId("book-category-button"))).isNotNull();

        // bottom buttons
        assertThat(_get(editDialog, Button.class, spec->spec.withId("dialog-submit-button"))).isNotNull();
        assertThat(_get(editDialog, Button.class, spec->spec.withId("dialog-cancel-button"))).isNotNull();
        assertThat(_get(editDialog, Button.class, spec->spec.withId("dialog-remove-button"))).isNotNull();
    }

    @DisplayName("Диалог закрывается")
    @Test
    void testCloseDialog() {
        UI.getCurrent().navigate(BookDefinitionEditLayout.class);
        Dialog editDialog = openDialog("cat A", "BOOK_C");
        clickCancel(editDialog);
        assertThat(editDialog.isOpened()).isFalse();
    }

    @DisplayName("Данные изменяются")
    @DirtiesContext
    @Test
    void testBasicEditing() {
        UI.getCurrent().navigate(BookDefinitionEditLayout.class);
        Dialog editDialog = openDialog("cat A", "BOOK_C");

        // setting some values
        String name = "BOOK_X";
        String desc = "Test description";
        int year = 1970;
        int pageCount = 500;
        String cover = "Test cover type";
        String isbn = "9999999999"; // 10 chars

        _setValue(_get(editDialog, TextField.class, spec->spec.withId("book-form-name")), name);
        _setValue(_get(editDialog, TextArea.class, spec->spec.withId("book-form-desc")), desc);
        _setValue(_get(editDialog, IntegerField.class, spec->spec.withId("book-form-year")),year);
        _setValue(_get(editDialog, IntegerField.class, spec->spec.withId("book-form-pages")),pageCount);
        _setValue(_get(editDialog, TextField.class, spec->spec.withId("book-form-cover")), cover);
        _setValue(_get(editDialog, TextField.class, spec->spec.withId("book-form-isbn")), isbn);

        clickSave(editDialog);
        assertThat(editDialog.isOpened()).isFalse();

        editDialog = openDialog("cat A", name);

        // validating values saved correctly
        assertThat(_get(editDialog, TextField.class, spec->spec.withId("book-form-name")).getValue()).isEqualTo(name);
        assertThat(_get(editDialog, TextArea.class, spec->spec.withId("book-form-desc")).getValue()).isEqualTo(desc);
        assertThat(_get(editDialog, IntegerField.class, spec->spec.withId("book-form-year")).getValue()).isEqualTo(year);
        assertThat(_get(editDialog, IntegerField.class, spec->spec.withId("book-form-pages")).getValue()).isEqualTo(pageCount);
        assertThat(_get(editDialog, TextField.class, spec->spec.withId("book-form-cover")).getValue()).isEqualTo(cover);
        assertThat(_get(editDialog, TextField.class, spec->spec.withId("book-form-isbn")).getValue()).isEqualTo(isbn);
    }

    @DisplayName("Смена категории")
    @Test
    @DirtiesContext
    void testCategoryChange() {
        UI.getCurrent().navigate(BookDefinitionEditLayout.class);
        Dialog editDialog = openDialog("cat A", "BOOK_C");
        // setting random isbn, because else validation will not let us save
        _setValue(_get(editDialog, TextField.class, spec->spec.withId("book-form-isbn")), "9999999999");

        // opening category picker dialog
        Button categoryButton = _get(editDialog, Button.class, spec->spec.withId("book-category-button"));
        assertThatCode(categoryButton::click).doesNotThrowAnyException();
        // finding category picker sub-dialog
        CategoryPicker categoryPicker = _find(editDialog, CategoryPicker.class).stream().findAny().orElse(null);
        assertThat(categoryPicker).isNotNull();
        TreeGrid<Category> categoryTreeGrid = _get(categoryPicker, TreeGrid.class);
        // changing category to 'cat B'
        selectCategory(categoryTreeGrid, "cat B");
        clickSave(editDialog);
        assertThat(editDialog.isOpened()).isFalse();

        assertThatCode(()->openDialog("cat B", "BOOK_C")).doesNotThrowAnyException();
    }

    /**
     * @param categoryName to search book in
     * @param bookName to search by
     * @return edit dialog for specified book
     */
    Dialog openDialog(String categoryName, String bookName) {
        TreeGrid<Category> categoryTreeGrid = _get(_get(CategoryPicker.class),(TreeGrid.class));

        assertThat(categoryTreeGrid).isNotNull();

        // getting category with specified name
        selectCategory(categoryTreeGrid, categoryName);

        Grid<BookDefinition> bookGrid = _find(Grid.class, spec->spec.withId("book-grid")).
                stream().findAny().orElse(null);
        assertThat(bookGrid).isNotNull();

        // getting book with specified name from given category
        List<String> bookNames = GridKt._findAll(bookGrid).stream().map(BookDefinition::getName).toList();
        int bookRowIndex = bookNames.indexOf(bookName);
        assertThat(bookRowIndex).isNotNegative().isLessThan(bookNames.size());
        GridKt._selectRow(bookGrid, bookRowIndex);
        // opening it's edit dialog
        GridKt._selectRow(bookGrid, bookRowIndex);

        // Making sure dialog had opened
        Dialog editDialog = getEditDialog();
        assertThat(editDialog).isNotNull();
        assertThat(editDialog.isOpened()).isTrue();
        return editDialog;
    }

    /** Selects category with specified name
     * Warning, expands category tree for accessing all its elements,
     * which seems to be irreversible in KaribuTesting
     * @param categoryTreeGrid TreeGrid to perform operation on
     * @param categoryName category to select
     */
    void selectCategory(TreeGrid<Category> categoryTreeGrid, String categoryName) {
        GridKt._expandAll(categoryTreeGrid);
        List<String> categoryNames = GridKt._findAll(categoryTreeGrid).stream().map(Category::getName).toList();
        int categoryRowIndex = categoryNames.indexOf(categoryName);
        assertThat(categoryRowIndex).isNotNegative().isLessThan(categoryNames.size());
        GridKt._selectRow(categoryTreeGrid, categoryRowIndex);
    }
    Dialog getEditDialog() {
        return _find(Dialog.class, spec->spec.withId("book-definition-edit-dialog")).
                stream().findAny().orElse(null);
    }

    /** Clicks the "cancel" button, which closes dialog */
    void clickCancel(Dialog editDialog) {
        _get(editDialog, Button.class, spec->spec.withId("dialog-cancel-button")).click();
    }

    /** Clicks the "save" button, which closes dialog */
    void clickSave(Dialog editDialog) {
        _get(editDialog, Button.class, spec->spec.withId("dialog-submit-button")).click();
    }
}