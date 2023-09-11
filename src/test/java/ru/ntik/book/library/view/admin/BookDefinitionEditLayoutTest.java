package ru.ntik.book.library.view.admin;

import com.github.mvysny.kaributesting.v10.GridKt;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.spring.annotation.UIScope;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.ntik.book.library.domain.BookDefinition;
import ru.ntik.book.library.domain.Category;
import ru.ntik.book.library.view.AbstractUITest;
import ru.ntik.book.library.view.MainLayout;
import ru.ntik.book.library.view.components.CategoryPicker;

import static com.github.mvysny.kaributesting.v10.LocatorJ._find;
import static com.github.mvysny.kaributesting.v10.LocatorJ._get;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@SpringBootTest
@ActiveProfiles("h2")
@UIScope
class BookDefinitionEditLayoutTest extends AbstractUITest {
    @DisplayName("Базовый тест")
    @Test
    void smokeTest() {
        assertThatCode(()->UI.getCurrent().navigate(BookDefinitionEditLayout.class)).doesNotThrowAnyException();
    }

    @DisplayName("Все элементы на месте")
    @Test
    void uiIntegrityTest() {
        UI.getCurrent().navigate(BookDefinitionEditLayout.class);
        assertThat(_find(Grid.class, spec->spec.withId("book-grid")).
                stream().findAny()).isPresent();

        assertThat(_find(CategoryPicker.class).stream().findFirst()).isPresent();

        assertThat(_find(Button.class, spec->spec.withId("back-navigation-button")).stream().findAny()).isPresent();
    }

    // books are loaded correctly

    @DisplayName("Кнопка \"На главную\" работает")
    @Test
    void testBackNavigation() {
        UI.getCurrent().navigate(BookDefinitionEditLayout.class);
        Button backButton = _get(Button.class, spec->spec.withId("back-navigation-button"));

        // defining listener
        UI.getCurrent().addAfterNavigationListener(e->{
            String previousRoute = RouteConfiguration.forSessionScope()
                    .getUrl(BookDefinitionEditLayout.class);
            String targetRoute = RouteConfiguration.forSessionScope()
                    .getUrl(MainLayout.class);
            String actualRoute = e.getLocation().getPath();

            assertThat(actualRoute).isEqualTo(targetRoute);
            assertThat(actualRoute).isNotEqualTo(previousRoute);
        });

        assertThatCode(backButton::click).doesNotThrowAnyException();
        // asserting in listener, that navigated to correct page
    }

    @DisplayName("Открывается диалог добавления")
    @Test
    void testAddDialogOpens() {
        UI.getCurrent().navigate(BookDefinitionEditLayout.class);
        TreeGrid<Category> categoryTreeGrid = _get(_get(CategoryPicker.class),(TreeGrid.class));

        assertThat(categoryTreeGrid).isNotNull();
        GridKt._expandAll(categoryTreeGrid);

        //  Category 'child of first child' with id 20 should be 2nd row (index 1) in expanded form
        //  Selecting it in menu (CategoryPicker)
        GridKt._selectRow(categoryTreeGrid, 1);

        //  After selecting single category "add category" button should get enabled
        Button addCategory = _find(Button.class, spec->spec.withText("+")).stream().findFirst().orElse(null);
        assertThat(addCategory).isNotNull();
        //  Clicking it
        assertThatCode(addCategory::click).doesNotThrowAnyException();

        // Making sure dialog had opened
        Dialog addDialog = _find(Dialog.class, spec->spec.withId("book-definition-add-dialog")).
                stream().findAny().orElse(null);
        assertThat(addDialog).isNotNull();
        assertThat(addDialog.isOpened()).isTrue();
    }

    @DisplayName("Открывается диалог редактирования")
    @Test
    void testEditDialogOpens() {
        UI.getCurrent().navigate(BookDefinitionEditLayout.class);
        TreeGrid<Category> categoryTreeGrid = _get(_get(CategoryPicker.class),(TreeGrid.class));

        assertThat(categoryTreeGrid).isNotNull();
        GridKt._expandAll(categoryTreeGrid);

        //  Category 'cat A' with id 18 should be 1st row (index 0) in expanded form
        //  Selecting it in menu (CategoryPicker)
        GridKt._selectRow(categoryTreeGrid, 0);

        // Getting list (actually Grid) of books in category 'cat A'
        Grid<BookDefinition> bookGrid = _find(Grid.class, spec->spec.withId("book-grid")).
                stream().findAny().orElse(null);
        assertThat(bookGrid).isNotNull();

        // selecting 1st row (index 0)
        BookDefinition book = GridKt._get(bookGrid, 0);
        // it should be 'BOOK_C'
        assertThat(book.getName()).isEqualTo("BOOK_C");

        // opening it's edit dialog
        GridKt._selectRow(bookGrid, 0);
        // Making sure dialog had opened
        Dialog editDialog = _find(Dialog.class, spec->spec.withId("book-definition-edit-dialog")).
                stream().findAny().orElse(null);
        assertThat(editDialog).isNotNull();
        assertThat(editDialog.isOpened()).isTrue();
    }
}