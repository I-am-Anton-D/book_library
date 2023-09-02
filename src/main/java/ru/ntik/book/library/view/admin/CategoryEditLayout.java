package ru.ntik.book.library.view.admin;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.beans.factory.annotation.Autowired;
import ru.ntik.book.library.domain.Category;
import ru.ntik.book.library.service.CategoryService;
import ru.ntik.book.library.view.MainLayout;
import ru.ntik.book.library.view.components.CategoryPicker;

@Route("categories")
@SpringComponent
@UIScope
public class CategoryEditLayout extends VerticalLayout {
    private final CategoryService categoryService;

    // UI components
    private final CategoryPicker categoryPicker;
    private final Dialog categoryDialog = new Dialog();
    private final Button navigateBackButton = new Button("< На главную");
    @Autowired
    CategoryEditLayout(CategoryService categoryService) {
        navigateBackButton.addClickListener(e->UI.getCurrent().navigate(MainLayout.class));

        this.categoryService = categoryService;

        categoryPicker = new CategoryPicker(categoryService, false);
        categoryPicker.addSelectionListener(categories->{
            if(!categories.isEmpty()) {
                Category category = categories.get(0);
                openEditDialog(category);
            }
        });

        // add subcategory to root button
        Button addButton = new Button(new Icon("lumo","plus"));
        Category root = categoryService.findRoot();
        addButton.addClickListener(e->this.openAddDialog(root));

        // UI
        add(navigateBackButton, categoryPicker, addButton);
    }

    private void openEditDialog(Category category) {
        CategoryEditForm editForm = getCategoryEditForm(category, null);

        categoryDialog.setId("category-edit-dialog");
        categoryDialog.removeAll();
        categoryDialog.add(editForm);
        categoryDialog.setHeaderTitle("Изменить категорию " + "\"" + category.getName() + "\"");
        add(categoryDialog);
        categoryDialog.open();
    }

    private void openAddDialog(Category parent) {
        CategoryEditForm addForm = getCategoryEditForm(null, parent);

        categoryDialog.setId("category-add-dialog");
        categoryDialog.removeAll();
        categoryDialog.add(addForm);
        if(parent.getParent() == null)
        {
            categoryDialog.setHeaderTitle("Создать новую корневую категорию");
        } else {
            categoryDialog.setHeaderTitle("Создать под-категорию " + "\"" + parent.getName() + "\"");
        }
        add(categoryDialog);
        categoryDialog.open();
    }

    private CategoryEditForm getCategoryEditForm(Category category, Category parent) {
        CategoryEditForm editForm = new CategoryEditForm(this.categoryService, category, parent);
        editForm.addOnSaveListener(cat -> {
            categoryPicker.update();
            categoryDialog.close();
        });
        editForm.addOnDeleteListener(cat->{
            categoryPicker.update();
            categoryDialog.close();
        });
        editForm.addOnCloseListener(cat->categoryDialog.close());
        return editForm;
    }
}
