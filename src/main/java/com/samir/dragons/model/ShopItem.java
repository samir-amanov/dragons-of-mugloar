package com.samir.dragons.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ShopItem {
	private String id;
	private String name;
	private int cost;
}