package io.github.dariopipa.warehouse.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/products")
public class ProductsController {

	
	@GetMapping("")
	public String getStrings() {
		return "";
	}
}
