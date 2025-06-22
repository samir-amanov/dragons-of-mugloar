package com.samir.dragons.service.game;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.samir.dragons.client.GameApiClient;
import com.samir.dragons.model.GameState;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class GameServiceUnitTest {

	@Mock
	private GameApiClient client;

	@Mock
	private GameLoopManager gameLoopManager;

	@InjectMocks
	private GameService gameService;

	private GameState gameState;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		gameState = GameState.builder()
				.gameId("game123")
				.lives(5)
				.gold(250)
				.score(250)
				.turn(12)
				.build();
	}

	@Test
	void runGameLoop_shouldStartGameLoopAndPrintSummary() {

		when(client.startNewGame()).thenReturn(gameState);

		gameService.runGameLoop();

		verify(client, times(1)).startNewGame();
		verify(gameLoopManager, times(1)).run(gameState);
	}
}