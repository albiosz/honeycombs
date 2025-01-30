package com.albiosz.honeycombs;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class HoneycombsApplication {

	public static void main(String[] args) {
		SpringApplication.run(HoneycombsApplication.class, args);
	}

	@Bean
	CommandLineRunner setUpDatabase(DBConfig dbConfig) {
		return args -> dbConfig.initDB();
	}
}
