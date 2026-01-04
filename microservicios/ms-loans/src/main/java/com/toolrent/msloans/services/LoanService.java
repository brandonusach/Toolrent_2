package com.toolrent.msloans.services;

import com.toolrent.msloans.entities.LoanEntity;
import com.toolrent.msloans.repositories.LoanRepository;
import com.toolrent.msloans.models.ClientDTO;
import com.toolrent.msloans.models.ToolDTO;
import com.toolrent.msloans.models.ToolInstanceDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import java.time.LocalDate;
import java.util.List;

@Service
public class LoanService {

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${microservices.inventory.url}")
    private String inventoryServiceUrl;

    @Value("${microservices.clients.url}")
    private String clientsServiceUrl;

    @Value("${microservices.kardex.url}")
    private String kardexServiceUrl;

    public List<LoanEntity> getAllLoans() {
        return loanRepository.findAll();
    }

    public LoanEntity getLoanById(Long id) {
        return loanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Préstamo no encontrado con id: " + id));
    }

    public List<LoanEntity> getLoansByClientId(Long clientId) {
        return loanRepository.findByClientId(clientId);
    }

    public List<LoanEntity> getActiveLoans() {
        return loanRepository.findByStatus(LoanEntity.LoanStatus.ACTIVE);
    }

    @Transactional
    public LoanEntity createLoan(LoanEntity loan) {
        // 1. Validar que el cliente existe y está activo
        ClientDTO client = validateClient(loan.getClientId());
        if (!"ACTIVE".equals(client.getStatus())) {
            throw new IllegalArgumentException("El cliente no está activo para realizar préstamos");
        }

        // 2. Validar que la herramienta existe y hay stock disponible
        ToolDTO tool = validateTool(loan.getToolId());
        if (tool.getCurrentStock() < loan.getQuantity()) {
            throw new IllegalArgumentException("No hay suficiente stock disponible");
        }

        // 3. Obtener una instancia disponible de la herramienta
        ToolInstanceDTO instance = getAvailableToolInstance(loan.getToolId());
        loan.setToolInstanceId(instance.getId());

        // 4. Establecer la tarifa diaria desde la herramienta
        loan.setDailyRate(tool.getRentalRate());

        // 5. Guardar el préstamo
        LoanEntity savedLoan = loanRepository.save(loan);

        // 6. Actualizar el estado de la instancia a LOANED
        updateToolInstanceStatus(instance.getId(), "LOANED");

        // 7. Registrar movimiento en kardex (asíncrono)
        try {
            notifyKardexMovement(savedLoan, "LOAN");
        } catch (Exception e) {
            // Log pero no fallar la transacción
            System.err.println("Error notificando al kardex: " + e.getMessage());
        }

        return savedLoan;
    }

    @Transactional
    public LoanEntity returnLoan(Long loanId, boolean hasDamage) {
        LoanEntity loan = getLoanById(loanId);

        if (loan.getStatus() != LoanEntity.LoanStatus.ACTIVE) {
            throw new IllegalArgumentException("El préstamo no está activo");
        }

        loan.setActualReturnDate(LocalDate.now());

        if (hasDamage) {
            loan.setStatus(LoanEntity.LoanStatus.DAMAGED);
            updateToolInstanceStatus(loan.getToolInstanceId(), "UNDER_REPAIR");
        } else {
            boolean isOverdue = loan.getActualReturnDate().isAfter(loan.getAgreedReturnDate());
            loan.setStatus(isOverdue ? LoanEntity.LoanStatus.OVERDUE : LoanEntity.LoanStatus.RETURNED);
            updateToolInstanceStatus(loan.getToolInstanceId(), "AVAILABLE");
        }

        LoanEntity returned = loanRepository.save(loan);

        // Notificar al kardex
        try {
            notifyKardexMovement(returned, "RETURN");
        } catch (Exception e) {
            System.err.println("Error notificando al kardex: " + e.getMessage());
        }

        return returned;
    }

    private ClientDTO validateClient(Long clientId) {
        try {
            return restTemplate.getForObject(
                clientsServiceUrl + "/api/v1/clients/" + clientId,
                ClientDTO.class
            );
        } catch (HttpClientErrorException e) {
            throw new IllegalArgumentException("Cliente no encontrado");
        }
    }

    private ToolDTO validateTool(Long toolId) {
        try {
            return restTemplate.getForObject(
                inventoryServiceUrl + "/api/v1/tools/" + toolId,
                ToolDTO.class
            );
        } catch (HttpClientErrorException e) {
            throw new IllegalArgumentException("Herramienta no encontrada");
        }
    }

    private ToolInstanceDTO getAvailableToolInstance(Long toolId) {
        try {
            ToolInstanceDTO[] instances = restTemplate.getForObject(
                inventoryServiceUrl + "/api/v1/tool-instances/tool/" + toolId + "/available",
                ToolInstanceDTO[].class
            );

            if (instances == null || instances.length == 0) {
                throw new IllegalArgumentException("No hay instancias disponibles de esta herramienta");
            }

            return instances[0];
        } catch (HttpClientErrorException e) {
            throw new IllegalArgumentException("Error consultando disponibilidad de herramientas");
        }
    }

    private void updateToolInstanceStatus(Long instanceId, String status) {
        try {
            restTemplate.put(
                inventoryServiceUrl + "/api/v1/tool-instances/" + instanceId + "/status?status=" + status,
                null
            );
        } catch (Exception e) {
            System.err.println("Error actualizando estado de instancia: " + e.getMessage());
        }
    }

    private void notifyKardexMovement(LoanEntity loan, String movementType) {
        try {
            // Aquí se haría la llamada al microservicio de kardex
            // Por ahora solo un placeholder
            String url = kardexServiceUrl + "/api/v1/kardex/movement";
            // restTemplate.postForObject(url, movementData, Void.class);
        } catch (Exception e) {
            throw e;
        }
    }

    @Transactional
    public void deleteLoan(Long id) {
        if (!loanRepository.existsById(id)) {
            throw new RuntimeException("Préstamo no encontrado");
        }
        loanRepository.deleteById(id);
    }
}

