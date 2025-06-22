package com.samir.dragons.service.ads;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import com.samir.dragons.model.Ad;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AdSelectionServiceUnitTest {

	private AdSelectionService adSelectionService;

	@BeforeEach
	void setUp() {
		adSelectionService = new AdSelectionService();
	}

	@Test
	void selectAdsWithHighProbability_filtersAndSortsCorrectly() {
		List<Ad> ads = List.of(
				Ad.builder().adId("1").reward(100).probability("Sure thing").message("Help me").build(),
				Ad.builder().adId("2").reward(50).probability("Impossible").message(null).build(),
				Ad.builder().adId("3").reward(200).probability("Walk in the park").message("Something else").build(),
				Ad.builder().adId("4").reward(150).probability("Quite likely").message("Steal attempt").build()
		);

		List<Ad> result = adSelectionService.selectAdsWithHighProbability(ads);

		assertEquals(2, result.size());

		assertEquals("3", result.get(0).getAdId());
		assertEquals("1", result.get(1).getAdId());
	}

	@Test
	void selectAdsWithModerateProbability_filtersCorrectly() {
		List<Ad> ads = List.of(
				Ad.builder().adId("1").reward(10).probability("Risky").message(null).build(),
				Ad.builder().adId("2").reward(100).probability("Suicide mission").message("Help needed").build(),
				Ad.builder().adId("3").reward(50).probability("Piece of cake").message(null).build()
		);

		List<Ad> result = adSelectionService.selectAdsWithModerateProbability(ads);

		assertEquals(2, result.size());
		assertEquals("3", result.get(0).getAdId());
		assertEquals("1", result.get(1).getAdId());
	}

	@Test
	void selectAdsWithLessProbability_returnsAllNonStealAds() {
		List<Ad> ads = List.of(
				Ad.builder().adId("1").reward(20).probability("Impossible").message("Something else").build(),
				Ad.builder().adId("2").reward(20).probability("Suicide mission").message(null).build(),
				Ad.builder().adId("3").reward(15).probability("Risky").message("Steal from you").build(),
				Ad.builder().adId("4").reward(20).probability("Impossible").message("Help").build()
		);

		List<Ad> result = adSelectionService.selectAdsWithLessProbability(ads);

		assertEquals(3, result.size());
		assertEquals("2", result.get(0).getAdId());
		assertEquals("4", result.get(1).getAdId());
		assertEquals("1", result.get(2).getAdId());
	}

	@Test
	void selectAds_emptyOrNull_returnsEmptyList() {
		assertEquals(0, adSelectionService.selectAdsWithHighProbability(List.of()).size());
		assertEquals(0, adSelectionService.selectAdsWithModerateProbability(null).size());
	}
}