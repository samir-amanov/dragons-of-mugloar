package com.samir.dragons.service.ads;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Deque;
import java.util.List;

import com.samir.dragons.model.Ad;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class AdQueueManagerUnitTest {

	@Mock
	private AdSelectionService adSelectionService;

	private AdQueueManager adQueueManager;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		adQueueManager = new AdQueueManager(adSelectionService);
	}

	@Test
	void buildAdQueue_shouldReturnHighProbabilityAds_ifAvailable() {
		List<Ad> inputAds = List.of(
				Ad.builder().adId("high1").build(),
				Ad.builder().adId("high2").build(),
				Ad.builder().adId("high3").build(),
				Ad.builder().adId("high4").build()
		);

		List<Ad> highProbAds = List.of(
				Ad.builder().adId("high1").build(),
				Ad.builder().adId("high2").build()
		);

		when(adSelectionService.selectAdsWithHighProbability(inputAds)).thenReturn(highProbAds);

		Deque<Ad> result = adQueueManager.buildAdQueue(inputAds);

		assertEquals(highProbAds.size(), result.size());
		assertEquals(highProbAds.getFirst(), result.peekFirst());
		verify(adSelectionService, times(1)).selectAdsWithHighProbability(inputAds);
		verify(adSelectionService, never()).selectAdsWithModerateProbability(anyList());
		verify(adSelectionService, never()).selectAdsWithLessProbability(anyList());
	}

	@Test
	void buildAdQueue_shouldReturnModerateProbabilityAds_ifNoHighProbabilityAds() {
		List<Ad> inputAds = List.of(
				Ad.builder().adId("1").build()
		);

		when(adSelectionService.selectAdsWithHighProbability(inputAds)).thenReturn(Collections.emptyList());

		List<Ad> moderateProbAds = List.of(
				Ad.builder().adId("mod1").build()
		);

		when(adSelectionService.selectAdsWithModerateProbability(inputAds)).thenReturn(moderateProbAds);

		Deque<Ad> result = adQueueManager.buildAdQueue(inputAds);

		assertEquals(moderateProbAds.size(), result.size());
		assertEquals(moderateProbAds.getFirst(), result.peekFirst());
		verify(adSelectionService).selectAdsWithHighProbability(inputAds);
		verify(adSelectionService).selectAdsWithModerateProbability(inputAds);
		verify(adSelectionService, never()).selectAdsWithLessProbability(anyList());
	}

	@Test
	void buildAdQueue_shouldReturnLessProbabilityAds_ifNoHighOrModerateProbabilityAds() {
		List<Ad> inputAds = List.of(
				Ad.builder().adId("1").build()
		);

		when(adSelectionService.selectAdsWithHighProbability(inputAds)).thenReturn(Collections.emptyList());
		when(adSelectionService.selectAdsWithModerateProbability(inputAds)).thenReturn(Collections.emptyList());

		List<Ad> lessProbAds = List.of(
				Ad.builder().adId("less1").build()
		);

		when(adSelectionService.selectAdsWithLessProbability(inputAds)).thenReturn(lessProbAds);

		Deque<Ad> result = adQueueManager.buildAdQueue(inputAds);

		assertEquals(lessProbAds.size(), result.size());
		assertEquals(lessProbAds.getFirst(), result.peekFirst());
		verify(adSelectionService).selectAdsWithHighProbability(inputAds);
		verify(adSelectionService).selectAdsWithModerateProbability(inputAds);
		verify(adSelectionService).selectAdsWithLessProbability(inputAds);
	}

	@Test
	void buildAdQueue_shouldReturnEmptyQueue_ifNoAdsSelected() {
		List<Ad> inputAds = List.of(
				Ad.builder().adId("1").build()
		);

		when(adSelectionService.selectAdsWithHighProbability(inputAds)).thenReturn(Collections.emptyList());
		when(adSelectionService.selectAdsWithModerateProbability(inputAds)).thenReturn(Collections.emptyList());
		when(adSelectionService.selectAdsWithLessProbability(inputAds)).thenReturn(Collections.emptyList());

		Deque<Ad> result = adQueueManager.buildAdQueue(inputAds);

		assertTrue(result.isEmpty());
		verify(adSelectionService).selectAdsWithHighProbability(inputAds);
		verify(adSelectionService).selectAdsWithModerateProbability(inputAds);
		verify(adSelectionService).selectAdsWithLessProbability(inputAds);
	}
}