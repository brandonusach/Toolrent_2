package com.toolrent.msinventory.services;

import com.toolrent.msinventory.entities.CategoryEntity;
import com.toolrent.msinventory.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryService implements CommandLineRunner {

    @Autowired
    private CategoryRepository categoryRepository;

    // IMPLEMENTACIÓN DE CommandLineRunner PARA INICIALIZACIÓN AUTOMÁTICA
    @Override
    public void run(String... args) throws Exception {
        System.out.println("=== INICIALIZANDO CATEGORÍAS POR DEFECTO ===");
        initializeDefaultCategories();
        System.out.println("=== CATEGORÍAS INICIALIZADAS ===");
    }

    // MÉTODO DE INICIALIZACIÓN DE CATEGORÍAS POR DEFECTO
    private void initializeDefaultCategories() {
        // Lista de categorías por defecto
        String[][] defaultCategories = {
                {"Herramientas de Corte", "Sierras, cortadoras, amoladoras, etc."},
                {"Herramientas de Perforación", "Taladros, rotomartillos, brocas, etc."},
                {"Herramientas de Medición", "Niveles, metros, reglas, escuadras, etc."},
                {"Herramientas Manuales", "Martillos, destornilladores, llaves, alicates, etc."},
                {"Herramientas de Jardín", "Podadoras, cortacéspedes, motosierras, etc."},
                {"Equipos de Limpieza", "Hidrolavadoras, aspiradoras industriales, pulidoras, etc."},
                {"Maquinaria Pesada", "Mezcladoras, compactadores, generadores, etc."},
                {"Herramientas Eléctricas", "Soldadoras, compresores, herramientas neumáticas, etc."},
                {"Herramientas de Fontanería", "Llaves de tubo, destapadores, prensas, etc."},
                {"Herramientas de Electricidad", "Multímetros, pelacables, crimpadoras, etc."},
                {"Herramientas de Pintura", "Compresores de pintura, rodillos, brochas profesionales, etc."},
                {"Equipos de Elevación", "Poleas, tecles, gatos hidráulicos, escaleras, etc."},
                {"Herramientas de Transporte", "Carretillas, diablitos, plataformas rodantes, etc."},
                {"Equipos de Seguridad", "Cascos, arneses, guantes de seguridad, etc."},
                {"Herramientas de Demolición", "Martillos neumáticos, cinceles, barras, etc."}
        };

        int created = 0;
        int existing = 0;

        for (String[] categoryData : defaultCategories) {
            String name = categoryData[0];
            String description = categoryData[1];

            if (!categoryRepository.existsByNameIgnoreCase(name)) {
                CategoryEntity category = new CategoryEntity();
                category.setName(name);
                category.setDescription(description);

                try {
                    categoryRepository.save(category);
                    created++;
                    System.out.println("✓ Categoría creada: " + name);
                } catch (Exception e) {
                    System.err.println("✗ Error creando categoría '" + name + "': " + e.getMessage());
                }
            } else {
                existing++;
            }
        }

        System.out.println("Categorías creadas: " + created);
        System.out.println("Categorías existentes: " + existing);
        System.out.println("Total categorías en sistema: " + categoryRepository.count());
    }

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

