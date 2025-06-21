package com.samir.dragons.service;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;
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
public class GameService implements ApplicationContextAware {

	private final GameApiClient client;
	private final AdSelectionService adSelectionService;
	private final PurchaseService purchaseService;

	private ApplicationContext context;

	@Override
	public void setApplicationContext(@NonNull ApplicationContext applicationContext) {
		this.context = applicationContext;
	}

	public void runGameLoop() {
		GameState gameState = client.startNewGame();
		log.info("🎯 New game started: {}", gameState.getGameId());

		Set<String> solvedAdIds = new HashSet<>();

		while (!gameState.isGameOver()) {
			try {
				List<Ad> ads = client.fetchMessages(gameState.getGameId()).stream()
						.filter(ad -> !solvedAdIds.contains(ad.getAdId()))
						.toList();

				if (ads.stream().anyMatch(ad -> ad.getEncrypted() != null)) {
					purchaseService.tryBuyTricks(gameState);
				}

				Deque<Ad> adQueue = selectBestAvailableAds(ads);

				while (!adQueue.isEmpty()) {
					Ad ad = adQueue.removeFirst();

					log.info("➡️ Solving ad: [{}] '{}' with reward {} and probability '{}'",
							ad.getAdId(), ad.getMessage(), ad.getReward(), ad.getProbability());

					purchaseService.tryBuyHealingPotion(gameState);
					SolveResult result = client.solveAd(gameState.getGameId(), ad.getAdId());

					if (result != null) {
						gameState.updateFrom(result);
						log.info("✅ Success {}, Score: {}, Lives: {}, Message: {}, Gold {}",
								result.isSuccess(), result.getScore(), result.getLives(), result.getMessage(), result.getGold());
					}

					solvedAdIds.add(ad.getAdId());

					if (gameState.isGameOver()) {
						break;
					}
				}
			} catch (Exception e) {
				log.error("❌ Exception during game loop: ", e);
			}
		}

		printGameSummary(gameState);
		SpringApplication.exit(context, () -> 0);
	}

	private Deque<Ad> selectBestAvailableAds(List<Ad> ads) {
		List<Ad> selected = adSelectionService.selectAdsWithHighProbability(ads);
		if (!selected.isEmpty()) {
			return new ArrayDeque<>(selected);
		}

		selected = adSelectionService.selectAdsWithModerateProbability(ads);
		if (!selected.isEmpty()) {
			return new ArrayDeque<>(selected);
		}

		selected = adSelectionService.selectAdsWithLessProbability(ads);
		if (!selected.isEmpty()) {
			return new ArrayDeque<>(selected);
		}

		log.warn("⚠️ No ads found after all selection attempts.");
		return new ArrayDeque<>();
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