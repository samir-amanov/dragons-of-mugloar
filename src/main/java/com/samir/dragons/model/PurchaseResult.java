package com.samir.dragons.model;

import lombok.Getter;

@Getter
public class PurchaseResult {
	private boolean shoppingSuccess;
	private int gold;
	private int lives;
	private int level;
	private int turn;

	public void updateFrom(PurchaseResult result) {
		this.lives = result.getLives();
		this.gold = result.getGold();
		this.turn = result.getTurn();
	}
}