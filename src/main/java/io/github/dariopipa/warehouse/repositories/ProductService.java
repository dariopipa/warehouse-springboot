package io.github.dariopipa.warehouse.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.dariopipa.warehouse.entities.Product;

public interface ProductService extends JpaRepository<Product, Long>{
	
}
