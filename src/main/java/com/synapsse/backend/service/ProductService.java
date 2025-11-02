package com.synapsse.backend.service;

import com.synapsse.backend.dto.ProductRequest;
import com.synapsse.backend.entity.Product;
import com.synapsse.backend.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public Page<Product> listAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public List<Product> listAll() {
        return productRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Product getById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado"));
    }

    @Transactional
    public Product create(ProductRequest request) {
        Product product = new Product();
        applyRequest(product, request);
        return productRepository.save(product);
    }

    @Transactional
    public Product update(Long id, ProductRequest request) {
        Product product = getById(id);
        applyRequest(product, request);
        return productRepository.save(product);
    }

    @Transactional
    public void delete(Long id) {
        if (!productRepository.existsById(id)) {
            throw new EntityNotFoundException("Producto no encontrado");
        }
        productRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Page<Product> search(String query, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        if (query == null || query.isBlank()) {
            return productRepository.findAll(pageable);
        }
        return productRepository.searchPaged(query, pageable);
    }

    @Transactional(readOnly = true)
    public List<Product> search(String query) {
        if (query == null || query.isBlank()) {
            return listAll();
        }
        return productRepository.search(query);
    }

    private void applyRequest(Product product, ProductRequest request) {
        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setStock(request.stock());
        product.setImageUrl(request.imageUrl());
        if (request.categories() == null || request.categories().isEmpty()) {
            product.getCategories().clear();
        } else {
            product.getCategories().clear();
            product.getCategories().addAll(request.categories());
        }
    }
}
