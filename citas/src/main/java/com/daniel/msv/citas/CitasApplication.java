package com.daniel.msv.citas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Microservicios: 6 servicios, 5 caídos. El que funciona es este.
@SpringBootApplication(scanBasePackages = {"com.daniel.msv.citas", "com.daniel.commons"})
public class CitasApplication {

	public static void main(String[] args) {
		SpringApplication.run(CitasApplication.class, args);
	}

}
