package com.toolrent.msloans.controllers;

import com.toolrent.msloans.entities.LoanEntity;
import com.toolrent.msloans.services.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/loans")
@CrossOrigin("*")
public class LoanController {

    @Autowired
    private LoanService loanService;

    @GetMapping("/")
    public ResponseEntity<List<LoanEntity>> getAllLoans() {
        List<LoanEntity> loans = loanService.getAllLoans();
        return ResponseEntity.ok(loans);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LoanEntity> getLoanById(@PathVariable Long id) {
        try {
            LoanEntity loan = loanService.getLoanById(id);
            return ResponseEntity.ok(loan);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<LoanEntity>> getLoansByClient(@PathVariable Long clientId) {
        List<LoanEntity> loans = loanService.getLoansByClientId(clientId);
        return ResponseEntity.ok(loans);
    }

    @GetMapping("/active")
    public ResponseEntity<List<LoanEntity>> getActiveLoans() {
        List<LoanEntity> loans = loanService.getActiveLoans();
        return ResponseEntity.ok(loans);
    }

    @PostMapping("/")
    public ResponseEntity<?> createLoan(@RequestBody LoanEntity loan) {
        try {
            LoanEntity newLoan = loanService.createLoan(loan);
            return ResponseEntity.ok(newLoan);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/return")
    public ResponseEntity<?> returnLoan(@PathVariable Long id, @RequestParam(defaultValue = "false") boolean hasDamage) {
        try {
            LoanEntity returned = loanService.returnLoan(id, hasDamage);
            return ResponseEntity.ok(returned);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteLoan(@PathVariable Long id) {
        try {
            loanService.deleteLoan(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

