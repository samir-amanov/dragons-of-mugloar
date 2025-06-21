package com.samir.dragons.model;

import lombok.Getter;

@Getter
public class Ad {
	private String adId;
	private String message;
	private int reward;
	private int expiresIn;
	private String encrypted;
	private String probability;
}