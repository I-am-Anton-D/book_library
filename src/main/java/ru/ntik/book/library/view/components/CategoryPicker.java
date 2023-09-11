package ru.ntik.book.library.view.components;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.data.provider.hierarchy.TreeData;
import com.vaadin.flow.data.provider.hierarchy.TreeDataProvider;
import ru.ntik.book.library.domain.Category;
import ru.ntik.book.library.service.CategoryService;
import ru.ntik.book.library.util.ObjectActionListener;

import java.util.ArrayList;
import java.util.List;

public class CategoryPicker extends VerticalLayout {
    private final TreeGrid<Category> categoryTree = new TreeGrid<>();
    private final List<ObjectActionListener<List<Category>>> selectionListeners = new ArrayList<>();
    private final CategoryService categoryService;

    List<Category> previousSelection = new ArrayList<>();
    public CategoryPicker(CategoryService categoryService, boolean isMultiselec) {
        this.categoryService = categoryService;
        categoryTree.addHierarchyColumn(Category::getName).setHeader("Категории").setKey("categories");

        TreeData<Category> treeData = categoryService.fetchCategoriesAsTreeData();
        categoryTree.setDataProvider(new TreeDataProvider<>(treeData));
        if (isMultiselec)
        {
            categoryTree.setSelectionMode(Grid.SelectionMode.MULTI);
            addSelectionListener(this::recursiveMultiselect);
        }

        categoryTree.addSelectionListener(e->{
            for(ObjectActionListener<List<Category>> listener : selectionListeners) {
                listener.onPerformed(categoryTree.getSelectedItems().stream().toList());
            }
        });

        add(categoryTree);
    }

    /**
     * Recursively expands and selects categories
     * @param category
     * @param isSelected
     */
    private void recursiveSelecteStep(Category category, boolean isSelected) {
        if(isSelected) {
            categoryTree.select(category);
            categoryTree.expand(category);
        } else {
            categoryTree.deselect(category);
        }
        categoryTree.getTreeData().getChildren(category).
                forEach(child-> recursiveSelecteStep(child, isSelected));
    }

    /**
     * Finds which elements were selected and deselected and
     * recursively (de-)selects their children
     * @param selection list of selected categories
     */
    private void recursiveMultiselect(List<Category> selection) {
        List<Category> added = selection.stream().filter(el->!previousSelection.contains(el)).toList();
        List<Category> removed = previousSelection.stream().filter(el->!selection.contains(el)).toList();

        /* There is double-selection of selected of elements, but it should not affect overall behaviour.
            Unless someone hacks into the component and subscribes to listener of TreeGrid directly.
            But in that case it's their problem */
        added.forEach(category->recursiveSelecteStep(category, true));
        removed.forEach(category->recursiveSelecteStep(category, false));
        previousSelection = selection;
    }

    public void update() {
        TreeData<Category> treeData = categoryService.fetchCategoriesAsTreeData();
        categoryTree.setTreeData(treeData);
        categoryTree.getDataProvider().refreshAll();
    }
    public void addSelectionListener(ObjectActionListener<List<Category>> listener) {
        selectionListeners.add(listener);
    }
}
