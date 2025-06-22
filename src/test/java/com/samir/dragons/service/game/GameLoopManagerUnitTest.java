package com.samir.dragons.service.game;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.samir.dragons.client.GameApiClient;
import com.samir.dragons.model.Ad;
import com.samir.dragons.model.GameState;
import com.samir.dragons.model.SolveResult;
import com.samir.dragons.service.ads.AdDecryptionService;
import com.samir.dragons.service.ads.AdQueueManager;
import com.samir.dragons.service.purchase.PurchaseService;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class GameLoopManagerTest {

	@Mock
	private GameApiClient client;

	@Mock
	private AdDecryptionService adDecryptionService;

	@Mock
	private AdQueueManager adQueueManager;

	@Mock
	private PurchaseService purchaseService;

	@InjectMocks
	private GameLoopManager gameLoopManager;

	@Mock
	private GameState gameState;

	private final String gameId = "game123";

	private Ad plainAd;
	private Ad encryptedAd;

	private SolveResult solveResult;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		plainAd = Ad.builder().adId("plainAd").encrypted(null).message("Plain Ad").reward(10).probability("high").build();
		encryptedAd = Ad.builder().adId("encryptedAd").encrypted(1).message("Encrypted Ad").reward(20).probability("low").build();

		solveResult = SolveResult.builder()
				.success(true)
				.score(100)
				.lives(3)
				.message("Solved!")
				.gold(50)
				.build();

		when(gameState.getGameId()).thenReturn(gameId);
	}

	@Test
	void run_shouldHandleEmptyAdListCorrectly() {
		when(gameState.isGameOver()).thenReturn(false, true);
		when(client.fetchMessages(gameId)).thenReturn(List.of());
		when(adDecryptionService.decryptAds(List.of())).thenReturn(List.of());
		when(adQueueManager.buildAdQueue(List.of())).thenReturn(new ArrayDeque<>());

		gameLoopManager.run(gameState);

		verify(client, times(1)).fetchMessages(gameId);
		verify(adDecryptionService, times(1)).decryptAds(List.of());
		verify(adQueueManager, times(1)).buildAdQueue(List.of());
		verify(purchaseService, never()).tryBuyItem(any());
		verify(client, never()).solveAd(any(), any());
		verify(gameState, never()).updateFrom((SolveResult) any());
	}

	@Test
	void run_shouldFilterAlreadySolvedAds() {

		when(gameState.isGameOver()).thenReturn(false, false, true);

		when(client.fetchMessages(gameId))
				.thenReturn(List.of(plainAd, encryptedAd))
				.thenReturn(List.of(plainAd, encryptedAd));

		Ad decryptedAd = Ad.builder()
				.adId("encryptedAd")
				.encrypted(0)
				.message("Decrypted Ad")
				.reward(20)
				.probability("low")
				.build();
		when(adDecryptionService.decryptAds(List.of(encryptedAd)))
				.thenReturn(List.of(decryptedAd))
				.thenReturn(List.of(decryptedAd));

		Deque<Ad> firstQueue = new ArrayDeque<>();
		firstQueue.add(plainAd);
		firstQueue.add(decryptedAd);

		Deque<Ad> secondQueue = new ArrayDeque<>();
		secondQueue.add(decryptedAd);

		when(adQueueManager.buildAdQueue(List.of(plainAd, decryptedAd))).thenReturn(firstQueue);
		when(adQueueManager.buildAdQueue(List.of(decryptedAd))).thenReturn(secondQueue);

		doNothing().when(purchaseService).tryBuyItem(gameState);

		when(client.solveAd(gameId, "plainAd")).thenReturn(solveResult);
		when(client.solveAd(gameId, "encryptedAd")).thenReturn(null);

		gameLoopManager.run(gameState);

		verify(client, times(1)).fetchMessages(gameId);

		verify(client).solveAd(gameId, "plainAd");
		verify(client).solveAd(gameId, "encryptedAd");
	}

	@Test
	void run_shouldProcessEncryptedAds() {
		when(gameState.isGameOver()).thenReturn(false, true);

		when(client.fetchMessages(gameId)).thenReturn(List.of(encryptedAd));

		Ad decryptedAd = Ad.builder()
				.adId("encryptedAd")
				.encrypted(0)
				.message("Decrypted Ad")
				.reward(20)
				.probability("low")
				.build();

		when(adDecryptionService.decryptAds(List.of(encryptedAd))).thenReturn(List.of(decryptedAd));
		when(adQueueManager.buildAdQueue(List.of(decryptedAd))).thenReturn(new ArrayDeque<>(List.of(decryptedAd)));

		doNothing().when(purchaseService).tryBuyItem(gameState);
		when(client.solveAd(gameId, "encryptedAd")).thenReturn(solveResult);

		gameLoopManager.run(gameState);

		verify(adDecryptionService).decryptAds(List.of(encryptedAd));
		verify(client).solveAd(gameId, "encryptedAd");
		verify(gameState).updateFrom(solveResult);
	}

	@Test
	void run_shouldHandleNullResult() {
		when(gameState.isGameOver()).thenReturn(false, true);

		when(client.fetchMessages(gameId)).thenReturn(List.of(plainAd));
		when(adDecryptionService.decryptAds(List.of())).thenReturn(List.of());
		when(adQueueManager.buildAdQueue(List.of(plainAd))).thenReturn(new ArrayDeque<>(List.of(plainAd)));

		doNothing().when(purchaseService).tryBuyItem(gameState);

		when(client.solveAd(gameId, "plainAd")).thenReturn(null);

		gameLoopManager.run(gameState);

		verify(client).solveAd(gameId, "plainAd");

		verify(gameState, never()).updateFrom((SolveResult) any());
	}

	@Test
	void run_shouldBreakIfGameOverAfterAd() {
		when(gameState.isGameOver()).thenReturn(false, false, true);

		when(client.fetchMessages(gameId)).thenReturn(List.of(plainAd, encryptedAd));

		when(adDecryptionService.decryptAds(List.of(encryptedAd))).thenReturn(List.of(encryptedAd));
		when(adQueueManager.buildAdQueue(List.of(plainAd, encryptedAd))).thenReturn(new ArrayDeque<>(List.of(plainAd, encryptedAd)));

		doNothing().when(purchaseService).tryBuyItem(gameState);

		when(client.solveAd(gameId, "plainAd")).thenAnswer(invocation -> {
			when(gameState.isGameOver()).thenReturn(true);
			return solveResult;
		});

		when(client.solveAd(gameId, "encryptedAd")).thenReturn(null);

		gameLoopManager.run(gameState);

		verify(client).solveAd(gameId, "plainAd");
		verify(client, never()).solveAd(gameId, "encryptedAd");
		verify(gameState).updateFrom(solveResult);
	}

	@Test
	void run_shouldLogAndRecoverFromException() {
		when(gameState.isGameOver()).thenReturn(false, true);

		when(client.fetchMessages(gameId)).thenThrow(new RuntimeException("Fetch failed"));

		gameLoopManager.run(gameState);

		verify(client).fetchMessages(gameId);
	}
}