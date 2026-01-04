package com.toolrent.msloans.services;

import com.toolrent.msloans.entities.DamageEntity;
import com.toolrent.msloans.repositories.DamageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class DamageService {

    @Autowired
    private DamageRepository damageRepository;

    public List<DamageEntity> getAllDamages() {
        return damageRepository.findAll();
    }

    public DamageEntity getDamageById(Long id) {
        return damageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Daño no encontrado con id: " + id));
    }

    public List<DamageEntity> getDamagesByLoanId(Long loanId) {
        return damageRepository.findByLoanId(loanId);
    }

    @Transactional
    public DamageEntity createDamage(DamageEntity damage) {
        return damageRepository.save(damage);
    }

    @Transactional
    public DamageEntity updateDamageStatus(Long id, DamageEntity.DamageStatus status) {
        DamageEntity damage = getDamageById(id);
        damage.setStatus(status);
        return damageRepository.save(damage);
    }

    @Transactional
    public void deleteDamage(Long id) {
        if (!damageRepository.existsById(id)) {
            throw new RuntimeException("Daño no encontrado");
        }
        damageRepository.deleteById(id);
    }
}

