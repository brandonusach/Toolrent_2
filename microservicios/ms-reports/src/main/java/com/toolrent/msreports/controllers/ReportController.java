package com.toolrent.msreports.controllers;

import com.toolrent.msreports.models.*;
import com.toolrent.msreports.services.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/reports")
@CrossOrigin("*")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardReport> getDashboard() {
        try {
            DashboardReport report = reportService.getDashboard();
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/tools")
    public ResponseEntity<List<ToolReportDTO>> getToolsReport() {
        try {
            List<ToolReportDTO> report = reportService.getToolsReport();
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/clients")
    public ResponseEntity<List<ClientReportDTO>> getClientsReport() {
        try {
            List<ClientReportDTO> report = reportService.getClientsReport();
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/financial")
    public ResponseEntity<Map<String, Object>> getFinancialReport() {
        try {
            Map<String, Object> report = reportService.getFinancialReport();
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}

