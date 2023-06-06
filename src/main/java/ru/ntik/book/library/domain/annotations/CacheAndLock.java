package ru.ntik.book.library.domain.annotations;

import jakarta.persistence.Cacheable;
import org.hibernate.annotations.*;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static ru.ntik.book.library.util.Constants.BOOK_DEFINITION_REGION_NAME;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE,
        region = BOOK_DEFINITION_REGION_NAME)

@OptimisticLocking(type = OptimisticLockType.DIRTY)
@DynamicUpdate
public @interface CacheAndLock {

}
