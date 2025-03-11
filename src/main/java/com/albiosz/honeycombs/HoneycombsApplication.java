package com.albiosz.honeycombs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

@SpringBootApplication
public class HoneycombsApplication {

	private static final Logger logger = LoggerFactory.getLogger("com.example.useractivity");

	public static void main(String[] args) {
		DBCheck dbCheck = new DBCheck();
		dbCheck.waitUntilDBConnectionIsAvailable();
		SpringApplication.run(HoneycombsApplication.class, args);
	}

	@Bean
	@Profile("!test")
	CommandLineRunner setUpDatabase(DBConfig dbConfig) {
		logger.info("LOADING SEEDS TO DB");
		return args -> dbConfig.initDB();
	}
}
