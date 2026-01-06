package com.toolrent.msinventory.services;

import com.toolrent.msinventory.entities.ToolEntity;
import com.toolrent.msinventory.entities.ToolInstanceEntity;
import com.toolrent.msinventory.entities.CategoryEntity;
import com.toolrent.msinventory.repositories.ToolRepository;
import com.toolrent.msinventory.repositories.CategoryRepository;
import com.toolrent.msinventory.repositories.ToolInstanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class ToolService {

    @Autowired
    private ToolRepository toolRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ToolInstanceRepository toolInstanceRepository;

    public List<ToolEntity> getAllTools() {
        return toolRepository.findAllWithCategories();
    }

    public Optional<ToolEntity> getToolById(Long id) {
        return toolRepository.findByIdWithCategory(id);
    }

    @Transactional
    public ToolEntity createTool(ToolEntity tool) {
        validateToolData(tool);

        CategoryEntity category = categoryRepository.findById(tool.getCategory().getId())
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));

        tool.setCategory(category);
        tool.setCurrentStock(tool.getInitialStock());
        tool.setStatus(ToolEntity.ToolStatus.AVAILABLE);

        ToolEntity savedTool = toolRepository.save(tool);

        // Crear instancias
        for (int i = 0; i < tool.getInitialStock(); i++) {
            ToolInstanceEntity instance = new ToolInstanceEntity(savedTool);
            toolInstanceRepository.save(instance);
        }

        return savedTool;
    }

    @Transactional
    public ToolEntity updateTool(Long id, ToolEntity tool) {
        ToolEntity existing = toolRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Herramienta no encontrada"));

        existing.setName(tool.getName());
        existing.setReplacementValue(tool.getReplacementValue());
        existing.setRentalRate(tool.getRentalRate());

        if (tool.getCategory() != null && tool.getCategory().getId() != null) {
            CategoryEntity category = categoryRepository.findById(tool.getCategory().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));
            existing.setCategory(category);
        }

        return toolRepository.save(existing);
    }

    @Transactional
    public void deleteTool(Long id) {
        if (!toolRepository.existsById(id)) {
            throw new RuntimeException("Herramienta no encontrada");
        }
        toolRepository.deleteById(id);
    }

    public List<ToolEntity> searchToolsByName(String name) {
        return toolRepository.findByNameContainingIgnoreCase(name);
    }

    public List<ToolEntity> getToolsByCategory(Long categoryId) {
        return toolRepository.findByCategoryId(categoryId);
    }

    @Transactional
    public ToolEntity updateStock(Long toolId, Integer newStock) {
        ToolEntity tool = toolRepository.findById(toolId)
                .orElseThrow(() -> new RuntimeException("Herramienta no encontrada"));

        tool.setCurrentStock(newStock);
        return toolRepository.save(tool);
    }

    @Transactional
    public ToolEntity addStock(Long toolId, Integer quantity) {
        if (quantity == null || quantity < 1) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
        }

        ToolEntity tool = toolRepository.findById(toolId)
                .orElseThrow(() -> new RuntimeException("Herramienta no encontrada"));

        // Crear nuevas instancias
        for (int i = 0; i < quantity; i++) {
            ToolInstanceEntity instance = new ToolInstanceEntity(tool);
            toolInstanceRepository.save(instance);
        }

        // Actualizar el stock actual
        tool.setCurrentStock(tool.getCurrentStock() + quantity);
        return toolRepository.save(tool);
    }

    @Transactional
    public ToolEntity decommissionTool(Long toolId, Long instanceId) {
        if (instanceId == null) {
            throw new IllegalArgumentException("ID de instancia es requerido");
        }

        ToolEntity tool = toolRepository.findById(toolId)
                .orElseThrow(() -> new RuntimeException("Herramienta no encontrada"));

        ToolInstanceEntity instance = toolInstanceRepository.findById(instanceId)
                .orElseThrow(() -> new RuntimeException("Instancia no encontrada"));

        if (!instance.getTool().getId().equals(toolId)) {
            throw new IllegalArgumentException("La instancia no pertenece a esta herramienta");
        }

        if (instance.isLoaned()) {
            throw new IllegalArgumentException("No se puede dar de baja una instancia en préstamo");
        }

        if (instance.isDecommissioned()) {
            throw new IllegalArgumentException("La instancia ya está dada de baja");
        }

        // Cambiar el estado de la instancia
        instance.setStatus(ToolInstanceEntity.ToolInstanceStatus.DECOMMISSIONED);
        toolInstanceRepository.save(instance);

        // Actualizar el stock actual
        if (tool.getCurrentStock() > 0) {
            tool.setCurrentStock(tool.getCurrentStock() - 1);
            toolRepository.save(tool);
        }

        return tool;
    }

    private void validateToolData(ToolEntity tool) {
        if (tool.getName() == null || tool.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la herramienta es requerido");
        }
        if (tool.getCategory() == null || tool.getCategory().getId() == null) {
            throw new IllegalArgumentException("La categoría es requerida");
        }
        if (tool.getInitialStock() == null || tool.getInitialStock() < 1) {
            throw new IllegalArgumentException("El stock inicial debe ser al menos 1");
        }
        if (tool.getReplacementValue() == null || tool.getReplacementValue().doubleValue() <= 0) {
            throw new IllegalArgumentException("El valor de reposición debe ser mayor a 0");
        }
        if (tool.getRentalRate() == null || tool.getRentalRate().doubleValue() <= 0) {
            throw new IllegalArgumentException("La tarifa de arriendo debe ser mayor a 0");
        }
    }
}

