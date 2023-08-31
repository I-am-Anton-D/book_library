package ru.ntik.book.library.view.components;

import com.github.mvysny.kaributesting.v10.LocatorJ;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.spring.annotation.UIScope;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;
import ru.ntik.book.library.view.AbstractUITest;
import ru.ntik.book.library.view.MainLayout;
import ru.ntik.book.library.view.admin.CategoryEditLayout;

import static com.github.mvysny.kaributesting.v10.LocatorJ._click;
import static com.github.mvysny.kaributesting.v10.LocatorJ._get;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@ActiveProfiles("h2")
@UIScope
class BookDefinitionPreviewTest extends AbstractUITest {
    /* NOTE: Test is relying on existence of and component's usage in MainLayout
    * If component in it replaced/removed or MainLayout discontinued entirely
    * test WILL break*/
    @DisplayName("Корректно работает превью книги")
    @Test
    void testBookPreview(){
        // Get first book preview
        UI.getCurrent().navigate(MainLayout.class);
        BookDefinitionPreview preview = LocatorJ._find(BookDefinitionPreview.class).get(0);
        assertThat(preview).isNotNull();

        // Validate UI integrity
        Span title = _get(preview, Span.class, spec->spec.withText("BOOK_C"));
        assertThat(title).isNotNull();
        Span raiting = _get(preview, Span.class, spec->spec.withPredicate(
                span->span.getText().toLowerCase().contains("рейтинг")
        ));
        assertThat(raiting).isNotNull();
    }

    @DisplayName("Работает редирект по нажатию")
    @Test
    void testNavigation() {
        // Get first book preview
        UI.getCurrent().navigate(MainLayout.class);

        // registering AfterNavigationListener for next assertions
        UI.getCurrent().addAfterNavigationListener(event->{
            String previousRoute = RouteConfiguration.forSessionScope()
                    .getUrl(MainLayout.class);
            // Expecting redirect to "https://site/book/1" where 1 is first BookDefenition's id
            String targetRoute = "book/" + 1;
            String actualRoute = event.getLocation().getPath();

            assertThat(actualRoute).isNotEqualTo(previousRoute);
            assertThat(actualRoute).isEqualTo(targetRoute);
        });

        BookDefinitionPreview preview = LocatorJ._find(BookDefinitionPreview.class).get(0);
        assertThatCode(()->_click(preview)).doesNotThrowAnyException();
        // next assertions performed in listener ...
    }

    // TODO: add test for rating when rating rendering is finalized
}