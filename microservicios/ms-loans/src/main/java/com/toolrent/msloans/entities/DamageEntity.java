package com.toolrent.msloans.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "damages")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DamageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "loan_id", nullable = false)
    private Long loanId;

    @Column(name = "tool_instance_id", nullable = false)
    private Long toolInstanceId;

    @Column(nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DamageSeverity severity;

    @Column(name = "repair_cost", precision = 10, scale = 2)
    private BigDecimal repairCost;

    @Column(name = "reported_date", nullable = false)
    private LocalDate reportedDate;

    @Column(name = "resolved_date")
    private LocalDate resolvedDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DamageStatus status;

    public enum DamageSeverity {
        MINOR,
        MODERATE,
        SEVERE,
        TOTAL_LOSS
    }

    public enum DamageStatus {
        REPORTED,
        UNDER_REPAIR,
        REPAIRED,
        WRITTEN_OFF
    }

    @PrePersist
    protected void onCreate() {
        if (reportedDate == null) {
            reportedDate = LocalDate.now();
        }
        if (status == null) {
            status = DamageStatus.REPORTED;
        }
    }
}

