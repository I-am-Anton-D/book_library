package ru.ntik.book.library.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InstancesInfo {

    @Column(name = COLUMN_INSTANCE_COUNT_NAME, columnDefinition = COLUMN_INSTANCE_COUNT_DEFINITION)
    int instanceCount;

    @Column(name = COLUMN_FREE_COUNT_NAME, columnDefinition = COLUMN_FREE_COUNT_DEFINITION)
    int freeCount;

    void incrementFreeCount() {
        freeCount++;
    }

    void decrementFreeCount() {
        freeCount--;
    }

    public void onAddInstance() {
        instanceCount++;
        freeCount++;

    }

    public void onRemoveInstance(boolean removedFree) {
        if (removedFree) freeCount--;
        instanceCount--;
    }

    private static final String COLUMN_INSTANCE_COUNT_NAME = "instance_count";
    private static final String COLUMN_INSTANCE_COUNT_DEFINITION = "SMALLINT CHECK(" + COLUMN_INSTANCE_COUNT_NAME + " >= 0) NOT NULL ";

    private static final String COLUMN_FREE_COUNT_NAME = "free_count";
    private static final String COLUMN_FREE_COUNT_DEFINITION = "SMALLINT CHECK(" + COLUMN_INSTANCE_COUNT_NAME + " >= 0) NOT NULL ";


}