package com.tencent.wxcloudrun.repository;

public interface ThemeParkPoiSummaryProjection {

  Long getId();

  String getEntityId();

  String getName();

  String getEnglishName();

  String getCategory();

  String getImgUrl();

  String getPrimaryLocationKey();

  Double getLatitude();

  Double getLongitude();

  String getRawCoordinates();

  String getLocationName();

  String getGuestHeight();

  String getThrillLevel();

  String getAgeRange();

  String getInterestTags();

  String getTodayOpenTime();

  java.time.LocalDateTime getCreatedAt();

  java.time.LocalDateTime getUpdatedAt();
}
