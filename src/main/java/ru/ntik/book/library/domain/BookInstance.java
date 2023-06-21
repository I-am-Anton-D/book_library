package ru.ntik.book.library.domain;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import static ru.ntik.book.library.util.Constants.BOOK_INSTANCE_REGION_NAME;

@Entity
@Getter

@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE,
        region = BOOK_INSTANCE_REGION_NAME)

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BookInstance extends StoredObject {




}
