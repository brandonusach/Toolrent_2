package com.toolrent.msrates.repositories;

import com.toolrent.msrates.entities.FineEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FineRepository extends JpaRepository<FineEntity, Long> {
    List<FineEntity> findByLoanId(Long loanId);
    List<FineEntity> findByStatus(FineEntity.FineStatus status);
    List<FineEntity> findByFineType(FineEntity.FineType fineType);
}

