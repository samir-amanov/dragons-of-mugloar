package com.samir.dragons.service.game;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.samir.dragons.model.GameState;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class GameParallelRunnerUnitTest {

	@Mock
	private GameService gameService;

	private GameParallelRunner gameParallelRunner;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		gameParallelRunner = new GameParallelRunner(gameService, 3);
	}

	@Test
	void runParallelGamesNow_shouldRunThreadsAndCollectResults() throws InterruptedException {
		GameState mockGameState = GameState.builder().gameId("game123").build();
		when(gameService.runGameLoop()).thenReturn(mockGameState);

		gameParallelRunner.runParallelGamesNow();

		verify(gameService, times(3)).runGameLoop();
	}

	@Test
	void runParallelGamesNow_shouldHandleExceptionsInThreads() throws InterruptedException {
		when(gameService.runGameLoop())
				.thenThrow(new RuntimeException("Error happened"))
				.thenReturn(GameState.builder().gameId("game123").build())
				.thenReturn(GameState.builder().gameId("game123").build());

		gameParallelRunner.runParallelGamesNow();

		verify(gameService, times(3)).runGameLoop();
	}

	@Test
	void isTestEnvironment_shouldReturnTrue_whenRunningInJUnit() {
		assertTrue(gameParallelRunner.isTestEnvironment(), "Expected test environment to be detected");
	}
}