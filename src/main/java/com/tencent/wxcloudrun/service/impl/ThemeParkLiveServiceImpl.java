package com.tencent.wxcloudrun.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tencent.wxcloudrun.model.ThemeParkLive;
import com.tencent.wxcloudrun.model.ThemeParkLiveResponse;
import com.tencent.wxcloudrun.repository.ThemeParkLiveRepository;
import com.tencent.wxcloudrun.repository.ThemeParkPoiRepository;
import com.tencent.wxcloudrun.repository.ThemeParkPoiSummaryProjection;
import com.tencent.wxcloudrun.service.ThemeParkEntityStorageService;
import com.tencent.wxcloudrun.service.ThemeParkLiveService;
import com.tencent.wxcloudrun.service.ThemeParkLiveStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ThemeParkLiveServiceImpl implements ThemeParkLiveService {

  private static final Logger logger = LoggerFactory.getLogger(ThemeParkLiveServiceImpl.class);
  private static final String DEFAULT_ENTITY_ID = "6e1464ca-1e9b-49c3-8937-c5c6f6675057";

  private final RestTemplate restTemplate;
  private final String liveApiUrlTemplate;
  private final List<String> entityIds;
  private final ThemeParkLiveStorageService themeParkLiveStorageService;
  private final ThemeParkEntityStorageService themeParkEntityStorageService;
  private final ThemeParkPoiRepository themeParkPoiRepository;
  private final ThemeParkLiveRepository themeParkLiveRepository;
  private final ObjectMapper objectMapper;
  private volatile List<ThemeParkLiveResponse> latestLiveData = Collections.emptyList();

  public ThemeParkLiveServiceImpl(
      RestTemplateBuilder restTemplateBuilder,
      ThemeParkLiveStorageService themeParkLiveStorageService,
      ThemeParkEntityStorageService themeParkEntityStorageService,
      ThemeParkPoiRepository themeParkPoiRepository,
      ThemeParkLiveRepository themeParkLiveRepository,
      ObjectMapper objectMapper,
      @Value("${themepark.live.url-template:https://api.themeparks.wiki/v1/entity/%s/live}") String liveApiUrlTemplate,
      @Value("${themepark.live.entity-ids:6e1464ca-1e9b-49c3-8937-c5c6f6675057}") List<String> entityIds) {
    this.restTemplate = restTemplateBuilder
        .setConnectTimeout(Duration.ofSeconds(10))
        .setReadTimeout(Duration.ofSeconds(10))
        .build();
    this.themeParkLiveStorageService = themeParkLiveStorageService;
    this.themeParkEntityStorageService = themeParkEntityStorageService;
    this.themeParkPoiRepository = themeParkPoiRepository;
    this.themeParkLiveRepository = themeParkLiveRepository;
    this.objectMapper = objectMapper;
    this.liveApiUrlTemplate = liveApiUrlTemplate;
    this.entityIds = entityIds == null || entityIds.isEmpty() ? Collections.singletonList(DEFAULT_ENTITY_ID) : entityIds;
  }

  @Override
  public List<ThemeParkLiveResponse> fetchLiveData() {
    return fetchAllLiveData();
  }

  @Override
  public ThemeParkLiveResponse getLatestLiveData() {
    List<ThemeParkLiveResponse> responses = getLatestLiveDataList();
    return responses.get(responses.size() - 1);
  }

  @Override
  public List<ThemeParkLiveResponse> getLatestLiveDataList() {
    List<ThemeParkLiveResponse> cached = latestLiveData;
    if (cached != null && !cached.isEmpty()) {
      return cached;
    }
    return fetchAllLiveData();
  }

  private List<ThemeParkLiveResponse> fetchAllLiveData() {
    if (entityIds == null || entityIds.isEmpty()) {
      throw new IllegalStateException("No entity ids configured for theme park live sync");
    }

    LocalDateTime batchFetchedAt = LocalDateTime.now();
    List<ThemeParkLiveResponse> responses = new ArrayList<>();

    for (String entityId : entityIds) {
      String liveApiUrl = String.format(liveApiUrlTemplate, entityId);
      logger.info("Fetching theme park live data from {}", liveApiUrl);
      ResponseEntity<ThemeParkLiveResponse> response = restTemplate.getForEntity(liveApiUrl, ThemeParkLiveResponse.class);
      ThemeParkLiveResponse body = response.getBody();
      if (body == null) {
        throw new IllegalStateException("theme park live api returned empty body, entityId=" + entityId);
      }
      responses.add(body);
      themeParkLiveStorageService.saveLives(body, batchFetchedAt);
    }

    latestLiveData = Collections.unmodifiableList(new ArrayList<>(responses));
    return latestLiveData;
  }

  @Override
  public Map<String, Object> getParkWaitTime(String entityId) {
    LocalDateTime latestFetchedAt = getRequiredLatestFetchedAt();
    Optional<ThemeParkLive> optionalLive = themeParkLiveRepository.findFirstByEntityIdAndFetchedAtOrderByIdDesc(entityId, latestFetchedAt);
    if (!optionalLive.isPresent()) {
      throw new IllegalArgumentException("Entity not found in latest batch: " + entityId);
    }
    return toLiveMap(optionalLive.get());
  }

  @Override
  public Map<String, Object> getLatestBatchLives() {
    LocalDateTime latestFetchedAt = getRequiredLatestFetchedAt();
    List<ThemeParkLive> lives = themeParkLiveRepository.findAllByFetchedAt(latestFetchedAt);

    List<Map<String, Object>> items = new ArrayList<>();
    Map<String, Map<String, Object>> destinationGroups = new LinkedHashMap<>();

    for (ThemeParkLive live : lives) {
      Map<String, Object> liveMap = toLiveMap(live);
      items.add(liveMap);

      String destinationKey = live.getParkId() == null || live.getParkId().trim().isEmpty() ? "UNKNOWN" : live.getParkId();
      Map<String, Object> destinationGroup = destinationGroups.computeIfAbsent(destinationKey, key -> {
        Map<String, Object> group = new LinkedHashMap<>();
        group.put("destinationOrParkId", key);
        group.put("count", 0);
        group.put("items", new ArrayList<Map<String, Object>>());
        return group;
      });

      @SuppressWarnings("unchecked")
      List<Map<String, Object>> groupItems = (List<Map<String, Object>>) destinationGroup.get("items");
      groupItems.add(liveMap);
      destinationGroup.put("count", groupItems.size());
    }

    Map<String, Object> result = new LinkedHashMap<>();
    result.put("fetchedAt", latestFetchedAt);
    result.put("count", items.size());
    result.put("items", items);
    result.put("groupedByDestinationOrPark", new ArrayList<>(destinationGroups.values()));
    return result;
  }

  @Override
  public List<Map<String, Object>> getLatestBatchRankings() {
    LocalDateTime latestFetchedAt = getRequiredLatestFetchedAt();
    List<ThemeParkLive> lives = themeParkLiveRepository.findAllByFetchedAt(latestFetchedAt);

    List<Map<String, Object>> rankings = new ArrayList<>();
    for (ThemeParkLive live : lives) {
      Map<String, Object> item = new LinkedHashMap<>();
      item.put("fetchedAt", live.getFetchedAt());
      item.put("id", live.getEntityId());
      item.put("name", live.getEntityName());
      item.put("entityType", live.getEntityType());
      item.put("status", live.getStatus());
      item.put("parkId", live.getParkId());
      item.put("standbyWaitTime", live.getStandbyWaitTime());
      item.put("singleRiderWaitTime", live.getSingleRiderWaitTime());
      rankings.add(item);
    }

    rankings.sort(Comparator.comparing(
        item -> (Integer) item.get("standbyWaitTime"),
        Comparator.nullsLast(Comparator.reverseOrder())
    ));
    return rankings;
  }

  @Override
  public List<Map<String, Object>> getLatestPoiWithWaitTimes() {
    LocalDateTime latestFetchedAt = getRequiredLatestFetchedAt();
    List<ThemeParkPoiSummaryProjection> pois = themeParkPoiRepository.findAllSummariesByOrderByIdAsc();
    if (pois.isEmpty()) {
      return Collections.emptyList();
    }

    List<ThemeParkLive> lives = themeParkLiveRepository.findAllByFetchedAt(latestFetchedAt);
    Map<String, ThemeParkLive> latestLiveByEntityId = lives.stream()
        .collect(Collectors.toMap(ThemeParkLive::getEntityId, live -> live, (oldValue, newValue) -> newValue));

    List<Map<String, Object>> result = new ArrayList<>();
    for (ThemeParkPoiSummaryProjection poi : pois) {
      Map<String, Object> item = new LinkedHashMap<>();
      item.put("id", poi.getId());
      item.put("entityId", poi.getEntityId());
      item.put("name", poi.getName());
      item.put("englishName", poi.getEnglishName());
      item.put("category", poi.getCategory());
      item.put("imgUrl", poi.getImgUrl());
      item.put("primaryLocationKey", poi.getPrimaryLocationKey());
      item.put("latitude", poi.getLatitude());
      item.put("longitude", poi.getLongitude());
      item.put("rawCoordinates", poi.getRawCoordinates());
      item.put("locationName", poi.getLocationName());
      item.put("guestHeight", poi.getGuestHeight());
      item.put("thrillLevel", poi.getThrillLevel());
      item.put("ageRange", poi.getAgeRange());
      item.put("interestTags", poi.getInterestTags());
      item.put("todayOpenTime", poi.getTodayOpenTime());
      item.put("createdAt", poi.getCreatedAt());
      item.put("updatedAt", poi.getUpdatedAt());

      ThemeParkLive live = latestLiveByEntityId.get(poi.getEntityId());
      item.put("status", live == null ? null : live.getStatus());
      item.put("standbyWaitTime", live == null ? null : live.getStandbyWaitTime());
      item.put("singleRiderWaitTime", live == null ? null : live.getSingleRiderWaitTime());
      item.put("queue", live == null ? Collections.emptyMap() : readQueue(live.getRawQueue()));
      item.put("liveLastUpdated", live == null ? null : live.getLiveLastUpdated());
      item.put("liveFetchedAt", latestFetchedAt);
      result.add(item);
    }
    return result;
  }

  private LocalDateTime getRequiredLatestFetchedAt() {
    LocalDateTime latestFetchedAt = themeParkLiveRepository.findLatestFetchedAt();
    if (latestFetchedAt == null) {
      throw new IllegalArgumentException("No synced live batch found");
    }
    return latestFetchedAt;
  }

  private Map<String, Object> toLiveMap(ThemeParkLive live) {
    Map<String, Object> result = new LinkedHashMap<>();
    result.put("fetchedAt", live.getFetchedAt());
    result.put("id", live.getEntityId());
    result.put("name", live.getEntityName());
    result.put("entityType", live.getEntityType());
    result.put("parkId", live.getParkId());
    result.put("externalId", live.getExternalId());
    result.put("status", live.getStatus());
    result.put("timezone", live.getTimezone());
    result.put("standbyWaitTime", live.getStandbyWaitTime());
    result.put("singleRiderWaitTime", live.getSingleRiderWaitTime());
    result.put("queue", readQueue(live.getRawQueue()));
    result.put("lastUpdated", live.getLiveLastUpdated());
    return result;
  }

  private Object readQueue(String rawQueue) {
    if (rawQueue == null || rawQueue.trim().isEmpty()) {
      return Collections.emptyMap();
    }
    try {
      return objectMapper.readValue(rawQueue, new TypeReference<Map<String, Object>>() {
      });
    } catch (Exception exception) {
      logger.warn("Failed to parse raw queue json for latest batch query. rawQueue={}", rawQueue, exception);
      return Collections.emptyMap();
    }
  }
}
