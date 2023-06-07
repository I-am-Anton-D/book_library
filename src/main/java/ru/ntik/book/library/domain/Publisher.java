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
    private final Set<BookDefinition> bookDefinitions = new HashSet<>();

    public Publisher(String name, String description, Long creator) {
        super(name, description, creator);
    }

    public Set<BookDefinition> getBookDefinitions() {
        return Collections.unmodifiableSet(bookDefinitions);
    }
}