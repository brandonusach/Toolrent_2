package com.toolrent.msrates.repositories;

import com.toolrent.msrates.entities.RateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RateRepository extends JpaRepository<RateEntity, Long> {
    Optional<RateEntity> findByToolId(Long toolId);
    boolean existsByToolId(Long toolId);
}

