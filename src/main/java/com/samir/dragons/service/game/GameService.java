package com.samir.dragons.service.game;

import static com.samir.dragons.util.Printer.printGameSummary;
import static com.samir.dragons.util.Printer.printStart;

import org.springframework.stereotype.Service;

import com.samir.dragons.client.GameApiClient;
import com.samir.dragons.model.GameState;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GameService {

	private final GameApiClient client;
	private final GameLoopManager gameLoopManager;

	public GameState runGameLoop() {
		GameState gameState = client.startNewGame();
		printStart(gameState);

		gameLoopManager.run(gameState);

		printGameSummary(gameState);
		return gameState;
	}
}