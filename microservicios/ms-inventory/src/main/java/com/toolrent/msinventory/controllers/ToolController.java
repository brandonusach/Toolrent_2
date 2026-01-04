package com.toolrent.msinventory.controllers;

import com.toolrent.msinventory.entities.ToolEntity;
import com.toolrent.msinventory.services.ToolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/tools")
@CrossOrigin("*")
public class ToolController {

    @Autowired
    private ToolService toolService;

    @GetMapping("/")
    public ResponseEntity<List<ToolEntity>> getAllTools() {
        List<ToolEntity> tools = toolService.getAllTools();
        return ResponseEntity.ok(tools);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ToolEntity> getToolById(@PathVariable Long id) {
        return toolService.getToolById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/")
    public ResponseEntity<?> createTool(@RequestBody ToolEntity tool) {
        try {
            ToolEntity newTool = toolService.createTool(tool);
            return ResponseEntity.ok(newTool);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/")
    public ResponseEntity<?> updateTool(@RequestBody ToolEntity tool) {
        try {
            ToolEntity updated = toolService.updateTool(tool.getId(), tool);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTool(@PathVariable Long id) {
        try {
            toolService.deleteTool(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<ToolEntity>> searchTools(@RequestParam String name) {
        List<ToolEntity> tools = toolService.searchToolsByName(name);
        return ResponseEntity.ok(tools);
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ToolEntity>> getToolsByCategory(@PathVariable Long categoryId) {
        List<ToolEntity> tools = toolService.getToolsByCategory(categoryId);
        return ResponseEntity.ok(tools);
    }

    @PutMapping("/{id}/stock")
    public ResponseEntity<?> updateStock(@PathVariable Long id, @RequestParam Integer stock) {
        try {
            ToolEntity updated = toolService.updateStock(id, stock);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

