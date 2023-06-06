package ru.ntik.book.library.domain;

import jakarta.persistence.ForeignKey;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.*;
import ru.ntik.book.library.domain.enums.BookLanguage;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "publisher_id", foreignKey = @ForeignKey(name = "pub_id_key"))
    private Publisher publisher;


    public BookDefinition(String name, String description, Long creator, Integer releaseYear,
                          String coverType, String isbn, Integer pageCount, BookLanguage language) {
        super(name, description, creator);

        printInfo = new PrintInfo(releaseYear, coverType, isbn, pageCount, language);
    }

    public void setPublisher(Publisher publisher) {
        this.publisher = publisher;
    }
}
