package com.tencent.wxcloudrun.repository;

import com.tencent.wxcloudrun.model.ThemeParkPoi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ThemeParkPoiRepository extends JpaRepository<ThemeParkPoi, Long> {

  List<ThemeParkPoi> findAllByOrderByIdAsc();

  @Query("select "
      + "p.id as id, "
      + "p.entityId as entityId, "
      + "p.name as name, "
      + "p.englishName as englishName, "
      + "p.category as category, "
      + "p.imgUrl as imgUrl, "
      + "p.primaryLocationKey as primaryLocationKey, "
      + "p.latitude as latitude, "
      + "p.longitude as longitude, "
      + "p.rawCoordinates as rawCoordinates, "
      + "p.locationName as locationName, "
      + "p.guestHeight as guestHeight, "
      + "p.thrillLevel as thrillLevel, "
      + "p.ageRange as ageRange, "
      + "p.interestTags as interestTags, "
      + "p.todayOpenTime as todayOpenTime, "
      + "p.createdAt as createdAt, "
      + "p.updatedAt as updatedAt "
      + "from ThemeParkPoi p order by p.id asc")
  List<ThemeParkPoiSummaryProjection> findAllSummariesByOrderByIdAsc();
}
