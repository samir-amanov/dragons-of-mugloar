package com.samir.dragons.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SolveResult {

	private boolean success;
	private int lives;
	private int gold;
	private int score;
	private int highScore;
	private int turn;
	private String message;
}