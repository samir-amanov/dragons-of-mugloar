package com.samir.dragons.service.game;

import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import com.samir.dragons.client.GameApiClient;
import com.samir.dragons.model.Ad;
import com.samir.dragons.model.GameState;
import com.samir.dragons.model.SolveResult;
import com.samir.dragons.service.ads.AdDecryptionService;
import com.samir.dragons.service.ads.AdQueueManager;
import com.samir.dragons.service.purchase.PurchaseService;

@Service
@RequiredArgsConstructor
@Slf4j
public class GameLoopManager {

	private final GameApiClient client;
	private final AdDecryptionService adDecryptionService;
	private final AdQueueManager adQueueManager;
	private final PurchaseService purchaseService;

	public void run(GameState gameState) {
		Set<String> alreadyTriedAdIds = new HashSet<>();

		while (!gameState.isGameOver()) {
			try {
				List<Ad> fetchedAds = client.fetchMessages(gameState.getGameId()).stream()
						.filter(ad -> !alreadyTriedAdIds.contains(ad.getAdId()))
						.toList();

				List<Ad> encryptedAds = fetchedAds.stream()
						.filter(ad -> ad.getEncrypted() != null && ad.getEncrypted() == 1)
						.toList();

				List<Ad> plainAds = fetchedAds.stream()
						.filter(ad -> ad.getEncrypted() == null)
						.toList();

				List<Ad> decryptedAds = adDecryptionService.decryptAds(encryptedAds);

				List<Ad> allAds = new ArrayList<>(plainAds);
				allAds.addAll(decryptedAds);

				Deque<Ad> adQueue = adQueueManager.buildAdQueue(allAds);

				while (!adQueue.isEmpty()) {
					Ad ad = adQueue.removeFirst();
					alreadyTriedAdIds.add(ad.getAdId());

					log.info("➡️ Solving ad: [{}] '{}' with reward {} and probability '{}'",
							ad.getAdId(), ad.getMessage(), ad.getReward(), ad.getProbability());

					purchaseService.tryBuyItem(gameState);
					SolveResult result = client.solveAd(gameState.getGameId(), ad.getAdId());

					if (result != null) {
						gameState.updateFrom(result);
						log.info("✅ Success {}, Score: {}, Lives: {}, Message: {}, Gold {}",
								result.isSuccess(), result.getScore(), result.getLives(), result.getMessage(), result.getGold());
					}

					if (gameState.isGameOver())
						break;
				}
			} catch (Exception e) {
				log.error("❌ Exception during game loop", e);
			}
		}
	}
}