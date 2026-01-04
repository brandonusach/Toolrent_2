package com.toolrent.mskardex.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "kardex_movements")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KardexMovementEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tool_id", nullable = false)
    private Long toolId;

    @Enumerated(EnumType.STRING)
    @Column(name = "movement_type", nullable = false)
    private MovementType movementType;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "balance_after")
    private Integer balanceAfter; // Stock después del movimiento

    @Column(name = "movement_date", nullable = false)
    private LocalDateTime movementDate;

    @Column(length = 500)
    private String reason;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "reference_id")
    private Long referenceId; // ID del préstamo, devolución, etc.

    @Column(name = "reference_type")
    private String referenceType; // LOAN, RETURN, ADJUSTMENT, etc.

    public enum MovementType {
        ENTRY,          // Entrada de stock (compra, devolución)
        EXIT,           // Salida por préstamo
        RETURN,         // Devolución
        ADJUSTMENT,     // Ajuste de inventario
        DAMAGE,         // Daño reportado
        DECOMMISSION,   // Baja de herramienta
        INITIAL_STOCK   // Stock inicial
    }

    @PrePersist
    protected void onCreate() {
        if (movementDate == null) {
            movementDate = LocalDateTime.now();
        }
    }
}

