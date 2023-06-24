package ru.ntik.book.library.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

@Entity
@Immutable

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BookImage extends StoredObject{

    @Column(nullable = false)
    private String fileName;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    private byte[] bytes;

    private boolean mainImage = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_definition_id")
    private BookDefinition bookDefinition;

    public BookImage(Long creator, String fileName, byte[] bytes, boolean mainImage, BookDefinition bd) {
        super(creator);
        this.fileName = fileName;
        this.bytes = bytes;
        this.mainImage = mainImage;
        this.bookDefinition = bd;
    }
}
