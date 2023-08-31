package ru.ntik.book.library.view;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;

import com.vaadin.flow.router.RouteConfiguration;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import ru.ntik.book.library.service.CategoryService;
import ru.ntik.book.library.view.admin.CategoryEditLayout;
import ru.ntik.book.library.view.components.BookDefinitionPreview;
import ru.ntik.book.library.view.components.CategoryPicker;

import java.util.List;

import static com.github.mvysny.kaributesting.v10.LocatorJ.*;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("h2")
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
        assertThat(_get(CategoryPicker.class)).isNotNull();

        // has "add category" button
        assertThat(_get(Button.class, spec -> {
            spec.withId("edit-categories-button");
        })).isNotNull();

        // has search menu
        assertThat(_get(TextField.class, spec -> {
            spec.withId("search-bar");
        })).isNotNull();
        assertThat(_get(Button.class, spec -> {
            spec.withText("Найти");
        })).isNotNull();
    }

    @DisplayName("Нажатие кнопки \"Изменить категории\"")
    @Test
    void addCategoryButtonTest() {
        UI.getCurrent().navigate(MainLayout.class);
        Button addButton = _get(Button.class, spec -> {
            spec.withText("Изменить категории");
        });

        // adding navigation event listeners to implement navigation testing
        UI.getCurrent().addAfterNavigationListener(event->{
            String previousRoute = RouteConfiguration.forSessionScope()
                    .getUrl(MainLayout.class);
            String targetRoute = RouteConfiguration.forSessionScope()
                    .getUrl(CategoryEditLayout.class);
            String actualRoute = event.getLocation().getPath();

           assertThat(actualRoute).isNotEqualTo(previousRoute);
           assertThat(actualRoute).isEqualTo(targetRoute);
        });

        assertThatCode(addButton::click).doesNotThrowAnyException();

        // further assertions taking place in NavigationListener specified earlier
    }

    @DisplayName("Корректно отображается список книг")
    @Test
    void testThatBooksDisplayed() {
        // has content region
        UI.getCurrent().navigate(MainLayout.class);
        VerticalLayout contentRegion = _get(VerticalLayout.class,
                spec ->spec.withId("content-region"));
        assertThat(contentRegion).isNotNull();

        // it is not empty
        assertThat(contentRegion.getChildren()).isNotEmpty();
        HorizontalLayout contentRow = (HorizontalLayout) contentRegion.getChildren().findFirst().orElse(null);
        assertThat(contentRow).isNotNull();

        // try getting random child and assert that it exist
        BookDefinitionPreview child = (BookDefinitionPreview) contentRow.getChildren().findAny().orElse(null);
        assertThat(child).isNotNull();
    }

    // TODO: add tests for "search" when respective functionality is added
}