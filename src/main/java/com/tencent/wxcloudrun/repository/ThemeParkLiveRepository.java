package com.tencent.wxcloudrun.repository;

import com.tencent.wxcloudrun.model.ThemeParkLive;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ThemeParkLiveRepository extends JpaRepository<ThemeParkLive, Long> {

  @Query("select max(t.fetchedAt) from ThemeParkLive t")
  LocalDateTime findLatestFetchedAt();

  Optional<ThemeParkLive> findFirstByEntityIdAndFetchedAtOrderByIdDesc(String entityId, LocalDateTime fetchedAt);

  List<ThemeParkLive> findAllByFetchedAt(LocalDateTime fetchedAt);
}
