package com.samir.dragons.client;

import com.samir.dragons.model.Ad;
import com.samir.dragons.model.GameState;
import com.samir.dragons.model.PurchaseResult;
import com.samir.dragons.model.ShopItem;
import com.samir.dragons.model.SolveResult;
import com.samir.dragons.validation.ApiResponseValidator;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GameApiClientUnitTest {

	private RestTemplate restTemplate;
	private GameApiClient client;
	private ApiResponseValidator validator;

	@BeforeEach
	void setup() {
		restTemplate = mock(RestTemplate.class);
		validator = mock(ApiResponseValidator.class);
		client = new GameApiClient(validator, restTemplate);
	}

	@Test
	void startNewGame_success() {
		GameState mockResponse = GameState.builder().gameId("abc").build();
		when(restTemplate.postForObject(any(URI.class), any(), Mockito.eq(GameState.class)))
				.thenReturn(mockResponse);

		GameState result = client.startNewGame();

		assertEquals("abc", result.getGameId());
	}

	@Test
	void startNewGame_failure() {
		when(restTemplate.postForObject(any(URI.class), any(), Mockito.eq(GameState.class)))
				.thenThrow(new RestClientException("error"));

		GameState result = client.startNewGame();

		assertNull(result);
	}

	@Test
	void fetchMessages_success() {
		Ad ad = Ad.builder().adId("1").build();
		when(restTemplate.getForObject(any(URI.class), Mockito.eq(Ad[].class)))
				.thenReturn(new Ad[] { ad });

		List<Ad> result = client.fetchMessages("game123");

		assertEquals(1, result.size());
		assertEquals("1", result.getFirst().getAdId());
	}

	@Test
	void fetchMessages_nullGameId_shouldThrow() {
		assertThrows(IllegalArgumentException.class, () -> client.fetchMessages(null));
		assertThrows(IllegalArgumentException.class, () -> client.fetchMessages(" "));
	}

	@Test
	void solveAd_success() {
		SolveResult solveResult = SolveResult.builder().success(true).build();
		when(restTemplate.postForObject(any(URI.class), any(), Mockito.eq(SolveResult.class)))
				.thenReturn(solveResult);

		SolveResult result = client.solveAd("game123", "ad456");

		assertTrue(result.isSuccess());
	}

	@Test
	void solveAd_invalidArgs_shouldThrow() {
		assertThrows(IllegalArgumentException.class, () -> client.solveAd(null, "ad"));
		assertThrows(IllegalArgumentException.class, () -> client.solveAd("game", ""));
	}

	@Test
	void fetchShopItems_success() {
		ShopItem item = ShopItem.builder().id("item1").build();
		when(restTemplate.getForObject(any(URI.class), Mockito.eq(ShopItem[].class)))
				.thenReturn(new ShopItem[] { item });

		List<ShopItem> result = client.fetchShopItems("game123");

		assertEquals(1, result.size());
		assertEquals("item1", result.getFirst().getId());
	}

	@Test
	void fetchShopItems_invalidArgs_shouldThrow() {
		assertThrows(IllegalArgumentException.class, () -> client.fetchShopItems(null));
		assertThrows(IllegalArgumentException.class, () -> client.fetchShopItems(" "));
	}

	@Test
	void buyItem_success() {
		PurchaseResult purchaseResult = PurchaseResult.builder().gold(100).build();
		when(restTemplate.postForObject(any(URI.class), any(), Mockito.eq(PurchaseResult.class)))
				.thenReturn(purchaseResult);

		PurchaseResult result = client.buyItem("game123", "item456");

		assertEquals(100, result.getGold());
	}

	@Test
	void buyItem_invalidArgs_shouldThrow() {
		assertThrows(IllegalArgumentException.class, () -> client.buyItem(null, "item"));
		assertThrows(IllegalArgumentException.class, () -> client.buyItem("game", ""));
	}

	@Test
	void fetchMessages_validationFails_returnsEmptyList() {
		Ad ad = Ad.builder().adId("1").build();
		when(restTemplate.getForObject(any(URI.class), Mockito.eq(Ad[].class)))
				.thenReturn(new Ad[] { ad });

		Mockito.doThrow(new IllegalStateException("Validation failed"))
				.when(validator).validateAll(Mockito.anyList());

		List<Ad> result = client.fetchMessages("game123");

		assertEquals(Collections.emptyList(), result);
	}

	@Test
	void fetchMessages_restClientFails_returnsEmptyList() {
		when(restTemplate.getForObject(any(URI.class), Mockito.eq(Ad[].class)))
				.thenThrow(new RestClientException("API unreachable"));

		List<Ad> result = client.fetchMessages("game123");

		assertEquals(Collections.emptyList(), result);
	}

	@Test
	void fetchShopItems_validationFails_returnsEmptyList() {
		ShopItem item = ShopItem.builder().id("item1").build();
		when(restTemplate.getForObject(any(URI.class), Mockito.eq(ShopItem[].class)))
				.thenReturn(new ShopItem[] { item });

		Mockito.doThrow(new IllegalStateException("Validation error"))
				.when(validator).validateAll(Mockito.anyList());

		List<ShopItem> result = client.fetchShopItems("game123");

		assertEquals(Collections.emptyList(), result);
	}

	@Test
	void fetchShopItems_restClientFails_returnsEmptyList() {
		when(restTemplate.getForObject(any(URI.class), Mockito.eq(ShopItem[].class)))
				.thenThrow(new RestClientException("Server down"));

		List<ShopItem> result = client.fetchShopItems("game123");

		assertEquals(Collections.emptyList(), result);
	}

	@Test
	void solveAd_validationFails_returnsNull() {
		SolveResult solveResult = SolveResult.builder().success(true).build();
		when(restTemplate.postForObject(any(URI.class), any(), Mockito.eq(SolveResult.class)))
				.thenReturn(solveResult);

		Mockito.doThrow(new IllegalStateException("Invalid response"))
				.when(validator).validate(Mockito.any());

		SolveResult result = client.solveAd("game123", "ad456");

		assertNull(result);
	}

	@Test
	void solveAd_restClientFails_returnsNull() {
		when(restTemplate.postForObject(any(URI.class), any(), Mockito.eq(SolveResult.class)))
				.thenThrow(new RestClientException("Timeout"));

		SolveResult result = client.solveAd("game123", "ad456");

		assertNull(result);
	}

	@Test
	void buyItem_validationFails_returnsNull() {
		PurchaseResult purchaseResult = PurchaseResult.builder().gold(100).build();
		when(restTemplate.postForObject(any(URI.class), any(), Mockito.eq(PurchaseResult.class)))
				.thenReturn(purchaseResult);

		Mockito.doThrow(new IllegalStateException("Bad result"))
				.when(validator).validate(Mockito.any());

		PurchaseResult result = client.buyItem("game123", "item456");

		assertNull(result);
	}

	@Test
	void buyItem_restClientFails_returnsNull() {
		when(restTemplate.postForObject(any(URI.class), any(), Mockito.eq(PurchaseResult.class)))
				.thenThrow(new RestClientException("Failed to connect"));

		PurchaseResult result = client.buyItem("game123", "item456");

		assertNull(result);
	}

	@Test
	void startNewGame_validationFails_returnsNull() {
		GameState mockResponse = GameState.builder().gameId("abc").build();
		when(restTemplate.postForObject(any(URI.class), any(), Mockito.eq(GameState.class)))
				.thenReturn(mockResponse);

		Mockito.doThrow(new IllegalStateException("Invalid game state"))
				.when(validator).validate(Mockito.any());

		GameState result = client.startNewGame();

		assertNull(result);
	}

	@Test
	void startNewGame_restClientFails_returnsNull() {
		when(restTemplate.postForObject(any(URI.class), any(), Mockito.eq(GameState.class)))
				.thenThrow(new RestClientException("Server error"));

		GameState result = client.startNewGame();

		assertNull(result);
	}

	@Test
	void fetchMessages_nullResponse_returnsEmptyList() {
		when(restTemplate.getForObject(any(URI.class), Mockito.eq(Ad[].class)))
				.thenReturn(null);

		List<Ad> result = client.fetchMessages("game123");

		assertEquals(Collections.emptyList(), result);
	}
}