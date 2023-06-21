package ru.ntik.book.library.domain;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Immutable;

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

    @ManyToOne
    @JoinColumn(name = "book_definition_id")
    private BookDefinition bookDefinition;

    public BookInstance(Long owner, boolean isCompany, Long creator) {
        super(creator);
        this.owner = owner;
        this.isCompany = isCompany;
    }
}
