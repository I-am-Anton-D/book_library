package ru.ntik.book.library.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.util.*;

import static ru.ntik.book.library.util.Constants.CATEGORY_REGION_NAME;


@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE,
        region = CATEGORY_REGION_NAME)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category extends NamedObject{
    @ManyToOne(fetch = FetchType.LAZY)
    private Category parent;

    @OneToMany(mappedBy = "parent")
    @OrderBy("name" )
    private final List<Category> children = new ArrayList<>();

    @OneToMany(mappedBy = "category")
    private Set<BookDefinition> books = new LinkedHashSet<>();

    public Category(String name, String description, Long creator, Category parent) {
        super(name, description, creator);

        Objects.requireNonNull(parent);
        this.parent = parent;
    }

    @PreRemove
    void preRemove() {
        if (!children.isEmpty()) throw
                new IllegalStateException("Can not delete category.Category has children. Delete child categories first");
        if (!books.isEmpty()) throw
                new IllegalStateException("Can not delete category.Category has books. Move books to another category first");
    }

    public static final String COLUMN_PARENT_NAME = "parent_id";
    public static final String COLUMN_PARENT_DEFINITION = "SMALLINT CHECK(" + COLUMN_PARENT_NAME + " >= 0)";
}
