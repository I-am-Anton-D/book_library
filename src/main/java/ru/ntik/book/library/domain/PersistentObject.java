package ru.ntik.book.library.domain;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import ru.ntik.book.library.util.Checker;

import java.time.Instant;
import java.util.Objects;

import static ru.ntik.book.library.util.Constants.*;

@MappedSuperclass
@Getter

abstract class PersistentObject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = COLUMN_NAME, columnDefinition = COLUMN_NAME_DEFINITION)
    private String name;

    @Column(name = COLUMN_DESCRIPTION_NAME, columnDefinition = COLUMN_DESCRIPTION_DEFINITION)
    private String description;

    @Column(nullable = false, updatable = false)
    private Long creator;

    @Column(name = COLUMN_CREATED_NAME, columnDefinition = COLUMN_CREATED_DEFINITION, updatable = false)
    @CreationTimestamp
    private Instant created;

    @Version
    private short version;

    protected PersistentObject() {
    }

    protected PersistentObject(String name, String description, Long creator) {
        setName(name);
        setDescription(description);
        setCreator(creator);
    }

    private void setCreator(Long creator) {
        Objects.requireNonNull(creator);
        this.creator = creator;
    }

    public void setName(String name) {
        Objects.requireNonNull(name);
        this.name = Checker.checkStringLength(name, PO_MIN_NAME_LENGTH, PO_MAX_NAME_LENGTH);
    }

    public void setDescription(String description) {
        this.description = description == null ? null : Checker.checkStringLength(description, PO_MIN_DESC_LENGTH, PO_MAX_DESC_LENGTH);
    }

    public boolean deepEquals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PersistentObject that)) return false;

        return Objects.equals(id, that.id) && Objects.equals(name, that.name)
                && Objects.equals(description, that.description);
    }

    @Override
    public String toString() {
        return String.format("%s { id=%d, name='%s'}", getClass().getSimpleName(), getId(), getName());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PersistentObject that)) return false;

        return getId() != null && that.getId() != null
                && Objects.equals(getId(), that.getId());

    }

    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : 0;
    }

    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_DESCRIPTION_NAME = "description";
    private static final String COLUMN_CREATED_NAME = "created";
    private static final String COLUMN_NAME_DEFINITION =
            "VARCHAR(" + PO_MAX_NAME_LENGTH + ") CHECK (length(" + COLUMN_NAME + ") >= " + PO_MIN_NAME_LENGTH + ") NOT NULL";
    private static final String COLUMN_DESCRIPTION_DEFINITION =
            "VARCHAR(" + PO_MAX_DESC_LENGTH + ") CHECK (length(" + COLUMN_DESCRIPTION_NAME + ") >= " + PO_MIN_DESC_LENGTH + ")";
    private static final String COLUMN_CREATED_DEFINITION =
            "TIMESTAMP(6) WITHOUT TIME ZONE CHECK(current_timestamp >= " + COLUMN_CREATED_NAME + ") NOT NULL";
}
