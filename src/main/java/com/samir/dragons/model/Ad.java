package com.samir.dragons.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Getter
@Builder(toBuilder = true)
public class Ad {

	@NotBlank
	private final String adId;

	@NotBlank
	private final String message;

	@Min(0)
	private final int reward;

	@Min(0)
	private final int expiresIn;

	private final Integer encrypted;

	@NotBlank
	private final String probability;

	@Setter
	@JsonIgnore
	private double multiplier;
}