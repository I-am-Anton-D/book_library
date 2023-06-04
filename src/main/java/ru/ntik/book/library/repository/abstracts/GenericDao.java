package ru.ntik.book.library.repository.abstracts;

import java.util.List;

public interface GenericDao<T> {
    T findById(long id);
    List<T> findAll();
    T save(T entity);
    void delete(T entity);
}