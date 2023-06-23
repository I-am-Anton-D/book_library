package ru.ntik.book.library.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Immutable;

import static ru.ntik.book.library.util.Constants.BOOK_ORDER_REGION_NAME;

@Entity
@Immutable
@Getter

@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY,
        region = BOOK_ORDER_REGION_NAME)

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BookOrder extends StoredObject {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_definition_id")
    private BookDefinition bookDefinition;

    public BookOrder(Long creator, BookDefinition bookDefinition) {
        super(creator);
        this.bookDefinition = bookDefinition;
    }
}
