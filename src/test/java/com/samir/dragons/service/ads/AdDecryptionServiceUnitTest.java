package com.samir.dragons.service.ads;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

import com.samir.dragons.model.Ad;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AdDecryptionServiceUnitTest {

	private AdDecryptionService adDecryptionService;

	@BeforeEach
	void setUp() {
		adDecryptionService = new AdDecryptionService();
	}

	@Test
	void decryptAds_shouldDecodeAllFieldsAndUnsetEncryptedFlag() {
		String encodedId = Base64.getEncoder().encodeToString("ad123".getBytes(StandardCharsets.UTF_8));
		String encodedMessage = Base64.getEncoder().encodeToString("Secret message".getBytes(StandardCharsets.UTF_8));
		String encodedProbability = Base64.getEncoder().encodeToString("Risky".getBytes(StandardCharsets.UTF_8));

		Ad encryptedAd = Ad.builder()
				.adId(encodedId)
				.message(encodedMessage)
				.probability(encodedProbability)
				.reward(42)
				.encrypted(1)
				.build();

		List<Ad> decryptedAds = adDecryptionService.decryptAds(List.of(encryptedAd));

		assertEquals(1, decryptedAds.size());
		Ad decrypted = decryptedAds.getFirst();
		assertEquals("ad123", decrypted.getAdId());
		assertEquals("Secret message", decrypted.getMessage());
		assertEquals("Risky", decrypted.getProbability());
		assertNull(decrypted.getEncrypted());
		assertEquals(42, decrypted.getReward());
	}

	@Test
	void decryptAds_shouldReturnOriginalAd_whenDecodingFails() {
		Ad badAd = Ad.builder()
				.adId("not_base64!!")
				.message("also_not_base64!!")
				.probability("Risky")
				.reward(10)
				.encrypted(1)
				.build();

		List<Ad> result = adDecryptionService.decryptAds(List.of(badAd));

		assertEquals(1, result.size());
		Ad returnedAd = result.getFirst();

		assertEquals(badAd.getAdId(), returnedAd.getAdId());
		assertEquals(badAd.getMessage(), returnedAd.getMessage());
		assertEquals(badAd.getProbability(), returnedAd.getProbability());
		assertEquals(badAd.getReward(), returnedAd.getReward());
		assertEquals(badAd.getEncrypted(), returnedAd.getEncrypted());
	}

	@Test
	void decryptAds_shouldReturnEmptyList_whenInputIsEmpty() {
		List<Ad> result = adDecryptionService.decryptAds(List.of());
		assertTrue(result.isEmpty());
	}
}