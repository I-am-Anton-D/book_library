package ru.ntik.book.library.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;
import ru.ntik.book.library.domain.enums.FileType;
import ru.ntik.book.library.util.Checker;

import java.util.Objects;

import static ru.ntik.book.library.util.Constants.DEFAULT_VARCHAR_LENGTH;

@Entity
@Immutable

@Getter
@NoArgsConstructor
public class BookFile extends NamedObject {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FileType type;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private String fileName;

    public BookFile(String name, String description, Long creator,
                    FileType type, String location, String fileName) {
        super(name, description, creator);
        Objects.requireNonNull(type);
        Objects.requireNonNull(location);
        Objects.requireNonNull(fileName);


        this.type = type;
        this.location = Checker.checkStringLength(location, 1,  DEFAULT_VARCHAR_LENGTH);
        this.fileName = Checker.checkStringLength(fileName, 1,  DEFAULT_VARCHAR_LENGTH);
    }
}
