package com.toolrent.msreports.services;

import com.toolrent.msreports.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.*;

@Service
public class ReportService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${microservices.inventory.url}")
    private String inventoryUrl;

    @Value("${microservices.loans.url}")
    private String loansUrl;

    @Value("${microservices.clients.url}")
    private String clientsUrl;

    @Value("${microservices.rates.url}")
    private String ratesUrl;

    @Value("${microservices.kardex.url}")
    private String kardexUrl;

    @SuppressWarnings("unchecked")
    public DashboardReport getDashboard() {
        DashboardReport report = new DashboardReport();

        try {
            // Obtener herramientas
            List<Map<String, Object>> tools = restTemplate.getForObject(
                inventoryUrl + "/api/v1/tools/",
                List.class
            );
            report.setTotalTools(tools != null ? (long) tools.size() : 0L);

            // Obtener clientes
            List<Map<String, Object>> clients = restTemplate.getForObject(
                clientsUrl + "/api/v1/clients/",
                List.class
            );
            report.setTotalClients(clients != null ? (long) clients.size() : 0L);

            // Obtener préstamos
            List<Map<String, Object>> loans = restTemplate.getForObject(
                loansUrl + "/api/v1/loans/",
                List.class
            );
            report.setTotalLoans(loans != null ? (long) loans.size() : 0L);

            // Contar préstamos activos
            long activeLoans = loans != null ? loans.stream()
                .filter(loan -> "ACTIVE".equals(loan.get("status")))
                .count() : 0L;
            report.setActiveLoans(activeLoans);

            // Obtener multas pendientes
            List<Map<String, Object>> fines = restTemplate.getForObject(
                ratesUrl + "/api/v1/fines/pending",
                List.class
            );
            report.setPendingFines(fines != null ? (long) fines.size() : 0L);

            // Herramientas por categoría
            Map<String, Long> toolsByCategory = new HashMap<>();
            if (tools != null) {
                for (Map<String, Object> tool : tools) {
                    Map<String, Object> category = (Map<String, Object>) tool.get("category");
                    if (category != null) {
                        String categoryName = (String) category.get("name");
                        toolsByCategory.put(categoryName, toolsByCategory.getOrDefault(categoryName, 0L) + 1);
                    }
                }
            }
            report.setToolsByCategory(toolsByCategory);

            // Préstamos por estado
            Map<String, Long> loansByStatus = new HashMap<>();
            if (loans != null) {
                for (Map<String, Object> loan : loans) {
                    String status = (String) loan.get("status");
                    loansByStatus.put(status, loansByStatus.getOrDefault(status, 0L) + 1);
                }
            }
            report.setLoansByStatus(loansByStatus);

        } catch (Exception e) {
            System.err.println("Error generando dashboard: " + e.getMessage());
        }

        return report;
    }

    @SuppressWarnings("unchecked")
    public List<ToolReportDTO> getToolsReport() {
        List<ToolReportDTO> report = new ArrayList<>();

        try {
            List<Map<String, Object>> tools = restTemplate.getForObject(
                inventoryUrl + "/api/v1/tools/",
                List.class
            );

            List<Map<String, Object>> loans = restTemplate.getForObject(
                loansUrl + "/api/v1/loans/",
                List.class
            );

            if (tools != null) {
                for (Map<String, Object> tool : tools) {
                    ToolReportDTO dto = new ToolReportDTO();
                    dto.setToolId(((Number) tool.get("id")).longValue());
                    dto.setToolName((String) tool.get("name"));

                    Map<String, Object> category = (Map<String, Object>) tool.get("category");
                    if (category != null) {
                        dto.setCategoryName((String) category.get("name"));
                    }

                    dto.setCurrentStock(((Number) tool.get("currentStock")).intValue());

                    // Contar préstamos de esta herramienta
                    if (loans != null) {
                        long totalLoansForTool = loans.stream()
                            .filter(loan -> {
                                Number toolId = (Number) loan.get("toolId");
                                return toolId != null && toolId.longValue() == dto.getToolId();
                            })
                            .count();
                        dto.setTotalLoans(totalLoansForTool);

                        long activeLoansForTool = loans.stream()
                            .filter(loan -> {
                                Number toolId = (Number) loan.get("toolId");
                                return toolId != null &&
                                       toolId.longValue() == dto.getToolId() &&
                                       "ACTIVE".equals(loan.get("status"));
                            })
                            .count();
                        dto.setActiveLoans(activeLoansForTool);
                    }

                    report.add(dto);
                }
            }
        } catch (Exception e) {
            System.err.println("Error generando reporte de herramientas: " + e.getMessage());
        }

        return report;
    }

    @SuppressWarnings("unchecked")
    public List<ClientReportDTO> getClientsReport() {
        List<ClientReportDTO> report = new ArrayList<>();

        try {
            List<Map<String, Object>> clients = restTemplate.getForObject(
                clientsUrl + "/api/v1/clients/",
                List.class
            );

            List<Map<String, Object>> loans = restTemplate.getForObject(
                loansUrl + "/api/v1/loans/",
                List.class
            );

            if (clients != null) {
                for (Map<String, Object> client : clients) {
                    ClientReportDTO dto = new ClientReportDTO();
                    dto.setClientId(((Number) client.get("id")).longValue());
                    dto.setClientName((String) client.get("name"));
                    dto.setClientEmail((String) client.get("email"));
                    dto.setStatus((String) client.get("status"));

                    // Contar préstamos del cliente
                    if (loans != null) {
                        long totalLoansForClient = loans.stream()
                            .filter(loan -> {
                                Number clientId = (Number) loan.get("clientId");
                                return clientId != null && clientId.longValue() == dto.getClientId();
                            })
                            .count();
                        dto.setTotalLoans(totalLoansForClient);

                        long activeLoansForClient = loans.stream()
                            .filter(loan -> {
                                Number clientId = (Number) loan.get("clientId");
                                return clientId != null &&
                                       clientId.longValue() == dto.getClientId() &&
                                       "ACTIVE".equals(loan.get("status"));
                            })
                            .count();
                        dto.setActiveLoans(activeLoansForClient);
                    }

                    report.add(dto);
                }
            }
        } catch (Exception e) {
            System.err.println("Error generando reporte de clientes: " + e.getMessage());
        }

        return report;
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getFinancialReport() {
        Map<String, Object> report = new HashMap<>();

        try {
            List<Map<String, Object>> fines = restTemplate.getForObject(
                ratesUrl + "/api/v1/fines/",
                List.class
            );

            if (fines != null) {
                double totalFines = fines.stream()
                    .mapToDouble(fine -> ((Number) fine.get("amount")).doubleValue())
                    .sum();

                double pendingFines = fines.stream()
                    .filter(fine -> "PENDING".equals(fine.get("status")))
                    .mapToDouble(fine -> ((Number) fine.get("amount")).doubleValue())
                    .sum();

                double paidFines = fines.stream()
                    .filter(fine -> "PAID".equals(fine.get("status")))
                    .mapToDouble(fine -> ((Number) fine.get("amount")).doubleValue())
                    .sum();

                report.put("totalFines", totalFines);
                report.put("pendingFines", pendingFines);
                report.put("paidFines", paidFines);
                report.put("totalFinesCount", fines.size());
            }

        } catch (Exception e) {
            System.err.println("Error generando reporte financiero: " + e.getMessage());
        }

        return report;
    }
}

