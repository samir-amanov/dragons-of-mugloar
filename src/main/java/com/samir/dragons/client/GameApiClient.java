package com.samir.dragons.client;

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

import com.samir.dragons.model.Ad;
import com.samir.dragons.model.GameState;
import com.samir.dragons.model.PurchaseResult;
import com.samir.dragons.model.Reputation;
import com.samir.dragons.model.ShopItem;
import com.samir.dragons.model.SolveResult;

@Component
public class GameApiClient {

	private static final Logger log = LoggerFactory.getLogger(GameApiClient.class);

	private static final String SCHEME = "https";
	private static final String HOST = "dragonsofmugloar.com";
	private static final String API_PREFIX = "/api/v2";

	private final RestTemplate restTemplate;

	public GameApiClient() {
		this.restTemplate = new RestTemplate();
	}

	public GameState startNewGame() {
		try {
			URI uri = buildUri("/game/start");
			GameState response = restTemplate.postForObject(uri, null, GameState.class);
			log.info("Started new game: {}", response);
			return response;
		} catch (RestClientException e) {
			log.error("Failed to start new game", e);
			return null;
		}
	}

	public List<Ad> fetchMessages(String gameId) {
		try {
			URI uri = buildUri("/" + gameId + "/messages");
			Ad[] ads = restTemplate.getForObject(uri, Ad[].class);
			log.debug("Fetched {} ads", ads != null ? ads.length : 0);
			return safeList(ads);
		} catch (RestClientException e) {
			log.error("Failed to fetch messages for gameId={}", gameId);
			return Collections.emptyList();
		}
	}

	public SolveResult solveAd(String gameId, String adId) {
		try {
			URI uri = buildUri("/" + gameId + "/solve/" + adId);
			SolveResult result = restTemplate.postForObject(uri, null, SolveResult.class);
			log.debug("Attempted to solve ad: {}, Result: {}", adId, result);
			return result;
		} catch (RestClientException e) {
			log.error("Failed to solve adId={} for gameId={}", adId, gameId);
			return null;
		}
	}

	public List<ShopItem> fetchShopItems(String gameId) {
		try {
			URI uri = buildUri("/" + gameId + "/shop");
			ShopItem[] items = restTemplate.getForObject(uri, ShopItem[].class);
			log.debug("Fetched shop items: {}", items != null ? items.length : 0);
			return safeList(items);
		} catch (RestClientException e) {
			log.error("Failed to fetch shop items for gameId={}", gameId);
			return Collections.emptyList();
		}
	}

	public PurchaseResult buyItem(String gameId, String itemId) {
		try {
			URI uri = buildUri("/" + gameId + "/shop/buy/" + itemId);
			PurchaseResult result = restTemplate.postForObject(uri, null, PurchaseResult.class);
			log.info("Purchased item: {} -> {}", itemId, result);
			return result;
		} catch (RestClientException e) {
			log.error("Failed to purchase itemId={} for gameId={}", itemId, gameId);
			return null;
		}
	}

	public Reputation investigateReputation(String gameId) {
		try {
			URI uri = buildUri("/" + gameId + "/investigate/reputation");
			Reputation reputation = restTemplate.postForObject(uri, null, Reputation.class);
			log.debug("Investigated reputation: {}", reputation);
			return reputation;
		} catch (RestClientException e) {
			log.error("Failed to investigate reputation for gameId={}", gameId);
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
}