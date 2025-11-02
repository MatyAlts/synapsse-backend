package com.synapsse.backend.repository;

import com.synapsse.backend.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(COALESCE(p.description, '')) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Product> search(@Param("query") String query);
    
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(COALESCE(p.description, '')) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Product> searchPaged(@Param("query") String query, Pageable pageable);
}
