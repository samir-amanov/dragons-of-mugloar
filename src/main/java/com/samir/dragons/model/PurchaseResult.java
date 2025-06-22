package com.samir.dragons.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PurchaseResult {
	private boolean shoppingSuccess;
	private int gold;
	private int lives;
	private int level;
	private int turn;
}