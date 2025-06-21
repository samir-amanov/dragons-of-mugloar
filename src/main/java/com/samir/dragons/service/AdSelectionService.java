package com.samir.dragons.service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.samir.dragons.model.Ad;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AdSelectionService {

	private static final Map<String, Double> PROBABILITY_MULTIPLIERS = Map.of(
			"Sure thing", 1.0,
			"Piece of cake", 0.95,
			"Walk in the park", 0.9,
			"Quite likely", 0.75,
			"Hmmm....", 0.5,
			"Rather detrimental", 0.3,
			"Playing with fire", 0.2,
			"Suicide mission", 0.1
	);

	private static final double HIGH_PROBABILITY = 0.5;
	private static final double MODERATE_PROBABILITY = 0.25;
	private static final double LESS_PROBABILITY = 0.00;
	private static final double HELPING_MULTIPLIER = 1.1;

	public List<Ad> selectAdsWithHighProbability(List<Ad> ads) {
		return selectAds(ads, HIGH_PROBABILITY);
	}

	public List<Ad> selectAdsWithModerateProbability(List<Ad> ads) {
		return selectAds(ads, MODERATE_PROBABILITY);
	}

	public List<Ad> selectAdsWithLessProbability(List<Ad> ads) {
		return selectAds(ads, LESS_PROBABILITY);
	}

	private List<Ad> selectAds(List<Ad> ads, double probability) {
		if (ads == null || ads.isEmpty()) {
			return List.of();
		}

		return ads.stream()
				.filter(this::isAdValid)
				.map(this::applyMultiplier)
				.filter(ad -> ad.getMultiplier() >= probability)
				.sorted(Comparator.comparingDouble(this::calculateScore).reversed())
				.toList();
	}

	private boolean isAdValid(Ad ad) {
		if (ad.getMessage() == null)
			return true;
		return !ad.getMessage().toLowerCase().contains("steal");
	}

	private Ad applyMultiplier(Ad ad) {
		double multiplier = PROBABILITY_MULTIPLIERS.getOrDefault(ad.getProbability(), 0.0);

		if (ad.getMessage() != null && ad.getMessage().toLowerCase().contains("help")) {
			multiplier *= HELPING_MULTIPLIER;
		}

		return ad.toBuilder()
				.multiplier(multiplier)
				.build();
	}

	private double calculateScore(Ad ad) {
		return ad.getReward() * ad.getMultiplier();
	}
}