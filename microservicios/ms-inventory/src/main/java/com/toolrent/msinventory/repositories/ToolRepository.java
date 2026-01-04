package com.toolrent.msinventory.repositories;

import com.toolrent.msinventory.entities.ToolEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ToolRepository extends JpaRepository<ToolEntity, Long> {
    List<ToolEntity> findByCategoryId(Long categoryId);
    List<ToolEntity> findByNameContainingIgnoreCase(String name);
    boolean existsByNameIgnoreCase(String name);

    @Query("SELECT t FROM ToolEntity t LEFT JOIN FETCH t.category")
    List<ToolEntity> findAllWithCategories();

    @Query("SELECT t FROM ToolEntity t LEFT JOIN FETCH t.category WHERE t.id = :id")
    Optional<ToolEntity> findByIdWithCategory(@Param("id") Long id);
}

