package com.toolrent.msreports.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientReportDTO {
    private Long clientId;
    private String clientName;
    private String clientEmail;
    private Long totalLoans;
    private Long activeLoans;
    private BigDecimal totalFines;
    private BigDecimal pendingFines;
    private String status;
}

