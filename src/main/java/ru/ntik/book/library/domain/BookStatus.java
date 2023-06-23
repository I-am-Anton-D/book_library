package ru.ntik.book.library.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.OptimisticLockType;
import org.hibernate.annotations.OptimisticLocking;
import org.hibernate.envers.Audited;
import ru.ntik.book.library.domain.enums.BookState;

import java.time.LocalDate;

@Entity

@Audited

@OptimisticLocking(type = OptimisticLockType.DIRTY)
@DynamicUpdate

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BookStatus extends StoredObject{

    @Enumerated(EnumType.STRING)
    @Column(name = "state", length = 20)
    private BookState state;

    private LocalDate movedToUserDate;

    private Long toUser;

    private LocalDate movedToOwnerDate;

    BookStatus(Long creator) {
        super(creator);
        this.state = BookState.ON_OWNER;
        this.movedToUserDate = null;
        this.toUser = null;
        this.movedToOwnerDate = null;
    }

    public void moveToUser(Long user) {
        state = BookState.ON_USER;
        movedToUserDate = LocalDate.now();
        toUser = user;
        movedToOwnerDate = null;
    }

    public void moveToOwner() {
        state = BookState.ON_OWNER;
        movedToUserDate = null;
        toUser = null;
        movedToOwnerDate = LocalDate.now();
    }
}
