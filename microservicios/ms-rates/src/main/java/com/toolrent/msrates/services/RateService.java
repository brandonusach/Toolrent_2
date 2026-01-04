package com.toolrent.msrates.services;

import com.toolrent.msrates.entities.RateEntity;
import com.toolrent.msrates.repositories.RateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Service
public class RateService {

    @Autowired
    private RateRepository rateRepository;

    public List<RateEntity> getAllRates() {
        return rateRepository.findAll();
    }

    public RateEntity getRateById(Long id) {
        return rateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarifa no encontrada con id: " + id));
    }

    public RateEntity getRateByToolId(Long toolId) {
        return rateRepository.findByToolId(toolId)
                .orElseThrow(() -> new RuntimeException("Tarifa no encontrada para herramienta: " + toolId));
    }

    @Transactional
    public RateEntity saveRate(RateEntity rate) {
        validateRateData(rate);
        return rateRepository.save(rate);
    }

    @Transactional
    public RateEntity updateRate(Long id, RateEntity rate) {
        RateEntity existing = getRateById(id);

        existing.setDailyRate(rate.getDailyRate());
        existing.setWeeklyRate(rate.getWeeklyRate());
        existing.setMonthlyRate(rate.getMonthlyRate());
        existing.setLateFeePerDay(rate.getLateFeePerDay());
        existing.setDamagePercentage(rate.getDamagePercentage());

        return rateRepository.save(existing);
    }

    @Transactional
    public void deleteRate(Long id) {
        if (!rateRepository.existsById(id)) {
            throw new RuntimeException("Tarifa no encontrada");
        }
        rateRepository.deleteById(id);
    }

    public BigDecimal calculateLateFee(Long toolId, int daysLate) {
        RateEntity rate = getRateByToolId(toolId);
        if (rate.getLateFeePerDay() == null) {
            return BigDecimal.ZERO;
        }
        return rate.getLateFeePerDay().multiply(BigDecimal.valueOf(daysLate));
    }

    public BigDecimal calculateDamageFee(Long toolId, BigDecimal replacementValue) {
        RateEntity rate = getRateByToolId(toolId);
        if (rate.getDamagePercentage() == null) {
            return replacementValue; // 100% del valor
        }
        return replacementValue.multiply(rate.getDamagePercentage()).divide(BigDecimal.valueOf(100));
    }

    private void validateRateData(RateEntity rate) {
        if (rate.getToolId() == null) {
            throw new IllegalArgumentException("El ID de herramienta es requerido");
        }
        if (rate.getDailyRate() == null || rate.getDailyRate().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("La tarifa diaria debe ser mayor a 0");
        }
    }
}

