package com.toolrent.msrates.services;

import com.toolrent.msrates.entities.FineEntity;
import com.toolrent.msrates.repositories.FineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Service
public class FineService {

    @Autowired
    private FineRepository fineRepository;

    @Autowired
    private RateService rateService;

    public List<FineEntity> getAllFines() {
        return fineRepository.findAll();
    }

    public FineEntity getFineById(Long id) {
        return fineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Multa no encontrada con id: " + id));
    }

    public List<FineEntity> getFinesByLoanId(Long loanId) {
        return fineRepository.findByLoanId(loanId);
    }

    public List<FineEntity> getPendingFines() {
        return fineRepository.findByStatus(FineEntity.FineStatus.PENDING);
    }

    @Transactional
    public FineEntity createFine(FineEntity fine) {
        return fineRepository.save(fine);
    }

    @Transactional
    public FineEntity createLateFine(Long loanId, Long toolId, int daysLate) {
        BigDecimal amount = rateService.calculateLateFee(toolId, daysLate);

        FineEntity fine = new FineEntity();
        fine.setLoanId(loanId);
        fine.setFineType(FineEntity.FineType.LATE_RETURN);
        fine.setAmount(amount);
        fine.setDescription("Multa por " + daysLate + " días de retraso");
        fine.setStatus(FineEntity.FineStatus.PENDING);

        return fineRepository.save(fine);
    }

    @Transactional
    public FineEntity createDamageFine(Long loanId, Long toolId, BigDecimal replacementValue, String damageDescription) {
        BigDecimal amount = rateService.calculateDamageFee(toolId, replacementValue);

        FineEntity fine = new FineEntity();
        fine.setLoanId(loanId);
        fine.setFineType(FineEntity.FineType.DAMAGE);
        fine.setAmount(amount);
        fine.setDescription("Multa por daño: " + damageDescription);
        fine.setStatus(FineEntity.FineStatus.PENDING);

        return fineRepository.save(fine);
    }

    @Transactional
    public FineEntity updateFineStatus(Long id, FineEntity.FineStatus status) {
        FineEntity fine = getFineById(id);
        fine.setStatus(status);
        return fineRepository.save(fine);
    }

    @Transactional
    public FineEntity payFine(Long id) {
        return updateFineStatus(id, FineEntity.FineStatus.PAID);
    }

    @Transactional
    public void deleteFine(Long id) {
        if (!fineRepository.existsById(id)) {
            throw new RuntimeException("Multa no encontrada");
        }
        fineRepository.deleteById(id);
    }

    public BigDecimal getTotalFinesForLoan(Long loanId) {
        List<FineEntity> fines = getFinesByLoanId(loanId);
        return fines.stream()
                .map(FineEntity::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}

