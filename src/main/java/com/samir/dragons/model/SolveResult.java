package com.samir.dragons.model;

import jakarta.validation.constraints.Min;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SolveResult {

	private final boolean success;

	@Min(0)
	private final int lives;

	@Min(0)
	private final int gold;

	@Min(0)
	private final int score;

	@Min(0)
	private final int highScore;

	@Min(0)
	private final int turn;

	private final String message;
}