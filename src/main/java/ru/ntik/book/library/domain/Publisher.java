package ru.ntik.book.library.domain;

import jakarta.persistence.Table;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static ru.ntik.book.library.domain.Publisher.GRAPH_FETCH_ALL;
import static ru.ntik.book.library.util.Constants.PUBLISHER_REGION_NAME;

@Entity
@Table(uniqueConstraints =
    @UniqueConstraint(name = "uniq_publisher_name", columnNames = "name"))

@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE,
        region = PUBLISHER_REGION_NAME)
@OptimisticLocking(type = OptimisticLockType.DIRTY)
@DynamicUpdate
@NamedEntityGraph(name = GRAPH_FETCH_ALL, includeAllAttributes = true)

@NoArgsConstructor(access = AccessLevel.PROTECTED)

public class Publisher extends PersistentObject {

    @OneToMany(mappedBy = "publisher", fetch = FetchType.LAZY)
    private final Set<BookDefinition> bookDefinitions = new HashSet<>();

    public Publisher(String name, String description, Long creator) {
        super(name, description, creator);
    }

    public Set<BookDefinition> getBookDefinitions() {
        return Collections.unmodifiableSet(bookDefinitions);
    }

    public static final String GRAPH_FETCH_ALL = "Publisher.FETCH_ALL";

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Publisher that = (Publisher) o;

        return getName().equals(that.getName());
    }

    @Override
    public int hashCode() {
        return getName().hashCode();
    }
}