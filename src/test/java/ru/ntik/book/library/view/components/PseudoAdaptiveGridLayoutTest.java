package ru.ntik.book.library.view.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.ntik.book.library.view.AbstractUITest;

import java.util.ArrayList;
import java.util.List;

import static com.github.mvysny.kaributesting.v10.LocatorJ._get;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class PseudoAdaptiveGridLayoutTest {
    @DisplayName("Базовый тест")
    @Test
    void smokeTest() {
        VerticalLayout root = new VerticalLayout();
        PseudoAdaptiveGridLayout layout = new PseudoAdaptiveGridLayout();
        assertThatCode(()->root.add(layout)).doesNotThrowAnyException();
    }
    @DisplayName("Заполнение элементами")
    @Test
    void testPopulating() {
        VerticalLayout root = new VerticalLayout();
        PseudoAdaptiveGridLayout layout = new PseudoAdaptiveGridLayout();

        // generate values
        List<Component> children = generateComponents(6);
        layout.add(children);

        // making sure they stored properly
        assertThat(layout.getChildren()).containsExactlyElementsOf(children);

        root.add(layout);
    }

    @DisplayName("Удаление элементов")
    @Test
    void testRemoving() {
        VerticalLayout root = new VerticalLayout();
        PseudoAdaptiveGridLayout layout = new PseudoAdaptiveGridLayout();

        List<Component> children = generateComponents(5);
        // [0,1,2,3,4]
        layout.add(children);

        // removing arbitrary elements: [0,x,x,3,4] -> [0,3,4]
        assertThatCode(()->layout.remove(children.subList(1,2))).doesNotThrowAnyException();

        List<Component> expected = children.subList(0,1);
        expected.addAll(children.subList(2,5));
        // Asserting that component's children are equal to simulated list: [0,3,4] == [0,3,4]
        assertThat(layout.getChildren()).containsExactlyElementsOf(expected);

        root.add(layout);
    }

    /* Found no way to test layout under-the-hood elements arrangement (Horizontal-/VerticalLayouts)
     * without creating mock layout and exposing it for navigation by KaribuTesting
     * which is unwanted, as this will pollute navigation tree and will be visible in final build */
    List<Component> generateComponents(int count) {
        List<Component> componentList = new ArrayList<>(count);
        for(int i = 1; i <= count; i++) {
            componentList.add(new Span(String.valueOf(count)));
        }
        return componentList;
    }
}