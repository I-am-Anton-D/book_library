package ru.ntik.book.library.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.ntik.book.library.domain.Category;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query("SELECT c FROM Category c WHERE c.parent IS NULL")
    Category findRoot();

    List<Category> findAll();

    @EntityGraph(value = Category.GRAPH_FETCH_ALL)
    @Query("SELECT c FROM Category c WHERE c.parent IS NULL")
    Category fetchRoot();

    @EntityGraph(value = Category.GRAPH_FETCH_ALL)
    @Query("SELECT c FROM Category c WHERE c.id = :id")
    Optional<Category> fetchById(@Param("id") Long id);

    @Query(value = """
        WITH RECURSIVE category_link(id, name, parent_id, path, level) AS (
            SELECT id, name, parent_id, '/' || name, 0
            FROM category WHERE parent_id IS NULL
            UNION ALL
            SELECT c.id, c.name, c.parent_id, cl.path || '/' || c.name, cl.level + 1
            FROM category_link cl
            JOIN category c ON cl.id = c.parent_id)
        SELECT id, name AS cat_name, parent_id, path, level
        FROM category_link ORDER BY level
        """, nativeQuery = true)
    List<Object[]> fetchCategoryTree();
}
