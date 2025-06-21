package com.samir.dragons.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.samir.dragons.client.GameApiClient;
import com.samir.dragons.model.GameState;
import com.samir.dragons.model.PurchaseResult;
import com.samir.dragons.model.ShopItem;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PurchaseService {

	private final GameApiClient client;

	public PurchaseService(GameApiClient client) {
		this.client = client;
	}

	public void tryBuyHealingPotion(GameState gameState) {
		if (gameState.getLives() <= 2 && gameState.getGold() >= 50) {
			tryBuyItem(gameState, "hpot");
		}
	}

	public void tryBuyTricks(GameState gameState) {
		if (gameState.getGold() >= 250) {
			tryBuyItem(gameState, "tricks");
		}
	}

	private void tryBuyItem(GameState gameState, String itemId) {
		List<ShopItem> items = client.fetchShopItems(gameState.getGameId());
		items.stream()
				.filter(item -> itemId.equals(item.getId()) && item.getCost() <= gameState.getGold())
				.findFirst()
				.ifPresent(item -> {
					PurchaseResult result = client.buyItem(gameState.getGameId(), item.getId());
					log.info("🛍️Bought item '{}', remaining gold: {}", item.getName(), result.getGold());
					gameState.updateFrom(result);
				});
	}
}
