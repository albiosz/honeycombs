package com.albiosz.honeycombs;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

@SpringBootApplication
public class HoneycombsApplication {

	public static void main(String[] args) {
		DBCheck dbCheck = new DBCheck();
		dbCheck.waitUntilDBConnectionIsAvailable();
		SpringApplication.run(HoneycombsApplication.class, args);
	}

	@Bean
	@Profile("!test")
	CommandLineRunner setUpDatabase(DBConfig dbConfig) {
		return args -> dbConfig.initDB();
	}
}
