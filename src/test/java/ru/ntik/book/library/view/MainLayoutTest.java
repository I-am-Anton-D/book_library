package ru.ntik.book.library.view;

import com.github.mvysny.kaributesting.v10.*;
import com.github.mvysny.kaributesting.v10.spring.MockSpringServlet;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.spring.SpringServlet;

import kotlin.jvm.functions.Function0;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import ru.ntik.book.library.service.CategoryService;

import static com.github.mvysny.kaributesting.v10.LocatorJ.*;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@DirtiesContext
class MainLayoutTest {
    private static final Routes routes = new Routes().autoDiscoverViews("ru.ntik.book.library.view");
    @Autowired
    protected ApplicationContext ctx;

    @BeforeEach
    public void setup() {
        final Function0<UI> uiFactory = UI::new;
        final SpringServlet servlet = new MockSpringServlet(routes, ctx, uiFactory);
        MockVaadin.setup(uiFactory, servlet);
    }

    @AfterEach
    public void tearDown() {
        MockVaadin.tearDown();
    }

    @Autowired
    CategoryService categoryService;

    @Test
    void smokeTest() {
        assertThat(UI.getCurrent()).isNotNull();
        assertThatCode(() -> UI.getCurrent().navigate(MainLayout.class)).doesNotThrowAnyException();
        assertThatCode(UI.getCurrent()::getChildren).doesNotThrowAnyException();
    }

    @Test
    void uiIntegrityTest() {
        UI.getCurrent().navigate(MainLayout.class);
        // has logo
        assertThat(_get(Image.class, spec -> {
            spec.withPredicate(
                    img -> {
                        return img.getAlt().orElse("").equals("logo");
                    }
            );
        })).isNotNull();

        // has categories menu
        assertThat(_get(TreeGrid.class)).isNotNull();

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
}