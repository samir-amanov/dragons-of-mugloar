package com.samir.dragons.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.samir.dragons.client.GameApiClient;
import com.samir.dragons.model.Ad;
import com.samir.dragons.model.GameState;
import com.samir.dragons.model.SolveResult;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class GameService {

	private final GameApiClient client;
	private final AdSelectionService adSelectionService;
	private final PurchaseService purchaseService;

	public void runGameLoop() {
		GameState gameState = client.startNewGame();
		log.info("🎯 New game started: {}", gameState.getGameId());

		while (!gameState.isGameOver()) {
			try {
				purchaseService.tryBuyHealingPotion(gameState);

				List<Ad> ads = client.fetchMessages(gameState.getGameId());
				var bestAdOpt = adSelectionService.pickBestAd(ads);

				if (bestAdOpt.isPresent()) {
					var ad = bestAdOpt.get();
					log.info("➡️ Solving ad: [{}] '{}' with reward {} and probability '{}'",
							ad.getAdId(), ad.getMessage(), ad.getReward(), ad.getProbability());

					SolveResult result = client.solveAd(gameState.getGameId(), ad.getAdId());
					log.info("✅ Success: {}, Score: {}, Lives: {}, Message: {}",
							result.isSuccess(), result.getScore(), result.getLives(), result.getMessage());

					gameState.updateFrom(result);
				} else {
					log.warn("⚠️ No suitable ads found, skipping turn.");
				}
			} catch (Exception e) {
				log.error("❌ Exception during game loop: ", e);
			}
		}

		printGameSummary(gameState);
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