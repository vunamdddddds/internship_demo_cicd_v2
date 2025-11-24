package com.example.InternShip;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling

public class Main {

	public static void main(String[] args) {
		// Load .env
        Dotenv dotenv = Dotenv.load();
        
        // Set tất cả biến vào System properties để Spring đọc được
        dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));
		SpringApplication.run(Main.class, args);
       
	}
}
