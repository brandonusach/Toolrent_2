package com.toolrent.msclients.repositories;

import com.toolrent.msclients.entities.ClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface ClientRepository extends JpaRepository<ClientEntity, Long> {
    Optional<ClientEntity> findByRut(String rut);
    Optional<ClientEntity> findByEmail(String email);
    boolean existsByRut(String rut);
    boolean existsByEmail(String email);
    List<ClientEntity> findByNameContainingIgnoreCase(String name);
}

