package com.toolrent.mskardex.repositories;

import com.toolrent.mskardex.entities.KardexMovementEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface KardexMovementRepository extends JpaRepository<KardexMovementEntity, Long> {

    List<KardexMovementEntity> findByToolId(Long toolId);

    List<KardexMovementEntity> findByToolIdOrderByMovementDateDesc(Long toolId);

    List<KardexMovementEntity> findByMovementType(KardexMovementEntity.MovementType movementType);

    List<KardexMovementEntity> findByReferenceIdAndReferenceType(Long referenceId, String referenceType);

    @Query("SELECT km FROM KardexMovementEntity km WHERE km.toolId = :toolId AND km.movementDate BETWEEN :startDate AND :endDate ORDER BY km.movementDate DESC")
    List<KardexMovementEntity> findMovementsByToolAndDateRange(
        @Param("toolId") Long toolId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT km FROM KardexMovementEntity km WHERE km.movementDate BETWEEN :startDate AND :endDate ORDER BY km.movementDate DESC")
    List<KardexMovementEntity> findMovementsByDateRange(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
}

