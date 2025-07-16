package io.github.dariopipa.warehouse.repositories;

import io.github.dariopipa.warehouse.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsByName(String name);

    boolean existsBySku(String sku);
}
