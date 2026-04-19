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
@Table(name = "theme_park_poi")
public class ThemeParkPoi implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "entity_id", nullable = false, length = 128)
  private String entityId;

  @Column(name = "name", nullable = false, length = 255)
  private String name;

  @Column(name = "english_name", length = 255)
  private String englishName;

  @Column(name = "category", length = 128)
  private String category;

  @Column(name = "img_url", length = 1024)
  private String imgUrl;

  @Column(name = "primary_location_key", length = 128)
  private String primaryLocationKey;

  @Column(name = "latitude")
  private Double latitude;

  @Column(name = "longitude")
  private Double longitude;

  @Column(name = "raw_coordinates", columnDefinition = "text")
  private String rawCoordinates;

  @Column(name = "location_name", length = 255)
  private String locationName;

  @Column(name = "guest_height", length = 255)
  private String guestHeight;

  @Column(name = "thrill_level", length = 255)
  private String thrillLevel;

  @Column(name = "age_range", length = 512)
  private String ageRange;

  @Column(name = "interest_tags", length = 512)
  private String interestTags;

  @Column(name = "today_open_time", length = 128)
  private String todayOpenTime;

  @Column(name = "raw_detail", columnDefinition = "text")
  private String rawDetail;

  @Column(name = "raw_schedule_time_info", columnDefinition = "text")
  private String rawScheduleTimeInfo;

  @Column(name = "created_at", insertable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at", insertable = false, updatable = false)
  private LocalDateTime updatedAt;
}
