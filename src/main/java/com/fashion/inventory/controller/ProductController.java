package com.fashion.inventory.controller;

import com.fashion.inventory.model.Product;
import com.fashion.inventory.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @GetMapping
    public List<Product> getProducts(
            @RequestParam(required = false) String category, 
            @RequestParam(required = false) String subcategory, 
            @RequestParam(required = false) String search) {
        
        if (category != null && subcategory != null && search != null && !search.isEmpty()) {
            return productRepository.findByCategoryAndSubcategoryAndNameContainingIgnoreCase(category, subcategory, search);
        } else if (category != null && subcategory != null) {
            return productRepository.findByCategoryAndSubcategory(category, subcategory);
        } else if (category != null && search != null && !search.isEmpty()) {
            return productRepository.findByCategoryAndNameContainingIgnoreCase(category, search);
        } else if (category != null) {
            return productRepository.findByCategory(category);
        } else if (search != null && !search.isEmpty()) {
            return productRepository.findByNameContainingIgnoreCase(search);
        }
        return productRepository.findAll();
    }

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            String orig = file.getOriginalFilename();
            String filename = System.currentTimeMillis() + "_" + (orig != null ? orig.replaceAll("[^a-zA-Z0-9\\.\\-]", "_") : "upload.jpg");
            Path path = Paths.get("data/static/images/" + filename);
            Files.createDirectories(path.getParent());
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            return ResponseEntity.ok(Map.of("url", "/images/" + filename));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<Product> addProduct(@RequestBody Product product) {
        if(product.getStock() == null) product.setStock(0);
        Product saved = productRepository.save(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product productDetails) {
        return productRepository.findById(id).map(product -> {
            product.setName(productDetails.getName());
            product.setCategory(productDetails.getCategory());
            product.setSubcategory(productDetails.getSubcategory());
            product.setStock(productDetails.getStock());
            product.setImageUrl(productDetails.getImageUrl());
            return ResponseEntity.ok(productRepository.save(product));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        return productRepository.findById(id).map(product -> {
            productRepository.delete(product);
            return ResponseEntity.ok().build();
        }).orElse(ResponseEntity.notFound().build());
    }
}
