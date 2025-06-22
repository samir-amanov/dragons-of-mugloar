package com.samir.dragons.model;

import jakarta.validation.constraints.Min;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PurchaseResult {

	private final boolean shoppingSuccess;

	@Min(0)
	private final int gold;

	@Min(0)
	private final int lives;

	@Min(0)
	private final int level;

	@Min(0)
	private final int turn;
}