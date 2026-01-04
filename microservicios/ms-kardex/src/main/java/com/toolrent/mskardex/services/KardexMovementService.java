package com.toolrent.mskardex.services;

import com.toolrent.mskardex.entities.KardexMovementEntity;
import com.toolrent.mskardex.repositories.KardexMovementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class KardexMovementService {

    @Autowired
    private KardexMovementRepository kardexMovementRepository;

    public List<KardexMovementEntity> getAllMovements() {
        return kardexMovementRepository.findAll();
    }

    public KardexMovementEntity getMovementById(Long id) {
        return kardexMovementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movimiento no encontrado con id: " + id));
    }

    public List<KardexMovementEntity> getMovementsByToolId(Long toolId) {
        return kardexMovementRepository.findByToolIdOrderByMovementDateDesc(toolId);
    }

    public List<KardexMovementEntity> getMovementsByType(KardexMovementEntity.MovementType type) {
        return kardexMovementRepository.findByMovementType(type);
    }

    public List<KardexMovementEntity> getMovementsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return kardexMovementRepository.findMovementsByDateRange(startDate, endDate);
    }

    public List<KardexMovementEntity> getMovementsByToolAndDateRange(Long toolId, LocalDateTime startDate, LocalDateTime endDate) {
        return kardexMovementRepository.findMovementsByToolAndDateRange(toolId, startDate, endDate);
    }

    @Transactional
    public KardexMovementEntity createMovement(KardexMovementEntity movement) {
        return kardexMovementRepository.save(movement);
    }

    @Transactional
    public KardexMovementEntity registerLoan(Long toolId, Integer quantity, Long loanId, Long userId) {
        KardexMovementEntity movement = new KardexMovementEntity();
        movement.setToolId(toolId);
        movement.setMovementType(KardexMovementEntity.MovementType.EXIT);
        movement.setQuantity(quantity);
        movement.setReason("Préstamo de herramienta");
        movement.setUserId(userId);
        movement.setReferenceId(loanId);
        movement.setReferenceType("LOAN");

        return kardexMovementRepository.save(movement);
    }

    @Transactional
    public KardexMovementEntity registerReturn(Long toolId, Integer quantity, Long loanId, Long userId) {
        KardexMovementEntity movement = new KardexMovementEntity();
        movement.setToolId(toolId);
        movement.setMovementType(KardexMovementEntity.MovementType.RETURN);
        movement.setQuantity(quantity);
        movement.setReason("Devolución de herramienta");
        movement.setUserId(userId);
        movement.setReferenceId(loanId);
        movement.setReferenceType("RETURN");

        return kardexMovementRepository.save(movement);
    }

    @Transactional
    public KardexMovementEntity registerEntry(Long toolId, Integer quantity, String reason, Long userId) {
        KardexMovementEntity movement = new KardexMovementEntity();
        movement.setToolId(toolId);
        movement.setMovementType(KardexMovementEntity.MovementType.ENTRY);
        movement.setQuantity(quantity);
        movement.setReason(reason);
        movement.setUserId(userId);

        return kardexMovementRepository.save(movement);
    }

    @Transactional
    public KardexMovementEntity registerDamage(Long toolId, Long damageId, String description, Long userId) {
        KardexMovementEntity movement = new KardexMovementEntity();
        movement.setToolId(toolId);
        movement.setMovementType(KardexMovementEntity.MovementType.DAMAGE);
        movement.setQuantity(1);
        movement.setReason("Daño reportado: " + description);
        movement.setUserId(userId);
        movement.setReferenceId(damageId);
        movement.setReferenceType("DAMAGE");

        return kardexMovementRepository.save(movement);
    }

    @Transactional
    public KardexMovementEntity registerAdjustment(Long toolId, Integer quantity, String reason, Long userId) {
        KardexMovementEntity movement = new KardexMovementEntity();
        movement.setToolId(toolId);
        movement.setMovementType(KardexMovementEntity.MovementType.ADJUSTMENT);
        movement.setQuantity(quantity);
        movement.setReason(reason);
        movement.setUserId(userId);

        return kardexMovementRepository.save(movement);
    }

    @Transactional
    public void deleteMovement(Long id) {
        if (!kardexMovementRepository.existsById(id)) {
            throw new RuntimeException("Movimiento no encontrado");
        }
        kardexMovementRepository.deleteById(id);
    }

    public Integer calculateCurrentBalance(Long toolId) {
        List<KardexMovementEntity> movements = getMovementsByToolId(toolId);
        int balance = 0;

        for (KardexMovementEntity movement : movements) {
            switch (movement.getMovementType()) {
                case ENTRY:
                case RETURN:
                case INITIAL_STOCK:
                    balance += movement.getQuantity();
                    break;
                case EXIT:
                case DAMAGE:
                case DECOMMISSION:
                    balance -= movement.getQuantity();
                    break;
                case ADJUSTMENT:
                    // Puede ser positivo o negativo
                    balance += movement.getQuantity();
                    break;
            }
        }

        return balance;
    }
}

