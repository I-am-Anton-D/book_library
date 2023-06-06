package ru.ntik.book.library.domain;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static ru.ntik.book.library.util.Constants.PUBLISHER_REGION_NAME;

@Entity

@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE,
        region = PUBLISHER_REGION_NAME)
@OptimisticLocking(type = OptimisticLockType.DIRTY)
@DynamicUpdate

@NoArgsConstructor(access = AccessLevel.PROTECTED)

public class Publisher extends PersistentObject {

    @OneToMany(mappedBy = "publisher", fetch = FetchType.LAZY)
    private Set<BookDefinition> bookDefinitions = new HashSet<>();

    public Publisher(String name, String description, Long creator, Set<BookDefinition> bookDefinitions) {
        super(name, description, creator);

        Objects.requireNonNull(bookDefinitions);
        this.bookDefinitions = bookDefinitions;
    }

    public Publisher(String name, String description, Long creator) {
        super(name, description, creator);
    }

    public Set<BookDefinition> getBookDefinitions() {
        return Collections.unmodifiableSet(bookDefinitions);
    }

    public void setBookDefinitions(Set<BookDefinition> bookDefinitions) {
        Objects.requireNonNull(bookDefinitions);

        this.bookDefinitions.clear();
        this.bookDefinitions.addAll(bookDefinitions);
    }

    public boolean addBookDefinition(BookDefinition bookDefinition) {
        Objects.requireNonNull(bookDefinition);
        return bookDefinitions.add(bookDefinition);
    }

    public boolean removeBookDefinition(BookDefinition bookDefinition) {
        Objects.requireNonNull(bookDefinition);
        return bookDefinitions.remove(bookDefinition);
    }

    public void cleanBookDefinitions() {
        bookDefinitions.clear();
    }
}