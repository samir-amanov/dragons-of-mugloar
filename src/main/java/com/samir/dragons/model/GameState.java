package com.samir.dragons.model;

import lombok.Getter;

@Getter
public class GameState {

	private String gameId;
	private int lives;
	private int gold;
	private int level;
	private int score;
	private int highScore;
	private int turn;

	public boolean isGameOver() {
		return lives <= 0;
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