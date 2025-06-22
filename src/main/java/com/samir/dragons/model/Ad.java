package com.samir.dragons.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder(toBuilder = true)
public class Ad {
	private String adId;
	private String message;
	private int reward;
	private int expiresIn;
	private Integer encrypted;
	private String probability;

	@Setter
	private transient double multiplier;
}