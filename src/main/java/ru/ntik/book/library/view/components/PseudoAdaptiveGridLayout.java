package ru.ntik.book.library.view.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Component that simulates responsive grid layout by allowing externally changing column count */
public class PseudoAdaptiveGridLayout extends VerticalLayout {
    private final List<Component> componentList = new ArrayList<>();

    @Getter
    private int columnCount = 3;

    public void setColumnCount(int count) {
        columnCount = count;
        updateLayout();
    }

    private void updateLayout() {
        super.removeAll();
        int elementsInRow = 0;
        HorizontalLayout currentLayout = new HorizontalLayout();
        for (Component component : componentList) {
            if(elementsInRow > columnCount) {
                super.add(currentLayout);
                currentLayout = new HorizontalLayout();
                elementsInRow = 0;
            }
            currentLayout.add(component);
            elementsInRow++;
        }
        super.add(currentLayout);
    }
    @Override
    public void add(Component... components) {
        componentList.addAll(Arrays.asList(components));
        updateLayout();
    }

    @Override
    public void remove(Component... components) {
        componentList.removeAll(Arrays.asList(components));
        updateLayout();
    }

    @Override
    public void removeAll() {
        componentList.clear();
        updateLayout();
    }
}
