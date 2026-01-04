package com.toolrent.msloans.repositories;

import com.toolrent.msloans.entities.DamageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DamageRepository extends JpaRepository<DamageEntity, Long> {
    List<DamageEntity> findByLoanId(Long loanId);
    List<DamageEntity> findByToolInstanceId(Long toolInstanceId);
    List<DamageEntity> findByStatus(DamageEntity.DamageStatus status);
}

