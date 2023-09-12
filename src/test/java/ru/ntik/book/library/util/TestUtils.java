package ru.ntik.book.library.util;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.router.RouteConfiguration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

public class TestUtils {
    /**
     * Asserts that navigation from <code>fromView</code> to <code>toView</code> took place
     * without exceptions
     * @param navigationButton button to trigger navigation
     * @param fromView original view class
     * @param toView destination view class
     */
    public static void assertNavigated(Button navigationButton, Class<? extends Component> fromView, Class<? extends Component> toView) {
        // adding navigation event listeners to implement navigation testing
        UI.getCurrent().addAfterNavigationListener(event->{
            String previousRoute = RouteConfiguration.forSessionScope()
                    .getUrl(fromView);
            String targetRoute = RouteConfiguration.forSessionScope()
                    .getUrl(toView);
            String actualRoute = event.getLocation().getPath();

            assertThat(actualRoute).isNotEqualTo(previousRoute);
            assertThat(actualRoute).isEqualTo(targetRoute);
        });

        assertThatCode(navigationButton::click).doesNotThrowAnyException();
    }
}
