package com.example.todoapp.repository;

import com.example.todoapp.enums.Priority;
import com.example.todoapp.model.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {

    @Query("SELECT DISTINCT t.id FROM Todo t LEFT JOIN t.tags tag_element WHERE " +
            "(:completed IS NULL OR t.completed = :completed) AND " +
            "(:priority IS NULL OR t.priority = :priority) AND " +
            "(:tag IS NULL OR :tag = '' OR tag_element = :tag) AND " +
            "(:overdue IS NULL OR " +
            "    (:overdue = true AND t.deadline IS NOT NULL AND t.deadline < :now) OR " +
            "    (:overdue = false AND (t.deadline IS NULL OR t.deadline >= :now))" +
            ") AND " +
            "t.userEmail = :userEmail")
    Page<Long> findTodoIds(
            @Param("completed") Boolean completed,
            @Param("priority") Priority priority,
            @Param("tag") String tag,
            @Param("overdue") Boolean overdue,
            @Param("userEmail") String userEmail,
            @Param("now") long now,
            Pageable pageable
    );

    @Query("SELECT DISTINCT t FROM Todo t LEFT JOIN FETCH t.tags WHERE t.id IN :ids")
    List<Todo> findByIdsWithTags(@Param("ids") List<Long> ids);

    List<Todo> findByDeadlineIsNotNullAndDeadlineLessThanEqualAndCompletedFalseAndReminderSentFalse(long deadline);

    Optional<Todo> findByIdAndUserEmail(Long id, String userEmail);

    boolean existsByIdAndUserEmail(Long id, String userEmail);
}