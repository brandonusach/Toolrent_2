package com.toolrent.msloans.controllers;

import com.toolrent.msloans.entities.DamageEntity;
import com.toolrent.msloans.services.DamageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/damages")
@CrossOrigin("*")
public class DamageController {

    @Autowired
    private DamageService damageService;

    @GetMapping("/")
    public ResponseEntity<List<DamageEntity>> getAllDamages() {
        List<DamageEntity> damages = damageService.getAllDamages();
        return ResponseEntity.ok(damages);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DamageEntity> getDamageById(@PathVariable Long id) {
        try {
            DamageEntity damage = damageService.getDamageById(id);
            return ResponseEntity.ok(damage);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/loan/{loanId}")
    public ResponseEntity<List<DamageEntity>> getDamagesByLoan(@PathVariable Long loanId) {
        List<DamageEntity> damages = damageService.getDamagesByLoanId(loanId);
        return ResponseEntity.ok(damages);
    }

    @PostMapping("/")
    public ResponseEntity<?> createDamage(@RequestBody DamageEntity damage) {
        try {
            DamageEntity newDamage = damageService.createDamage(damage);
            return ResponseEntity.ok(newDamage);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateDamageStatus(@PathVariable Long id, @RequestParam DamageEntity.DamageStatus status) {
        try {
            DamageEntity updated = damageService.updateDamageStatus(id, status);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDamage(@PathVariable Long id) {
        try {
            damageService.deleteDamage(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

