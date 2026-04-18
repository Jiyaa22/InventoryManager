package com.fashion.inventory.repository;

import com.fashion.inventory.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategory(String category);
    List<Product> findByNameContainingIgnoreCase(String name);
    List<Product> findByCategoryAndNameContainingIgnoreCase(String category, String name);
    List<Product> findByCategoryAndSubcategory(String category, String subcategory);
    List<Product> findByCategoryAndSubcategoryAndNameContainingIgnoreCase(String category, String subcategory, String name);
}
