package ru.ntik.book.library.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.Objects;

import static ru.ntik.book.library.util.Constants.ID_GENERATOR;

@MappedSuperclass
@SequenceGenerator(name = ID_GENERATOR, sequenceName = "po_seq", initialValue = 50)

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class StoredObject {

    @Id
    @GeneratedValue(generator = ID_GENERATOR)
    private Long id;

    @Column(nullable = false, updatable = false)
    private Long creator;

    @Column(name = COLUMN_CREATED_NAME, columnDefinition = COLUMN_CREATED_DEFINITION, updatable = false)
    @CreationTimestamp
    private Instant created;

    protected StoredObject(Long creator) {
        setCreator(creator);
    }

    private void setCreator(Long creator) {
        Objects.requireNonNull(creator);
        this.creator = creator;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StoredObject that)) return false;

        return id != null && that.getId() != null
                && Objects.equals(id, that.getId());
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    private static final String COLUMN_CREATED_NAME = "created";
    private static final String COLUMN_CREATED_DEFINITION ="TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL";

}
