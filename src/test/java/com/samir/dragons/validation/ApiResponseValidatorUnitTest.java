package com.samir.dragons.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.samir.dragons.model.Ad;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ApiResponseValidatorUnitTest {

	private ApiResponseValidator validator;

	@BeforeEach
	void setup() {
		validator = new ApiResponseValidator();
	}

	@Test
	void validate_nullResponse_shouldThrowException() {
		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
			validator.validate(null);
		});
		assertEquals("API response is null", ex.getMessage());
	}

	@Test
	void validate_validAd_shouldPass() {
		Ad validAd = Ad.builder()
				.adId("ad1")
				.message("Valid message")
				.reward(10)
				.probability("Sure thing")
				.build();

		assertDoesNotThrow(() -> validator.validate(validAd));
	}

	@Test
	void validate_invalidAd_shouldThrowException() {
		Ad invalidAd = Ad.builder()
				.adId(null)
				.message(null)
				.reward(-5)
				.probability("Impossible")
				.build();

		IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
			validator.validate(invalidAd);
		});
		assertTrue(ex.getMessage().contains("API response is invalid"));
	}

	@Test
	void validateAll_nullList_shouldThrowException() {
		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
			validator.validateAll(null);
		});
		assertEquals("API response list is null", ex.getMessage());
	}

	@Test
	void validateAll_validList_shouldPass() {
		Ad ad1 = Ad.builder().adId("1").message("Help").reward(5).probability("Sure thing").build();
		Ad ad2 = Ad.builder().adId("2").message("Steal").reward(10).probability("Piece of cake").build();

		List<Ad> ads = List.of(ad1, ad2);

		assertDoesNotThrow(() -> validator.validateAll(ads));
	}

	@Test
	void validateAll_listWithInvalidItem_shouldThrowException() {
		Ad validAd = Ad.builder().adId("1").message("Help").reward(5).probability("Sure thing").build();
		Ad invalidAd = Ad.builder().adId(null).message(null).reward(-10).probability("Impossible").build();

		List<Ad> ads = List.of(validAd, invalidAd);

		IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
			validator.validateAll(ads);
		});
		assertTrue(ex.getMessage().contains("API response is invalid"));
	}
}