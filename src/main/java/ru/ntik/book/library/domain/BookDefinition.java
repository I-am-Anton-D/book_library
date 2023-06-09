package ru.ntik.book.library.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.*;
import ru.ntik.book.library.domain.enums.BookLanguage;

import java.util.ArrayList;
import java.util.Collection;

import static ru.ntik.book.library.util.Constants.BOOK_DEFINITION_REGION_NAME;
import static ru.ntik.book.library.util.Constants.PO_BATCH_SIZE;

@Entity
@BatchSize(size = PO_BATCH_SIZE)

@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE,
        region = BOOK_DEFINITION_REGION_NAME)

@OptimisticLocking(type = OptimisticLockType.DIRTY)
@DynamicUpdate

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)

public class BookDefinition extends PersistentObject {
    @Embedded
    private PrintInfo printInfo;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Publisher publisher;

    @JoinTable(name = "book_to_author")
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private final Collection<Author> authors = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "book_to_book",
            joinColumns = @JoinColumn(name = "book_one", foreignKey =  @ForeignKey(name = "fk_book_one")),
            inverseJoinColumns = @JoinColumn(name = "book_two"), foreignKey =  @ForeignKey(name = "fk_book_two"))
    private final Collection<BookDefinition> links = new ArrayList<>();

    public BookDefinition(String name, String description, Long creator, Integer releaseYear,
                          String coverType, String isbn, Integer pageCount, BookLanguage language) {

        super(name, description, creator);
        printInfo = new PrintInfo(releaseYear, coverType, isbn, pageCount, language);
    }

    public void setPublisher(Publisher publisher) {
        this.publisher = publisher;
    }
}
