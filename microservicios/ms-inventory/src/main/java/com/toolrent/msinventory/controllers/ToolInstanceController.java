package com.toolrent.msinventory.controllers;

import com.toolrent.msinventory.entities.ToolInstanceEntity;
import com.toolrent.msinventory.entities.ToolInstanceEntity.ToolInstanceStatus;
import com.toolrent.msinventory.services.ToolInstanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/tool-instances")
@CrossOrigin("*")
public class ToolInstanceController {

    @Autowired
    private ToolInstanceService toolInstanceService;

    @GetMapping("/")
    public ResponseEntity<List<ToolInstanceEntity>> getAllInstances() {
        List<ToolInstanceEntity> instances = toolInstanceService.getAllInstances();
        return ResponseEntity.ok(instances);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ToolInstanceEntity> getInstanceById(@PathVariable Long id) {
        try {
            ToolInstanceEntity instance = toolInstanceService.getInstanceById(id);
            return ResponseEntity.ok(instance);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/tool/{toolId}")
    public ResponseEntity<List<ToolInstanceEntity>> getInstancesByToolId(@PathVariable Long toolId) {
        List<ToolInstanceEntity> instances = toolInstanceService.getInstancesByToolId(toolId);
        return ResponseEntity.ok(instances);
    }

    @GetMapping("/tool/{toolId}/available")
    public ResponseEntity<List<ToolInstanceEntity>> getAvailableInstancesByToolId(@PathVariable Long toolId) {
        List<ToolInstanceEntity> instances = toolInstanceService.getAvailableInstancesByToolId(toolId);
        return ResponseEntity.ok(instances);
    }

    @GetMapping("/tool/{toolId}/available/count")
    public ResponseEntity<Long> countAvailableInstancesByToolId(@PathVariable Long toolId) {
        Long count = toolInstanceService.countAvailableInstancesByToolId(toolId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<ToolInstanceEntity>> getInstancesByStatus(@PathVariable ToolInstanceStatus status) {
        List<ToolInstanceEntity> instances = toolInstanceService.getInstancesByStatus(status);
        return ResponseEntity.ok(instances);
    }

    @PostMapping("/tool/{toolId}")
    public ResponseEntity<?> createInstance(@PathVariable Long toolId) {
        try {
            ToolInstanceEntity instance = toolInstanceService.createInstance(toolId);
            return ResponseEntity.ok(instance);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateInstanceStatus(@PathVariable Long id, @RequestParam ToolInstanceStatus status) {
        try {
            ToolInstanceEntity updated = toolInstanceService.updateInstanceStatus(id, status);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteInstance(@PathVariable Long id) {
        try {
            toolInstanceService.deleteInstance(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

