package com.samir.dragons.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GameState {

	@NotBlank
	private final String gameId;

	@Min(0)
	private int lives;

	@Min(0)
	private int gold;

	@Min(0)
	private int level;

	@Min(0)
	private int score;

	@Min(0)
	private int highScore;

	@Min(0)
	private int turn;

	public boolean isGameOver() {
		return lives == 0;
	}

	public void updateFrom(SolveResult result) {
		this.lives = result.getLives();
		this.gold = result.getGold();
		this.score = result.getScore();
		this.highScore = result.getHighScore();
		this.turn = result.getTurn();
	}

	public void updateFrom(PurchaseResult result) {
		this.lives = result.getLives();
		this.gold = result.getGold();
		this.turn = result.getTurn();
	}
}