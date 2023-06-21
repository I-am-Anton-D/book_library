package ru.ntik.book.library.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Immutable;

import java.util.Objects;

import static ru.ntik.book.library.util.Constants.BOOK_INSTANCE_REGION_NAME;

@Entity
@Immutable

@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE,
        region = BOOK_INSTANCE_REGION_NAME)

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BookInstance extends StoredObject {

    private Long owner;

    private boolean isCompany;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_definition_id")
    private BookDefinition bookDefinition;

    public BookInstance(Long owner, Long creator, boolean isCompany, BookDefinition bookDefinition) {
        super(creator);

        Objects.requireNonNull(bookDefinition);
        this.owner = owner;
        this.isCompany = isCompany;
        this.bookDefinition = bookDefinition;
    }

}
