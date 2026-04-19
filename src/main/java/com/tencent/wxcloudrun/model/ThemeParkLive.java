package com.tencent.wxcloudrun.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "theme_park_live")
public class ThemeParkLive implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "entity_id", nullable = false, length = 64)
  private String entityId;

  @Column(name = "entity_name", nullable = false, length = 255)
  private String entityName;

  @Column(name = "entity_type", nullable = false, length = 32)
  private String entityType;

  @Column(name = "timezone", length = 64)
  private String timezone;

  @Column(name = "status", length = 32)
  private String status;

  @Column(name = "park_id", length = 64)
  private String parkId;

  @Column(name = "external_id", length = 255)
  private String externalId;

  @Column(name = "standby_wait_time")
  private Integer standbyWaitTime;

  @Column(name = "single_rider_wait_time")
  private Integer singleRiderWaitTime;

  @Column(name = "raw_queue", columnDefinition = "text")
  private String rawQueue;

  @Column(name = "live_last_updated")
  private LocalDateTime liveLastUpdated;

  @Column(name = "fetched_at", nullable = false)
  private LocalDateTime fetchedAt;

  @Column(name = "yymmddhh", length = 8)
  private String yymmddhh;

  @Column(name = "created_at", insertable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at", insertable = false, updatable = false)
  private LocalDateTime updatedAt;
}
