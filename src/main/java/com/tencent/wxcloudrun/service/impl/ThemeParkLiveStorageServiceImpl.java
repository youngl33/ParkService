package com.tencent.wxcloudrun.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tencent.wxcloudrun.model.ThemeParkLive;
import com.tencent.wxcloudrun.model.ThemeParkLiveResponse;
import com.tencent.wxcloudrun.repository.ThemeParkLiveRepository;
import com.tencent.wxcloudrun.service.ThemeParkLiveStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ThemeParkLiveStorageServiceImpl implements ThemeParkLiveStorageService {

  private static final Logger logger = LoggerFactory.getLogger(ThemeParkLiveStorageServiceImpl.class);

  private final ThemeParkLiveRepository themeParkLiveRepository;
  private final ObjectMapper objectMapper;

  public ThemeParkLiveStorageServiceImpl(ThemeParkLiveRepository themeParkLiveRepository, ObjectMapper objectMapper) {
    this.themeParkLiveRepository = themeParkLiveRepository;
    this.objectMapper = objectMapper;
  }

  @Override
  public void saveLives(ThemeParkLiveResponse response, LocalDateTime fetchedAt) {
    if (response == null || CollectionUtils.isEmpty(response.getLiveData())) {
      logger.info("No live data available, skip database persistence");
      return;
    }

    List<ThemeParkLive> liveRecords = new ArrayList<>();
    for (ThemeParkLiveResponse.LiveDataItem item : response.getLiveData()) {
      ThemeParkLive live = new ThemeParkLive();
      live.setEntityId(item.getId());
      live.setEntityName(item.getName());
      live.setEntityType(item.getEntityType());
      live.setTimezone(response.getTimezone());
      live.setStatus(item.getStatus());
      live.setParkId(item.getParkId());
      live.setExternalId(item.getExternalId());
      live.setStandbyWaitTime(getWaitTime(item.getQueue(), "STANDBY"));
      live.setSingleRiderWaitTime(getWaitTime(item.getQueue(), "SINGLE_RIDER"));
      live.setRawQueue(toJson(item.getQueue()));
      live.setLiveLastUpdated(parseUtcTime(item.getLastUpdated()));
      live.setFetchedAt(fetchedAt);
      liveRecords.add(live);
    }

    try {
      themeParkLiveRepository.saveAll(liveRecords);
      logger.info("Saved {} theme park live records. destinationId={}, timezone={}, fetchedAt={}",
          liveRecords.size(), response.getId(), response.getTimezone(), fetchedAt);
    } catch (Exception exception) {
      logger.error("Failed to save theme park live records. destinationId={}, timezone={}, liveDataCount={}, fetchedAt={}, error={}",
          response.getId(), response.getTimezone(), liveRecords.size(), fetchedAt, exception.getMessage(), exception);
      throw exception;
    }
  }

  private Integer getWaitTime(Map<String, ThemeParkLiveResponse.QueueInfo> queue, String queueType) {
    if (queue == null) {
      return null;
    }
    ThemeParkLiveResponse.QueueInfo queueInfo = queue.get(queueType);
    return queueInfo == null ? null : queueInfo.getWaitTime();
  }

  private String toJson(Object value) {
    try {
      return objectMapper.writeValueAsString(value);
    } catch (JsonProcessingException exception) {
      throw new IllegalStateException("Failed to serialize queue data", exception);
    }
  }

  private LocalDateTime parseUtcTime(String value) {
    if (value == null || value.trim().isEmpty()) {
      return null;
    }
    return LocalDateTime.ofInstant(Instant.parse(value), ZoneId.of("UTC"));
  }
}
