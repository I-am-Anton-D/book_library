package ru.ntik.book.library.domain;

import jakarta.persistence.OrderBy;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.*;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

import static ru.ntik.book.library.domain.Category.GRAPH_FETCH_ALL;
import static ru.ntik.book.library.util.Constants.CATEGORY_REGION_NAME;


@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE,
        region = CATEGORY_REGION_NAME)
@NamedEntityGraph(name = GRAPH_FETCH_ALL, includeAllAttributes = true)

@OptimisticLocking(type = OptimisticLockType.DIRTY)
@DynamicUpdate

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category extends NamedObject {
    @ManyToOne(fetch = FetchType.LAZY)
    private Category parent;

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    @OrderBy("name")
    private final Set<Category> children = new LinkedHashSet<>();

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    @OrderBy("name")
    private Set<BookDefinition> books = new LinkedHashSet<>();

    public Category(String name, String description, Long creator, Category parent) {
        super(name, description, creator);

        Objects.requireNonNull(parent);
        this.parent = parent;
    }

    public Set<Category> getChildren() {
        return Collections.unmodifiableSet(children);
    }

    public Set<BookDefinition> getBooks() {
        return Collections.unmodifiableSet(books);
    }

    @PreRemove
    void preRemove() {
        if (!children.isEmpty()) throw
                new IllegalStateException("Can not delete category. Category has a children. Delete child categories first");
        if (!books.isEmpty()) throw
                new IllegalStateException("Can not delete category. Category has a books. Move books to another category first");
    }

    public static final String GRAPH_FETCH_ALL = "Category.GRAPH_ALL";
}
