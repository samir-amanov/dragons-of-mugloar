package com.samir.dragons.client;

import com.samir.dragons.model.Ad;
import com.samir.dragons.model.GameState;
import com.samir.dragons.model.PurchaseResult;
import com.samir.dragons.model.ShopItem;
import com.samir.dragons.model.SolveResult;
import com.samir.dragons.validation.ApiResponseValidator;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class GameApiClient {

	private static final Logger log = LoggerFactory.getLogger(GameApiClient.class);

	private static final String SCHEME = "https";
	private static final String HOST = "dragonsofmugloar.com";
	private static final String API_PREFIX = "/api/v2";

	private final RestTemplate restTemplate;
	private final ApiResponseValidator apiResponseValidator;

	public GameApiClient(ApiResponseValidator apiResponseValidator, RestTemplate restTemplate) {
		this.apiResponseValidator = apiResponseValidator;
		this.restTemplate = restTemplate;
	}

	public GameState startNewGame() {
		try {
			URI uri = buildUri("/game/start");
			GameState response = restTemplate.postForObject(uri, null, GameState.class);
			apiResponseValidator.validate(response);
			log.info("Started new game: {}", response);
			return response;
		} catch (RestClientException | IllegalStateException e) {
			log.error("Failed to start new game", e);
			return null;
		}
	}

	public List<Ad> fetchMessages(String gameId) {
		validateNotBlank(gameId, "Game ID");
		try {
			URI uri = buildUri("/" + gameId + "/messages");
			Ad[] ads = restTemplate.getForObject(uri, Ad[].class);
			List<Ad> adList = safeList(ads);
			apiResponseValidator.validateAll(adList);
			log.debug("Fetched {} ads", adList.size());
			return adList;
		} catch (RestClientException | IllegalStateException e) {
			log.error("Failed to fetch messages for gameId={}", gameId, e);
			return Collections.emptyList();
		}
	}

	public SolveResult solveAd(String gameId, String adId) {
		validateNotBlank(gameId, "Game ID");
		validateNotBlank(adId, "Ad ID");
		try {
			URI uri = buildUri("/" + gameId + "/solve/" + adId);
			SolveResult result = restTemplate.postForObject(uri, null, SolveResult.class);
			apiResponseValidator.validate(result);
			log.debug("Attempted to solve ad: {}, Result: {}", adId, result);
			return result;
		} catch (RestClientException | IllegalStateException e) {
			log.error("Failed to solve adId={} for gameId={}", adId, gameId, e);
			return null;
		}
	}

	public List<ShopItem> fetchShopItems(String gameId) {
		validateNotBlank(gameId, "Game ID");
		try {
			URI uri = buildUri("/" + gameId + "/shop");
			ShopItem[] items = restTemplate.getForObject(uri, ShopItem[].class);
			List<ShopItem> itemList = safeList(items);
			apiResponseValidator.validateAll(itemList);
			log.debug("Fetched shop items: {}", itemList.size());
			return itemList;
		} catch (RestClientException | IllegalStateException e) {
			log.error("Failed to fetch shop items for gameId={}", gameId, e);
			return Collections.emptyList();
		}
	}

	public PurchaseResult buyItem(String gameId, String itemId) {
		validateNotBlank(gameId, "Game ID");
		validateNotBlank(itemId, "Item ID");
		try {
			URI uri = buildUri("/" + gameId + "/shop/buy/" + itemId);
			PurchaseResult result = restTemplate.postForObject(uri, null, PurchaseResult.class);
			apiResponseValidator.validate(result);
			log.info("Purchased item: {} -> {}", itemId, result);
			return result;
		} catch (RestClientException | IllegalStateException e) {
			log.error("Failed to purchase itemId={} for gameId={}", itemId, gameId, e);
			return null;
		}
	}

	private URI buildUri(String path) {
		return UriComponentsBuilder.newInstance()
				.scheme(SCHEME)
				.host(HOST)
				.path(API_PREFIX + path)
				.build()
				.toUri();
	}

	private <T> List<T> safeList(T[] array) {
		return array != null ? Arrays.asList(array) : Collections.emptyList();
	}

	private void validateNotBlank(String value, String name) {
		if (value == null || value.isBlank()) {
			throw new IllegalArgumentException(name + " must not be null or blank");
		}
	}
}