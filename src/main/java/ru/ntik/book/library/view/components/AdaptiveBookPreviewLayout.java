package ru.ntik.book.library.view.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Component that allows simulating responsive grid layout by allowing externally changing column count */
public class AdaptiveBookPreviewLayout extends VerticalLayout {
    private final List<Component> componentList = new ArrayList<>();
    @Getter
    private int columnCount = 5;

    @Getter
    private boolean autoUpdate = false;

    private int widthAvailable = 1024;

    public void setEnableAutoUpdate(boolean autoUpdate) {
        this.autoUpdate = autoUpdate;
        if (autoUpdate) {
            updateWidth(widthAvailable);
        }
    }

    /**
     * Manually specify column count
     * WARNING: will not work correctly if manualMode is false
     * @param count
     */
    private void setColumnCount(int count) {
        columnCount = count;
        updateLayout();
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

    /**
     * Try automatically adapt to available size
     * @param width in pixels
     */
    public void updateWidth(int width) {
        if (width < 0) {
            throw new IllegalArgumentException("Component width can't be < 0.");
        }
        widthAvailable = width;
        // TODO: generalize, based on component width and padding
        if (autoUpdate) {
            int columns = 1;

            if (width >= 1200) {
                columns = 5;
            } else if (width >= 1000) {
                columns = 4;
            } else if (width >= 750) {
                columns = 3;
            } else if (width >= 550) {
                columns = 2;
            }

            if (columns != columnCount) {
                setColumnCount(columns);
            }
        }
    }

    private void updateLayout() {
        super.removeAll();
        int elementsInRow = 0;
        HorizontalLayout currentLayout = new HorizontalLayout();
        for (Component component : componentList) {
            if(elementsInRow >= columnCount) {
                super.add(currentLayout);
                currentLayout = new HorizontalLayout();
                elementsInRow = 0;
            }
            currentLayout.add(component);
            elementsInRow++;
        }
        super.add(currentLayout);
    }
}
