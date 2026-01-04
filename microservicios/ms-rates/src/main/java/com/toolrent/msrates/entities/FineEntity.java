package com.toolrent.msrates.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "fines")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FineEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "loan_id", nullable = false)
    private Long loanId;

    @Enumerated(EnumType.STRING)
    @Column(name = "fine_type", nullable = false)
    private FineType fineType;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(length = 500)
    private String description;

    @Column(name = "fine_date", nullable = false)
    private LocalDate fineDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FineStatus status;

    public enum FineType {
        LATE_RETURN,    // Retraso en devolución
        DAMAGE,         // Daño a herramienta
        LOSS            // Pérdida de herramienta
    }

    public enum FineStatus {
        PENDING,
        PAID,
        WAIVED
    }

    @PrePersist
    protected void onCreate() {
        if (fineDate == null) {
            fineDate = LocalDate.now();
        }
        if (status == null) {
            status = FineStatus.PENDING;
        }
    }
}

