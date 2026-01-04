package com.toolrent.mskardex.controllers;

import com.toolrent.mskardex.entities.KardexMovementEntity;
import com.toolrent.mskardex.services.KardexMovementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/kardex")
@CrossOrigin("*")
public class KardexMovementController {

    @Autowired
    private KardexMovementService kardexMovementService;

    @GetMapping("/")
    public ResponseEntity<List<KardexMovementEntity>> getAllMovements() {
        List<KardexMovementEntity> movements = kardexMovementService.getAllMovements();
        return ResponseEntity.ok(movements);
    }

    @GetMapping("/{id}")
    public ResponseEntity<KardexMovementEntity> getMovementById(@PathVariable Long id) {
        try {
            KardexMovementEntity movement = kardexMovementService.getMovementById(id);
            return ResponseEntity.ok(movement);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/tool/{toolId}")
    public ResponseEntity<List<KardexMovementEntity>> getMovementsByTool(@PathVariable Long toolId) {
        List<KardexMovementEntity> movements = kardexMovementService.getMovementsByToolId(toolId);
        return ResponseEntity.ok(movements);
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<KardexMovementEntity>> getMovementsByType(@PathVariable KardexMovementEntity.MovementType type) {
        List<KardexMovementEntity> movements = kardexMovementService.getMovementsByType(type);
        return ResponseEntity.ok(movements);
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<KardexMovementEntity>> getMovementsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<KardexMovementEntity> movements = kardexMovementService.getMovementsByDateRange(startDate, endDate);
        return ResponseEntity.ok(movements);
    }

    @GetMapping("/tool/{toolId}/date-range")
    public ResponseEntity<List<KardexMovementEntity>> getMovementsByToolAndDateRange(
            @PathVariable Long toolId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<KardexMovementEntity> movements = kardexMovementService.getMovementsByToolAndDateRange(toolId, startDate, endDate);
        return ResponseEntity.ok(movements);
    }

    @PostMapping("/")
    public ResponseEntity<?> createMovement(@RequestBody KardexMovementEntity movement) {
        try {
            KardexMovementEntity newMovement = kardexMovementService.createMovement(movement);
            return ResponseEntity.ok(newMovement);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/loan")
    public ResponseEntity<?> registerLoan(
            @RequestParam Long toolId,
            @RequestParam Integer quantity,
            @RequestParam Long loanId,
            @RequestParam(required = false) Long userId) {
        try {
            KardexMovementEntity movement = kardexMovementService.registerLoan(toolId, quantity, loanId, userId);
            return ResponseEntity.ok(movement);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/return")
    public ResponseEntity<?> registerReturn(
            @RequestParam Long toolId,
            @RequestParam Integer quantity,
            @RequestParam Long loanId,
            @RequestParam(required = false) Long userId) {
        try {
            KardexMovementEntity movement = kardexMovementService.registerReturn(toolId, quantity, loanId, userId);
            return ResponseEntity.ok(movement);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/entry")
    public ResponseEntity<?> registerEntry(
            @RequestParam Long toolId,
            @RequestParam Integer quantity,
            @RequestParam String reason,
            @RequestParam(required = false) Long userId) {
        try {
            KardexMovementEntity movement = kardexMovementService.registerEntry(toolId, quantity, reason, userId);
            return ResponseEntity.ok(movement);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/damage")
    public ResponseEntity<?> registerDamage(
            @RequestParam Long toolId,
            @RequestParam Long damageId,
            @RequestParam String description,
            @RequestParam(required = false) Long userId) {
        try {
            KardexMovementEntity movement = kardexMovementService.registerDamage(toolId, damageId, description, userId);
            return ResponseEntity.ok(movement);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/adjustment")
    public ResponseEntity<?> registerAdjustment(
            @RequestParam Long toolId,
            @RequestParam Integer quantity,
            @RequestParam String reason,
            @RequestParam(required = false) Long userId) {
        try {
            KardexMovementEntity movement = kardexMovementService.registerAdjustment(toolId, quantity, reason, userId);
            return ResponseEntity.ok(movement);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMovement(@PathVariable Long id) {
        try {
            kardexMovementService.deleteMovement(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/tool/{toolId}/balance")
    public ResponseEntity<Integer> getCurrentBalance(@PathVariable Long toolId) {
        Integer balance = kardexMovementService.calculateCurrentBalance(toolId);
        return ResponseEntity.ok(balance);
    }
}

