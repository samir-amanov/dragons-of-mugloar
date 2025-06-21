package com.samir.dragons.service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.samir.dragons.model.Ad;

@Service
public class AdSelectionService {

	public Optional<Ad> pickBestAd(List<Ad> ads) {
		if (ads == null || ads.isEmpty()) {
			return Optional.empty();
		}

		return ads.stream()
				.filter(this::isAdWorthTrying)
				.max(Comparator.comparingInt(Ad::getReward));
	}

	private boolean isAdWorthTrying(Ad ad) {
		return switch (ad.getProbability()) {
			case "Piece of cake", "Walk in the park", "Quite likely" -> true;
			default -> false;
		};
	}
}