package com.samir.dragons.runner;

import java.util.Arrays;

import com.samir.dragons.service.game.GameService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ParallelRunner {

	private final GameService gameService;
	private final int parallelRuns;

	public ParallelRunner(GameService gameService, @Value("${dragons.parallel-runs}") int parallelRuns) {
		this.gameService = gameService;
		this.parallelRuns = parallelRuns;
	}

	@EventListener(ApplicationReadyEvent.class)
	public void runParallelGames() throws InterruptedException {
		if (isTestEnvironment())
			return;

		Thread[] threads = new Thread[parallelRuns];

		for (int i = 0; i < parallelRuns; i++) {
			final int threadId = i;
			threads[i] = Thread.ofVirtual().start(() -> {
				String runId = "run-" + threadId;
				MDC.put("runId", runId);
				try {
					gameService.runGameLoop();
				} catch (Exception e) {
					LoggerFactory.getLogger(ParallelRunner.class).error("Run {} failed: {}", threadId, e.getMessage(), e);
				} finally {
					MDC.clear();
				}
			});
		}

		for (Thread thread : threads) {
			thread.join();
		}

		Logger summaryLogger = LoggerFactory.getLogger("summary");
		summaryLogger.info("\n✅ All {} game loops completed. See logs in /logs/", parallelRuns);
		System.exit(0);
	}

	boolean isTestEnvironment() {
		return Arrays.stream(Thread.currentThread().getStackTrace())
				.anyMatch(el -> el.getClassName().contains("org.junit")
						|| el.getClassName().contains("surefire"));
	}
}