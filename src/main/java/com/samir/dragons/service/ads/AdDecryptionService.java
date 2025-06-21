package com.samir.dragons.service.ads;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

import org.springframework.stereotype.Service;

import com.samir.dragons.model.Ad;

@Service
public class AdDecryptionService {

	public List<Ad> decryptAds(List<Ad> encryptedAds) {
		return encryptedAds.stream()
				.map(this::decryptAd)
				.toList();
	}

	private Ad decryptAd(Ad ad) {
		try {
			return ad.toBuilder()
					.adId(decode(ad.getAdId()))
					.message(decode(ad.getMessage()))
					.probability(decode(ad.getProbability()))
					.encrypted(null)
					.reward(ad.getReward())
					.build();
		} catch (Exception e) {
			return ad;
		}
	}

	private String decode(String input) {
		return new String(Base64.getDecoder().decode(input), StandardCharsets.UTF_8);
	}
}