package io.github.dariopipa.warehouse.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import io.github.dariopipa.warehouse.entities.ProductType;

public interface ProductTypeRepository  extends JpaRepository<ProductType, Long>{
	boolean existsByName(String name);
	boolean existsByNameAndIdNot(String name, Long id);
}
