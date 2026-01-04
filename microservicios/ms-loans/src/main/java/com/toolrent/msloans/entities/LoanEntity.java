package com.toolrent.msloans.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.math.BigDecimal;

@Entity
@Table(name = "loans")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "client_id", nullable = false)
    private Long clientId;

    @Column(name = "tool_id", nullable = false)
    private Long toolId;

    @Column(name = "tool_instance_id", nullable = false)
    private Long toolInstanceId;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "loan_date", nullable = false)
    private LocalDate loanDate;

    @Column(name = "agreed_return_date", nullable = false)
    private LocalDate agreedReturnDate;

    @Column(name = "actual_return_date")
    private LocalDate actualReturnDate;

    @Column(name = "daily_rate", nullable = false, precision = 10, scale = 2)
    private BigDecimal dailyRate;

    @Column(length = 500)
    private String notes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoanStatus status;

    @PrePersist
    protected void onCreate() {
        if (loanDate == null) {
            loanDate = LocalDate.now();
        }
        if (status == null) {
            status = LoanStatus.ACTIVE;
        }
    }

    public enum LoanStatus {
        ACTIVE,
        RETURNED,
        OVERDUE,
        DAMAGED
    }
}

