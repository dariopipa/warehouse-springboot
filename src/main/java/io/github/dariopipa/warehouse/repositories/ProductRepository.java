package io.github.dariopipa.warehouse.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import io.github.dariopipa.warehouse.entities.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

	@Modifying
	@Query("UPDATE Product p SET p.quantity = p.quantity + :delta WHERE p.id = :id")
	void updateQuantityById(@Param("id") Long id, @Param("delta") int delta);

	boolean existsByName(String name);

	boolean existsBySku(String sku);
}
