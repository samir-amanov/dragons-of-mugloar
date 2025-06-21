package com.samir.dragons.service.game;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.samir.dragons.client.GameApiClient;
import com.samir.dragons.model.GameState;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class GameService implements ApplicationContextAware {

	private final GameApiClient client;
	private final GameLoopManager gameLoopManager;

	private ApplicationContext context;

	@Override
	public void setApplicationContext(@NonNull ApplicationContext applicationContext) {
		this.context = applicationContext;
	}

	public void runGameLoop() {
		GameState gameState = client.startNewGame();
		log.info("🎯 New game started: {}", gameState.getGameId());

		gameLoopManager.run(gameState);

		printGameSummary(gameState);
		SpringApplication.exit(context, () -> 0);
	}

	private void printGameSummary(GameState gameState) {
		log.info("🏁 Game Over");
		log.info("📈 Final Score: {}", gameState.getScore());
		log.info("🎖️ High Score: {}", gameState.getHighScore());
		log.info("💰 Gold Left: {}", gameState.getGold());
		log.info("🩸 Lives Remaining: {}", gameState.getLives());
		log.info("🔁 Turns Played: {}", gameState.getTurn());
	}
}