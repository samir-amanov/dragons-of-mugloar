package com.samir.dragons.service.ads;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import com.samir.dragons.model.Ad;

@Service
@RequiredArgsConstructor
public class AdQueueManager {

	private final AdSelectionService adSelectionService;

	public Deque<Ad> buildAdQueue(List<Ad> ads) {
		List<Ad> selected = adSelectionService.selectAdsWithHighProbability(ads);
		if (!selected.isEmpty())
			return new ArrayDeque<>(selected);

		selected = adSelectionService.selectAdsWithModerateProbability(ads);
		if (!selected.isEmpty())
			return new ArrayDeque<>(selected);

		selected = adSelectionService.selectAdsWithLessProbability(ads);
		if (!selected.isEmpty())
			return new ArrayDeque<>(selected);

		return new ArrayDeque<>();
	}
}