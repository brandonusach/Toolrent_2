package com.toolrent.msreports.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardReport {
    private Long totalTools;
    private Long totalClients;
    private Long activeLoans;
    private Long totalLoans;
    private Long pendingFines;
    private Map<String, Long> toolsByCategory;
    private Map<String, Long> loansByStatus;
}

