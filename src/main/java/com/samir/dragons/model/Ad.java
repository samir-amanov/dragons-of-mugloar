package com.samir.dragons.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Ad {
	private String adId;
	private String message;
	private int reward;
	private int expiresIn;
	private String encrypted;
	private String probability;

	@Setter
	private transient double multiplier;
}