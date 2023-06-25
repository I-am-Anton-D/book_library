package ru.ntik.book.library.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;
import ru.ntik.book.library.util.Checker;

import java.util.Objects;

import static ru.ntik.book.library.util.Constants.DEFAULT_VARCHAR_LENGTH;

@Entity
@Immutable

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BookImage extends StoredObject{

    @Column(nullable = false)
    private String fileName;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(nullable = false)
    private byte[] bytes;

    private boolean mainImage = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_definition_id")
    private BookDefinition bookDefinition;

    public BookImage(Long creator, String fileName, byte[] bytes, boolean mainImage, BookDefinition bd) {
        super(creator);
        Objects.requireNonNull(fileName);
        Objects.requireNonNull(bytes);
        Objects.requireNonNull(bd);

        //Pattern
        this.fileName = Checker.checkStringLength(fileName, 1,  DEFAULT_VARCHAR_LENGTH);
        this.bytes = bytes;
        this.mainImage = mainImage;
        this.bookDefinition = bd;
    }
}
