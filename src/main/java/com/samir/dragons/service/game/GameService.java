package com.samir.dragons.service.game;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.samir.dragons.client.GameApiClient;
import com.samir.dragons.model.GameState;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class GameService {

	private final GameApiClient client;
	private final GameLoopManager gameLoopManager;

	Logger summaryLogger = LoggerFactory.getLogger("summary");

	public void runGameLoop() {
		GameState gameState = client.startNewGame();
		summaryLogger.info("⌛ New game started: {}. Wait for results.", gameState.getGameId());

		gameLoopManager.run(gameState);

		printGameSummary(gameState);
	}

	private void printGameSummary(GameState gameState) {
		log.info("🏁 Game Finished: {}", gameState.getGameId());
		log.info("📈 Final Score: {}", gameState.getScore());
		log.info("🔁 Turns Played: {}", gameState.getTurn());

		summaryLogger.info("\n🎯️ Game Finished: {}, Final Score {}", gameState.getGameId(), gameState.getScore());
	}
}