package com.toolrent.msinventory.repositories;

import com.toolrent.msinventory.entities.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
    boolean existsByNameIgnoreCase(String name);
    List<CategoryEntity> findByNameContainingIgnoreCase(String name);
}

