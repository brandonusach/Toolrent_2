package com.toolrent.msinventory.services;

import com.toolrent.msinventory.entities.ToolInstanceEntity;
import com.toolrent.msinventory.entities.ToolInstanceEntity.ToolInstanceStatus;
import com.toolrent.msinventory.entities.ToolEntity;
import com.toolrent.msinventory.repositories.ToolInstanceRepository;
import com.toolrent.msinventory.repositories.ToolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class ToolInstanceService {

    @Autowired
    private ToolInstanceRepository toolInstanceRepository;

    @Autowired
    private ToolRepository toolRepository;

    public List<ToolInstanceEntity> getAllInstances() {
        return toolInstanceRepository.findAll();
    }

    public ToolInstanceEntity getInstanceById(Long id) {
        return toolInstanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Instancia no encontrada con id: " + id));
    }

    public List<ToolInstanceEntity> getInstancesByToolId(Long toolId) {
        return toolInstanceRepository.findByToolId(toolId);
    }

    public List<ToolInstanceEntity> getAvailableInstancesByToolId(Long toolId) {
        return toolInstanceRepository.findByToolIdAndStatus(toolId, ToolInstanceStatus.AVAILABLE);
    }

    public Long countAvailableInstancesByToolId(Long toolId) {
        return toolInstanceRepository.countByToolIdAndStatus(toolId, ToolInstanceStatus.AVAILABLE);
    }

    @Transactional
    public ToolInstanceEntity createInstance(Long toolId) {
        ToolEntity tool = toolRepository.findById(toolId)
                .orElseThrow(() -> new RuntimeException("Herramienta no encontrada"));

        ToolInstanceEntity instance = new ToolInstanceEntity(tool);
        return toolInstanceRepository.save(instance);
    }

    @Transactional
    public ToolInstanceEntity updateInstanceStatus(Long id, ToolInstanceStatus status) {
        ToolInstanceEntity instance = getInstanceById(id);
        instance.setStatus(status);
        return toolInstanceRepository.save(instance);
    }

    @Transactional
    public void deleteInstance(Long id) {
        if (!toolInstanceRepository.existsById(id)) {
            throw new RuntimeException("Instancia no encontrada");
        }
        toolInstanceRepository.deleteById(id);
    }

    public List<ToolInstanceEntity> getInstancesByStatus(ToolInstanceStatus status) {
        return toolInstanceRepository.findByStatus(status);
    }
}

