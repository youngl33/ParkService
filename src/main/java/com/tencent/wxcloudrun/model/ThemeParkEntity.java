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
@Table(name = "theme_park_entity")
public class ThemeParkEntity implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "entity_id", nullable = false, unique = true, length = 64)
  private String entityId;

  @Column(name = "name", nullable = false, length = 255)
  private String name;

  @Column(name = "slug", length = 255)
  private String slug;

  @Column(name = "entity_type", nullable = false, length = 32)
  private String entityType;

  @Column(name = "parent_id", length = 64)
  private String parentId;

  @Column(name = "destination_id", length = 64)
  private String destinationId;

  @Column(name = "timezone", length = 64)
  private String timezone;

  @Column(name = "external_id", length = 255)
  private String externalId;

  @Column(name = "latitude")
  private Double latitude;

  @Column(name = "longitude")
  private Double longitude;

  @Column(name = "raw_location", columnDefinition = "text")
  private String rawLocation;

  @Column(name = "raw_tags", columnDefinition = "text")
  private String rawTags;

  @Column(name = "fetched_at", nullable = false)
  private LocalDateTime fetchedAt;

  @Column(name = "created_at", insertable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at", insertable = false, updatable = false)
  private LocalDateTime updatedAt;
}
