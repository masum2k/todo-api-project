package com.example.todoapp.repository;

import com.example.todoapp.enums.Priority;
import com.example.todoapp.model.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {
    // ✅ QUERY 1: Sadece ID'leri getir (pagination ile)
    @Query("SELECT DISTINCT t.id FROM Todo t LEFT JOIN t.tags tag_element WHERE " +
            "(:completed IS NULL OR t.completed = :completed) AND " +
            "(:priority IS NULL OR t.priority = :priority) AND " +
            "(:tag IS NULL OR :tag = '' OR tag_element = :tag) AND " +
            "(:overdue IS NULL OR " +
            "    (:overdue = true AND t.deadline IS NOT NULL AND t.deadline < :now) OR " +
            "    (:overdue = false AND (t.deadline IS NULL OR t.deadline >= :now))" +
            ")")
    Page<Long> findTodoIds(
            @Param("completed") Boolean completed,
            @Param("priority") Priority priority,
            @Param("tag") String tag,
            @Param("overdue") Boolean overdue,
            @Param("now") LocalDateTime now,
            Pageable pageable
    );

    // ✅ QUERY 2: Bu ID'lere ait
    @Query("SELECT DISTINCT t FROM Todo t LEFT JOIN FETCH t.tags WHERE t.id IN :ids ORDER BY t.id DESC")
    List<Todo> findByIdsWithTags(@Param("ids") List<Long> ids);
}