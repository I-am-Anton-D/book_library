package ru.ntik.book.library.view.admin;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.data.provider.hierarchy.TreeData;
import com.vaadin.flow.data.provider.hierarchy.TreeDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.ntik.book.library.domain.Category;
import ru.ntik.book.library.service.CategoryService;
import ru.ntik.book.library.view.MainLayout;

@Route("categories")
@SpringComponent
@UIScope
public class CategoryEditLayout extends VerticalLayout {
    private final CategoryService categoryService;

    // UI components
    private final TreeGrid<Category> categoryTree = new TreeGrid<>();
    private final Dialog editDialog = new Dialog();
    private final Button navigateBackButton = new Button("< На главную");
    @Autowired
    CategoryEditLayout(CategoryService categoryService) {
        navigateBackButton.addClickListener(e->UI.getCurrent().navigate(MainLayout.class));
        add(navigateBackButton);

        this.categoryService = categoryService;

        categoryTree.addHierarchyColumn(Category::getName).setHeader("Категории").
                setTooltipGenerator(Category::getDescription).setKey("title");
        categoryTree.addColumn(categoryService::countBooksWithCategory).setHeader("Книг в категории").setKey("count");
        categoryTree.addColumn(new ComponentRenderer<>(HorizontalLayout::new, this::generateButtons)).setKey("buttons");

        TreeData<Category> treeData = categoryService.fetchCategoriesAsTreeData();
        categoryTree.setDataProvider(new TreeDataProvider<>(treeData));
        categoryTree.setId("category-edit-tree");
        add(categoryTree);

        // add subcategory to root button
        Button addButton = new Button(new Icon("lumo","plus"));
        Category root = categoryService.findRoot();
        addButton.addClickListener(e->this.openAddDialog(root));
        add(addButton);
    }

    @Transactional
    public void delete(Category category) {
        if (!categoryService.isEmpty(category)) {
            Notification.show("Невозможно удалить. Категория не пуста.").addThemeVariants(NotificationVariant.LUMO_ERROR);
        } else {
            categoryService.remove(category);
            refreshPage();
        }
    }

    private void openEditDialog(Category category) {
        CategoryEditForm editView = new CategoryEditForm(this.categoryService, category, category.getParent());
        editView.addOnSaveListener(cat -> {
            categoryService.save(cat);
            editDialog.close();
            UI.getCurrent().getPage().reload();
        });
        editView.addOnCloseListener(cat->editDialog.close());

        editDialog.removeAll();
        editDialog.add(editView);
        editDialog.setHeaderTitle("Изменить категорию " + "\"" + category.getName() + "\"");
        add(editDialog);
        editDialog.open();
    }

    private void refreshPage() {
        UI.getCurrent().getPage().reload();
    }

    private void openAddDialog(Category parent) {
        CategoryEditForm addView = new CategoryEditForm(this.categoryService, null, parent);
        addView.addOnSaveListener(cat -> {
            categoryService.save(cat);
            Notification.show("Сохранено").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            editDialog.close();
            refreshPage();
        });
        addView.addOnCloseListener(cat->editDialog.close());

        editDialog.removeAll();
        editDialog.add(addView);
        if(parent.getParent() == null)
        {
            editDialog.setHeaderTitle("Создать новую корневую категорию");
        } else {
            editDialog.setHeaderTitle("Создать под-категорию " + "\"" + parent.getName() + "\"");
        }
        add(editDialog);
        editDialog.open();
    }

    private void generateButtons(HorizontalLayout layout, Category category) {
        Button addButton = new Button(new Icon("lumo","plus"));
        addButton.addClassName("category-add-button");
        addButton.addClickListener(e->this.openAddDialog(category));
        addButton.setTooltipText("Добавить под-категорию");
        layout.add(addButton);

        Button editButton = new Button(new Icon("lumo","edit"));
        editButton.addClassName("category-edit-button");
        editButton.addClickListener(e->this.openEditDialog(category));
        editButton.setTooltipText("Изменить категорию");
        layout.add(editButton);

        Button removeButton = new Button(new Icon("lumo","cross"));
        removeButton.addClassName("category-remove-button");
        removeButton.addClickListener(e->{
            if(e.isShiftKey()) {
                delete(category);
            } else {
                openDeleteDialog(category);
            }
        });
        removeButton.setTooltipText("Удалить категорию");
        layout.add(removeButton);
    }

    private void openDeleteDialog(Category category) {
        Dialog deleteDialog = new Dialog();
        deleteDialog.setHeaderTitle("Удалить категорию \"" + category.getName() + "\" ?");

        Button deleteButton = new Button("Удалить", buttonClickEvent -> delete(category));
        deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        deleteDialog.add(deleteButton);

        deleteDialog.add(new Button("Отмена", buttonClickEvent -> deleteDialog.close()));
        deleteDialog.open();
    }
}
