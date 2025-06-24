package com.samir.dragons.util;

import java.util.concurrent.ConcurrentLinkedQueue;

import lombok.extern.slf4j.Slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.samir.dragons.model.GameState;

@Slf4j
public class Printer {

	private static final Logger summaryLogger = LoggerFactory.getLogger("summary");

	public static void printFinalInfo(ConcurrentLinkedQueue<GameState> results, long startTime, int parallelRuns) {

		long totalDuration = (System.nanoTime() - startTime) / 1_000_000_000;

		long wins = results.stream().filter(result -> result.getScore() >= 1000).count();
		double winRate = (double) wins / parallelRuns * 100.0;
		summaryLogger.info("\n✅ All {} game loops completed in {} seconds. See details in /logs/",
				parallelRuns, totalDuration);
		summaryLogger.info("\nWon {} of {} games. Win percentage is: {} \n", wins, parallelRuns, String.format("%.2f", winRate));
	}

	public static void printStart(GameState gameState) {

		summaryLogger.info("⌛ New game started: {}. Wait for results.", gameState.getGameId());
	}

	public static void printGameSummary(GameState gameState) {
		log.info("🏁 Game Finished: {}", gameState.getGameId());
		log.info("📈 Final Score: {}", gameState.getScore());
		log.info("🔁 Turns Played: {}", gameState.getTurn());

		summaryLogger.info("\n🎯️ Game Finished: {}, Final Score {} in {} turns.", gameState.getGameId(), gameState.getScore()
				, gameState.getTurn());
	}
}