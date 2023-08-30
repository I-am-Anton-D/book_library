package ru.ntik.book.library.service;

import com.vaadin.flow.data.provider.hierarchy.TreeData;
import com.vaadin.flow.function.ValueProvider;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import org.springframework.transaction.annotation.Transactional;
import ru.ntik.book.library.domain.Category;
import ru.ntik.book.library.repository.CategoryRepository;

import java.util.List;


@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;
    /**
     * If no root category exists, method creates it and returns true,<br>
     * otherwise method does nothing and returns false.
     */
    @PostConstruct
    public boolean tryCreateRootCategory() {
        if(this.categoryRepository.findRoot() == null) {
            this.categoryRepository.save(Category.createRootCategory("Все",
                    "Список всех доступных книг", 0L));
            return true;
        }
        return false;
    }

    public CategoryService(CategoryRepository categoryRepository) { this.categoryRepository = categoryRepository;}

    @Transactional
    public List<Category> fetchAll() {
        return categoryRepository.findAll();
    }

    @Transactional
    public List<Category> fetchChildren(Category parent) {
        return categoryRepository.fetchChildren(parent);
    }

    /**@param id for desired category
     * @return Category object or null */
    public Category findById(long id) { return categoryRepository.findById(id).orElse(null); }
    public Category findRoot() { return categoryRepository.findRoot(); }
    public void save(Category category) {
        this.categoryRepository.save(category);
    }

    public void remove(Category category) {
        this.categoryRepository.delete(category);
    }

    /**
     * @return TreeData &lt Category &gt, containing all categories present in repository
     */
    @Transactional
    public TreeData<Category> fetchCategoriesAsTreeData() {
        List<Category> allCategories = this.fetchAll();
        // getting first children of root
        List<Category> rootCategories = allCategories.stream().
                filter(cat -> cat.getParent() != null && cat.getParent().getParent() == null).toList();
        TreeData<Category> treeData = new TreeData<>();
        treeData.addRootItems(rootCategories);
        addChildrenRecursively(treeData, rootCategories, parent-> findChildrenInCollection(allCategories, parent));
        return treeData;
    }

    @Transactional
    public boolean isEmpty(Category category) {
        return categoryRepository.fetchById(category.getId()).orElseThrow().getChildren().isEmpty();
    }
    public int countBooksWithCategory(Category category) {
        return categoryRepository.fetchById(category.getId()).orElseThrow().getBooks().size();
    }

    private List<Category> findChildrenInCollection(List<Category> categories, Category parent) {
        return categories.stream().filter(cat -> cat.getParent() == parent).toList();
    }

    private void addChildrenRecursively(TreeData<Category> treeData, List<Category> categories, ValueProvider<Category, List<Category>> childProvider) {
        categories.forEach(category ->
        {
            List<Category> chidren = childProvider.apply(category);
            treeData.addItems(category, chidren);
            if (!chidren.isEmpty()) {
                addChildrenRecursively(treeData, chidren, childProvider);
            }
        });
    }
}
