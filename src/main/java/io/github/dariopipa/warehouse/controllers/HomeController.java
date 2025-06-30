package io.github.dariopipa.warehouse.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

	@GetMapping("")
	public String homeController() {
		return "HEALTHY";
	}
	
}
