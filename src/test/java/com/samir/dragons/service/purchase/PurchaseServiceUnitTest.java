package com.samir.dragons.service.purchase;

import com.samir.dragons.client.GameApiClient;
import com.samir.dragons.model.GameState;
import com.samir.dragons.model.PurchaseResult;
import com.samir.dragons.model.ShopItem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PurchaseServiceUnitTest {

	private GameApiClient gameApiClient;
	private PurchaseService purchaseService;

	@BeforeEach
	void setUp() {
		gameApiClient = mock(GameApiClient.class);
		purchaseService = new PurchaseService(gameApiClient);
	}

	@Test
	void testBuyHealingPotion_whenLowLivesAndEnoughGold() {
		GameState gameState = GameState.builder()
				.gameId("game123")
				.lives(2)
				.gold(60)
				.build();

		ShopItem hpot = ShopItem.builder()
				.id("hpot")
				.name("Healing Potion")
				.cost(50)
				.build();

		PurchaseResult result = PurchaseResult.builder()
				.gold(10)
				.lives(4)
				.build();

		when(gameApiClient.fetchShopItems(gameState.getGameId())).thenReturn(List.of(hpot));
		when(gameApiClient.buyItem(gameState.getGameId(), hpot.getId())).thenReturn(result);

		purchaseService.tryBuyItem(gameState);

		assertEquals(10, gameState.getGold());
		assertEquals(4, gameState.getLives());
	}

	@Test
	void testBuyWingPotion_whenEnoughGold_shouldUpdateGold() {
		GameState gameState = GameState.builder()
				.gameId("game123")
				.lives(5)
				.gold(300)
				.build();

		ShopItem wing = ShopItem.builder()
				.id("wingpot")
				.name("Potion of Stronger Wings")
				.cost(100)
				.build();

		PurchaseResult result = PurchaseResult.builder()
				.gold(200)
				.lives(5)
				.build();

		when(gameApiClient.fetchShopItems(gameState.getGameId())).thenReturn(List.of(wing));
		when(gameApiClient.buyItem(gameState.getGameId(), wing.getId())).thenReturn(result);

		purchaseService.tryBuyItem(gameState);

		assertEquals(200, gameState.getGold());
		assertEquals(5, gameState.getLives());
	}

	@Test
	void testNoPurchase_whenNotEnoughGold() {
		GameState gameState = GameState.builder()
				.gameId("game123")
				.lives(3)
				.gold(30)
				.build();

		when(gameApiClient.fetchShopItems(gameState.getGameId())).thenReturn(List.of());

		purchaseService.tryBuyItem(gameState);

		verify(gameApiClient, never()).fetchShopItems(gameState.getGameId());
		verify(gameApiClient, never()).buyItem(any(), any());
	}

	@Test
	void testNoPurchase_whenNoItemsMatchCriteria() {
		GameState gameState = GameState.builder()
				.gameId("game123")
				.lives(5)
				.gold(500)
				.build();

		ShopItem irrelevantItem = ShopItem.builder()
				.id("nonmatch")
				.name("Fake Item")
				.cost(800)
				.build();

		when(gameApiClient.fetchShopItems(gameState.getGameId())).thenReturn(List.of(irrelevantItem));

		purchaseService.tryBuyItem(gameState);

		verify(gameApiClient, times(1)).fetchShopItems(gameState.getGameId());
		verify(gameApiClient, never()).buyItem(any(), any());
	}

	@Test
	void testNoPurchase_whenItemPriceChanged() {
		GameState gameState = GameState.builder()
				.gameId("game123")
				.lives(5)
				.gold(500)
				.build();

		ShopItem wing = ShopItem.builder()
				.id("wingpot")
				.name("Potion of Stronger Wings")
				.cost(800)
				.build();

		when(gameApiClient.fetchShopItems(gameState.getGameId())).thenReturn(List.of(wing));

		purchaseService.tryBuyItem(gameState);

		verify(gameApiClient, times(1)).fetchShopItems(gameState.getGameId());
		verify(gameApiClient, never()).buyItem(any(), any());
	}
}