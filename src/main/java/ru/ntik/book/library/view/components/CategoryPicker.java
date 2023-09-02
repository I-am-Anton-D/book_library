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
    public CategoryPicker(CategoryService categoryService, boolean isMultiselec) {
        this.categoryService = categoryService;
        categoryTree.addHierarchyColumn(Category::getName).setHeader("Категории").setKey("categories");

        TreeData<Category> treeData = categoryService.fetchCategoriesAsTreeData();
        categoryTree.setDataProvider(new TreeDataProvider<>(treeData));
        if (isMultiselec)
        {
            categoryTree.setSelectionMode(Grid.SelectionMode.MULTI);
        }

        categoryTree.addSelectionListener(e->{
            for(ObjectActionListener<List<Category>> listener : selectionListeners) {
                listener.onPerformed(categoryTree.getSelectedItems().stream().toList());
            }
        });
        add(categoryTree);
    }

    public void update() {
        // TODO: think how to update treeData without re-creating TreeDataProvider
        TreeData<Category> treeData = categoryService.fetchCategoriesAsTreeData();
        categoryTree.setDataProvider(new TreeDataProvider<>(treeData));
    }
    public void addSelectionListener(ObjectActionListener<List<Category>> listener) {
        selectionListeners.add(listener);
    }
}
