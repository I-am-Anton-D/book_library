package ru.ntik.book.library.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.SequenceGenerator;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import ru.ntik.book.library.util.Checker;

import java.time.Instant;
import java.util.Objects;

import static ru.ntik.book.library.util.Constants.*;

@MappedSuperclass

@Getter
@EqualsAndHashCode(of = {"id", "name", "description"})
@ToString(exclude = "creator")

abstract class PersistentObject {
    //-------------------------
    //TABLE, COLUMN DEFINITIONS
    public static final String SEQ_NAME = "persistent_object_seq";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_DESCRIPTION_NAME = "description";
    public static final String COLUMN_CREATED_NAME = "created";
    public static final String COLUMN_NAME_DEFINITION =
            "VARCHAR(" + PO_MAX_NAME_LENGTH + ") CHECK (length(" + COLUMN_NAME + ") >= " + PO_MIN_NAME_LENGTH + ") NOT NULL";
    public static final String COLUMN_DESCRIPTION_DEFINITION =
            "VARCHAR(" + PO_MAX_DESC_LENGTH + ") CHECK (length(" + COLUMN_DESCRIPTION_NAME + ") >= " + PO_MIN_DESC_LENGTH + ")";
    public static final String COLUMN_CREATED_DEFINITION =
            "TIMESTAMP(6) WITHOUT TIME ZONE CHECK(current_timestamp >= " + COLUMN_CREATED_NAME + ") NOT NULL";
    @Id
    @SequenceGenerator(name = SEQ_NAME, sequenceName = SEQ_NAME, allocationSize = 1)
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

    protected PersistentObject() {
    }

    protected PersistentObject(Long id, String name, String description, Long creator) {
        Objects.requireNonNull(id);
        Objects.requireNonNull(creator);

        this.id = id;
        this.creator = creator;

        setName(name);
        setDescription(description);
    }

    public void setName(String name) {
        Objects.requireNonNull(name);
        this.name = Checker.checkStringLength(name, PO_MIN_NAME_LENGTH, PO_MAX_NAME_LENGTH);
    }

    public void setDescription(String description) {
        this.description = description == null ? null : Checker.checkStringLength(description, PO_MIN_DESC_LENGTH, PO_MAX_DESC_LENGTH);
    }
}
