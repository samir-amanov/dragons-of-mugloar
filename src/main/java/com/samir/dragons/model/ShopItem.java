package com.samir.dragons.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ShopItem {

	@NotBlank
	private final String id;

	@NotBlank
	private final String name;

	@Min(0)
	private final int cost;
}