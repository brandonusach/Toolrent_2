package com.toolrent.msloans.repositories;

import com.toolrent.msloans.entities.LoanEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<LoanEntity, Long> {
    List<LoanEntity> findByClientId(Long clientId);
    List<LoanEntity> findByToolId(Long toolId);
    List<LoanEntity> findByStatus(LoanEntity.LoanStatus status);
    List<LoanEntity> findByClientIdAndStatus(Long clientId, LoanEntity.LoanStatus status);
}

