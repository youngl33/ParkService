package com.tencent.wxcloudrun.repository;

import com.tencent.wxcloudrun.model.ThemeParkEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ThemeParkEntityRepository extends JpaRepository<ThemeParkEntity, Long> {

  Optional<ThemeParkEntity> findTopByEntityIdOrderByIdDesc(String entityId);
}
