package ru.ntik.book.library.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.ntik.book.library.domain.Publisher;

@Repository
public interface PublisherRepository extends CrudRepository<Publisher, Long> {}