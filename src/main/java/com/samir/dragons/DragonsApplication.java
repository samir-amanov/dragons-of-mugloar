package com.samir.dragons;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.samir.dragons.service.GameService;

@SpringBootApplication
public class DragonsApplication {

	public static void main(String[] args) {
		SpringApplication.run(DragonsApplication.class, args);
	}

	@Bean
	public CommandLineRunner runGame(GameService gameService) {
		return args -> {
			gameService.runGameLoop();
		};
	}
}