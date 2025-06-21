package com.samir.dragons.model;

import lombok.Getter;

@Getter
public class SolveResult {

	private boolean success;
	private int lives;
	private int gold;
	private int score;
	private int highScore;
	private int turn;
	private String message;

	public void updateFrom(SolveResult result) {
		this.lives = result.getLives();
		this.gold = result.getGold();
		this.score = result.getScore();
		this.highScore = result.getHighScore();
		this.turn = result.getTurn();
	}
}