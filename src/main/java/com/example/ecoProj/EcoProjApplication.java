package com.example.ecoProj;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class EcoProjApplication {

	public static void main(String[] args) {
		SpringApplication.run(EcoProjApplication.class, args);
	}

}
