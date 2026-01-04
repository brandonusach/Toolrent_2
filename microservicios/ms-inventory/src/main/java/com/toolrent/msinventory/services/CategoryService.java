package com.toolrent.msinventory.services;

import com.toolrent.msinventory.entities.CategoryEntity;
import com.toolrent.msinventory.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public List<CategoryEntity> getAllCategories() {
        return categoryRepository.findAll();
    }

    public CategoryEntity getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada con id: " + id));
    }

    @Transactional
    public CategoryEntity saveCategory(CategoryEntity category) {
        if (category.getName() == null || category.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la categoría es requerido");
        }
        if (category.getId() == null && categoryRepository.existsByNameIgnoreCase(category.getName())) {
            throw new IllegalArgumentException("Ya existe una categoría con este nombre");
        }
        return categoryRepository.save(category);
    }

    @Transactional
    public CategoryEntity updateCategory(Long id, CategoryEntity category) {
        CategoryEntity existing = getCategoryById(id);
        existing.setName(category.getName());
        existing.setDescription(category.getDescription());
        return categoryRepository.save(existing);
    }

    @Transactional
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new RuntimeException("Categoría no encontrada con id: " + id);
        }
        categoryRepository.deleteById(id);
    }

    public List<CategoryEntity> searchCategories(String name) {
        return categoryRepository.findByNameContainingIgnoreCase(name);
    }
}

