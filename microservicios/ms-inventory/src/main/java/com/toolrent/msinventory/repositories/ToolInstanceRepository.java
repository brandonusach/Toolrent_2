package com.toolrent.msinventory.repositories;

import com.toolrent.msinventory.entities.ToolInstanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ToolInstanceRepository extends JpaRepository<ToolInstanceEntity, Long> {
    List<ToolInstanceEntity> findByToolId(Long toolId);
    List<ToolInstanceEntity> findByStatus(ToolInstanceEntity.ToolInstanceStatus status);
    List<ToolInstanceEntity> findByToolIdAndStatus(Long toolId, ToolInstanceEntity.ToolInstanceStatus status);
    long countByToolIdAndStatus(Long toolId, ToolInstanceEntity.ToolInstanceStatus status);
}

