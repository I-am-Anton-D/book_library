package ru.ntik.book.library.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.*;
import ru.ntik.book.library.domain.enums.BookState;

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
    private BookDefinition bookDefinition;

    @OneToOne(cascade = CascadeType.ALL, optional = false, orphanRemoval = true, fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(nullable = false)
    private BookStatus status;

    public BookInstance(Long owner, Long creator, boolean isCompany, BookDefinition bookDefinition) {
        super(creator);

        Objects.requireNonNull(bookDefinition);
        this.owner = owner;
        this.isCompany = isCompany;
        this.bookDefinition = bookDefinition;
        this.status = new BookStatus(creator);
    }

    public void moveToUser(Long toUser) {
        Objects.requireNonNull(toUser);

        if (status.getState() == BookState.ON_OWNER) {
            bookDefinition.getInstancesInfo().decrementFreeCount();
        }

        if (status.getState() == BookState.ON_USER && toUser.equals(status.getToUser()))  {
            throw new IllegalStateException("Attempt to transfer to the same user");
        }

        status.moveToUser(toUser);
    }

    public void moveToOwner() {
        bookDefinition.getInstancesInfo().incrementFreeCount();
        status.moveToOwner();
    }
}
