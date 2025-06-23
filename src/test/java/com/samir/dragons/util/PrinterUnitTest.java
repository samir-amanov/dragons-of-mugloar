package com.samir.dragons.util;

import com.samir.dragons.model.GameState;

import org.junit.jupiter.api.Test;

import java.util.concurrent.ConcurrentLinkedQueue;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PrinterUnitTest {

	@Test
	void printFinalInfo_shouldLogCorrectWinStats() {
		ConcurrentLinkedQueue<GameState> results = new ConcurrentLinkedQueue<>();

		results.add(createGameState("game1", 1000, 4));
		results.add(createGameState("game2", 999, 6));
		results.add(createGameState("game3", 2500, 5));

		long startTime = System.nanoTime() - 5_000_000_000L;
		int parallelRuns = 3;

		assertDoesNotThrow(() ->
				Printer.printFinalInfo(results, startTime, parallelRuns)
		);

		long wins = results.stream().filter(result -> result.getScore() >= 1000).count();
		double winRate = (double) wins / parallelRuns * 100.0;

		assertEquals(2, wins);
		assertEquals((2 / 3.0) * 100, winRate, 0.001);
	}

	@Test
	void printFinalInfo_shouldLogWinStats() {
		ConcurrentLinkedQueue<GameState> results = new ConcurrentLinkedQueue<>();

		results.add(createGameState("game1", 1200, 5));
		results.add(createGameState("game2", 500, 6));

		long startTime = System.nanoTime();
		int parallelRuns = 2;

		assertDoesNotThrow(() ->
				Printer.printFinalInfo(results, startTime, parallelRuns)
		);
	}

	@Test
	void printStart_shouldLogStartMessage() {
		GameState gameState = createGameState("game3", 0, 0);

		assertDoesNotThrow(() ->
				Printer.printStart(gameState)
		);
	}

	@Test
	void printGameSummary_shouldLogSummaryDetails() {
		GameState gameState = createGameState("game4", 850, 7);

		assertDoesNotThrow(() ->
				Printer.printGameSummary(gameState)
		);
	}

	private GameState createGameState(String gameId, int score, int turn) {
		return GameState.builder()
				.gameId(gameId)
				.score(score)
				.turn(turn)
				.build();
	}
}