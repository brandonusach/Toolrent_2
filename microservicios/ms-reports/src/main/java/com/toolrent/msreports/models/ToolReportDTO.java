package com.toolrent.msreports.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ToolReportDTO {
    private Long toolId;
    private String toolName;
    private String categoryName;
    private Long totalLoans;
    private Long activeLoans;
    private BigDecimal totalRevenue;
    private Integer currentStock;
}

