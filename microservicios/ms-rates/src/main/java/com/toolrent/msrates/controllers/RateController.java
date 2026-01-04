package com.toolrent.msrates.controllers;

import com.toolrent.msrates.entities.RateEntity;
import com.toolrent.msrates.services.RateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/rates")
@CrossOrigin("*")
public class RateController {

    @Autowired
    private RateService rateService;

    @GetMapping("/")
    public ResponseEntity<List<RateEntity>> getAllRates() {
        List<RateEntity> rates = rateService.getAllRates();
        return ResponseEntity.ok(rates);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RateEntity> getRateById(@PathVariable Long id) {
        try {
            RateEntity rate = rateService.getRateById(id);
            return ResponseEntity.ok(rate);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/tool/{toolId}")
    public ResponseEntity<RateEntity> getRateByToolId(@PathVariable Long toolId) {
        try {
            RateEntity rate = rateService.getRateByToolId(toolId);
            return ResponseEntity.ok(rate);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/")
    public ResponseEntity<?> createRate(@RequestBody RateEntity rate) {
        try {
            RateEntity newRate = rateService.saveRate(rate);
            return ResponseEntity.ok(newRate);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/")
    public ResponseEntity<?> updateRate(@RequestBody RateEntity rate) {
        try {
            RateEntity updated = rateService.updateRate(rate.getId(), rate);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRate(@PathVariable Long id) {
        try {
            rateService.deleteRate(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/calculate-late-fee")
    public ResponseEntity<BigDecimal> calculateLateFee(@RequestParam Long toolId, @RequestParam int daysLate) {
        try {
            BigDecimal fee = rateService.calculateLateFee(toolId, daysLate);
            return ResponseEntity.ok(fee);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/calculate-damage-fee")
    public ResponseEntity<BigDecimal> calculateDamageFee(@RequestParam Long toolId, @RequestParam BigDecimal replacementValue) {
        try {
            BigDecimal fee = rateService.calculateDamageFee(toolId, replacementValue);
            return ResponseEntity.ok(fee);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}

