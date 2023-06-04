package ru.ntik.book.library.repository.abstracts;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceContextType;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public class AbstractDao<T> implements GenericDao<T> {
    @PersistenceContext(type = PersistenceContextType.EXTENDED)
    protected EntityManager em;

    private Class<T> entityClass;

    public void setEntityClass(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    @Override
    @Transactional(readOnly = true)
    public T findById(long id) {
        return em.find(entityClass, id);
    }

    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public List<T> findAll() {
        return em.createQuery("from " + entityClass.getName()).getResultList();
    }

    @Override
    @Transactional
    public T save(T entity) {
        return em.merge(entity);
    }

    @Override
    @Transactional
    public void delete(T entity) {
        em.remove(entity);
    }
}