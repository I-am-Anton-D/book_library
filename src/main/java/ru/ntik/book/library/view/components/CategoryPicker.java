package ru.ntik.book.library.view.components;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.data.provider.hierarchy.TreeData;
import com.vaadin.flow.data.provider.hierarchy.TreeDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import ru.ntik.book.library.domain.Category;
import ru.ntik.book.library.service.CategoryService;
import ru.ntik.book.library.util.ObjectActionListener;

import java.util.ArrayList;
import java.util.List;

public class CategoryPicker extends VerticalLayout {
    private final TreeGrid<Category> categoryTree = new TreeGrid<>();
    private final List<ObjectActionListener<Category>> selectionListeners = new ArrayList<>();
    private final List<ObjectActionListener<Category>> editButtonListeners = new ArrayList<>();
    private final CategoryService categoryService;

    public CategoryPicker(CategoryService categoryService) {
        this(categoryService, false);
    }

    public CategoryPicker(CategoryService categoryService, boolean hasEditRow) {
        this.categoryService = categoryService;
        categoryTree.addHierarchyColumn(Category::getName).setHeader("Категории").setKey("categories");

        TreeData<Category> treeData = categoryService.fetchCategoriesAsTreeData();
        categoryTree.setDataProvider(new TreeDataProvider<>(treeData));

        categoryTree.addSelectionListener(e->{
            for(ObjectActionListener<Category> listener : selectionListeners) {
                listener.onPerformed(categoryTree.getSelectedItems().stream().findFirst().orElse(null));
            }
        });

        if(hasEditRow) {
            categoryTree.addColumn(new ComponentRenderer<>(this::generateEditButton));
        }

        add(categoryTree);
    }

    public void update() {
        TreeData<Category> treeData = categoryService.fetchCategoriesAsTreeData();
        categoryTree.setTreeData(treeData);
        categoryTree.getDataProvider().refreshAll();
    }

    public void addSelectionListener(ObjectActionListener<Category> listener) {
        selectionListeners.add(listener);
    }
    public void addEditButtonListener(ObjectActionListener<Category> listener) {
        editButtonListeners.add(listener);
    }

    private Button generateEditButton(Category category) {
        return new Button(new Icon("lumo", "menu"),
                e->editButtonListeners.forEach(listener -> listener.onPerformed(category))
        );
    }

    private List<Category> getCategoryTreeBranch(List<Category> categories) {
        List<Category> children = new ArrayList<>(List.of());
        for(Category category : categories) {
            children.addAll(categoryTree.getTreeData().getChildren(category));
        }
        List<Category> result = new ArrayList<>(categories);
        if(!children.isEmpty()) {
            result.addAll(getCategoryTreeBranch(children));
        }
        return result;
    }

    /**
     * @param category Parent category
     * @Returns all subcategories of given category <b>and provided category</b><br>
     * If category is not present in CategoryPicker (and as such, most likely in Repository)
     * then empty list is returned.
     * If category has no children only itself is returned;
     */
    public List<Category> getCategoryWithChildren(Category category) {
        List<Category> categories = new ArrayList<>(List.of(category));
        categories.addAll(getCategoryTreeBranch(List.of(category)));
        return categories;
    }
}
