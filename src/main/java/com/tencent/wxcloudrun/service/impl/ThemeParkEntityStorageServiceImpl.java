package com.tencent.wxcloudrun.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tencent.wxcloudrun.model.ThemeParkEntity;
import com.tencent.wxcloudrun.model.ThemeParkLiveResponse;
import com.tencent.wxcloudrun.repository.ThemeParkEntityRepository;
import com.tencent.wxcloudrun.service.ThemeParkEntityStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ThemeParkEntityStorageServiceImpl implements ThemeParkEntityStorageService {

  private static final Logger logger = LoggerFactory.getLogger(ThemeParkEntityStorageServiceImpl.class);

  private final ThemeParkEntityRepository themeParkEntityRepository;
  private final ObjectMapper objectMapper;

  public ThemeParkEntityStorageServiceImpl(ThemeParkEntityRepository themeParkEntityRepository, ObjectMapper objectMapper) {
    this.themeParkEntityRepository = themeParkEntityRepository;
    this.objectMapper = objectMapper;
  }

  @Override
  public void saveEntity(ThemeParkLiveResponse response, LocalDateTime fetchedAt) {
    if (response == null) {
      return;
    }

    ThemeParkEntity entity = themeParkEntityRepository.findTopByEntityIdOrderByIdDesc(response.getId())
        .orElseGet(ThemeParkEntity::new);
    entity.setEntityId(response.getId());
    entity.setName(response.getName());
    entity.setSlug(response.getSlug());
    entity.setEntityType(response.getEntityType());
    entity.setParentId(response.getParentId());
    entity.setDestinationId(response.getDestinationId());
    entity.setTimezone(response.getTimezone());
    entity.setExternalId(response.getExternalId());
    if (response.getLocation() != null) {
      entity.setLatitude(response.getLocation().getLatitude());
      entity.setLongitude(response.getLocation().getLongitude());
    }
    entity.setRawLocation(toJson(response.getLocation()));
    entity.setRawTags(toJson(response.getTags()));
    entity.setFetchedAt(fetchedAt);

    try {
      themeParkEntityRepository.save(entity);
      logger.info("Saved theme park entity. entityId={}, name={}, entityType={}, fetchedAt={}",
          entity.getEntityId(), entity.getName(), entity.getEntityType(), fetchedAt);
    } catch (Exception exception) {
      logger.error("Failed to save theme park entity. entityId={}, name={}, timezone={}, fetchedAt={}, error={}",
          entity.getEntityId(), entity.getName(), entity.getTimezone(), fetchedAt, exception.getMessage(), exception);
      throw exception;
    }
  }

  private String toJson(Object value) {
    try {
      return objectMapper.writeValueAsString(value);
    } catch (JsonProcessingException exception) {
      throw new IllegalStateException("Failed to serialize entity data", exception);
    }
  }
}
