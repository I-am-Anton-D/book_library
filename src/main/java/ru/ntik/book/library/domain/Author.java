package ru.ntik.book.library.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static ru.ntik.book.library.domain.Author.GRAPH_FETCH_ALL;
import static ru.ntik.book.library.util.Constants.AUTHOR_REGION_NAME;

@Entity

@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE,
        region = AUTHOR_REGION_NAME)

@OptimisticLocking(type = OptimisticLockType.DIRTY)
@DynamicUpdate
@NamedEntityGraph(name = GRAPH_FETCH_ALL, includeAllAttributes = true)

@NoArgsConstructor(access = AccessLevel.PROTECTED)

public class Author extends PersistentObject{

    @ManyToMany(mappedBy = "authors", fetch = FetchType.LAZY)
    private Set<BookDefinition> bookDefinitions = new HashSet<>();

    public Set<BookDefinition> getBookDefinitions() {
        return Collections.unmodifiableSet(bookDefinitions);
    }

    public static final String GRAPH_FETCH_ALL = "Author.FETCH_ALL";
}
