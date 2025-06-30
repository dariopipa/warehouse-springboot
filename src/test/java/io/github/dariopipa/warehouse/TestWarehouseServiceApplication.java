package io.github.dariopipa.warehouse;

import org.springframework.boot.SpringApplication;

public class TestWarehouseServiceApplication {

	public static void main(String[] args) {
		SpringApplication.from(WarehouseServiceApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
