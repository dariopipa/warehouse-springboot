package io.github.dariopipa.warehouse.repositories;

import io.github.dariopipa.warehouse.entities.ProductType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductTypeRepository extends JpaRepository<ProductType, Long> {
    
    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Long id);
}
