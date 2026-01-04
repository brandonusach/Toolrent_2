package com.toolrent.msrates.controllers;

import com.toolrent.msrates.entities.FineEntity;
import com.toolrent.msrates.services.FineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/fines")
@CrossOrigin("*")
public class FineController {

    @Autowired
    private FineService fineService;

    @GetMapping("/")
    public ResponseEntity<List<FineEntity>> getAllFines() {
        List<FineEntity> fines = fineService.getAllFines();
        return ResponseEntity.ok(fines);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FineEntity> getFineById(@PathVariable Long id) {
        try {
            FineEntity fine = fineService.getFineById(id);
            return ResponseEntity.ok(fine);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/loan/{loanId}")
    public ResponseEntity<List<FineEntity>> getFinesByLoan(@PathVariable Long loanId) {
        List<FineEntity> fines = fineService.getFinesByLoanId(loanId);
        return ResponseEntity.ok(fines);
    }

    @GetMapping("/pending")
    public ResponseEntity<List<FineEntity>> getPendingFines() {
        List<FineEntity> fines = fineService.getPendingFines();
        return ResponseEntity.ok(fines);
    }

    @PostMapping("/")
    public ResponseEntity<?> createFine(@RequestBody FineEntity fine) {
        try {
            FineEntity newFine = fineService.createFine(fine);
            return ResponseEntity.ok(newFine);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/late")
    public ResponseEntity<?> createLateFine(@RequestParam Long loanId, @RequestParam Long toolId, @RequestParam int daysLate) {
        try {
            FineEntity fine = fineService.createLateFine(loanId, toolId, daysLate);
            return ResponseEntity.ok(fine);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/damage")
    public ResponseEntity<?> createDamageFine(
            @RequestParam Long loanId,
            @RequestParam Long toolId,
            @RequestParam BigDecimal replacementValue,
            @RequestParam String damageDescription) {
        try {
            FineEntity fine = fineService.createDamageFine(loanId, toolId, replacementValue, damageDescription);
            return ResponseEntity.ok(fine);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateFineStatus(@PathVariable Long id, @RequestParam FineEntity.FineStatus status) {
        try {
            FineEntity updated = fineService.updateFineStatus(id, status);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/pay")
    public ResponseEntity<?> payFine(@PathVariable Long id) {
        try {
            FineEntity paid = fineService.payFine(id);
            return ResponseEntity.ok(paid);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFine(@PathVariable Long id) {
        try {
            fineService.deleteFine(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/loan/{loanId}/total")
    public ResponseEntity<BigDecimal> getTotalFinesForLoan(@PathVariable Long loanId) {
        BigDecimal total = fineService.getTotalFinesForLoan(loanId);
        return ResponseEntity.ok(total);
    }
}

