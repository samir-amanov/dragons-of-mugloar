package com.samir.dragons.service.game;

import static com.samir.dragons.util.Printer.printFinalInfo;

import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.samir.dragons.model.GameState;

import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class GameParallelRunner {

	private final GameService gameService;
	private final int parallelRuns;

	public GameParallelRunner(GameService gameService, @Value("${dragons.parallel-runs}") int parallelRuns) {
		this.gameService = gameService;
		this.parallelRuns = parallelRuns;
	}

	@EventListener(ApplicationReadyEvent.class)
	public void runParallelGames() throws InterruptedException {
		if (isTestEnvironment())
			return;

		long startTime = System.nanoTime();

		Thread[] threads = new Thread[parallelRuns];

		ConcurrentLinkedQueue<GameState> results = new ConcurrentLinkedQueue<>();

		for (int i = 0; i < parallelRuns; i++) {
			final int threadId = i;
			threads[i] = Thread.ofVirtual().start(() -> {
				String runId = "run-" + threadId;
				MDC.put("runId", runId);
				try {
					GameState gameState = gameService.runGameLoop();
					results.add(gameState);
				} catch (Exception e) {
					LoggerFactory.getLogger(GameParallelRunner.class).error("Run {} failed: {}", threadId, e.getMessage(), e);
				} finally {
					MDC.clear();
				}
			});
		}

		for (Thread thread : threads) {
			thread.join();
		}

		printFinalInfo(results, startTime, parallelRuns);
		System.exit(0);
	}

	boolean isTestEnvironment() {
		return Arrays.stream(Thread.currentThread().getStackTrace())
				.anyMatch(el -> el.getClassName().contains("org.junit")
						|| el.getClassName().contains("surefire"));
	}
}