package com.tencent.wxcloudrun.task;

import com.tencent.wxcloudrun.model.ThemeParkLiveResponse;
import com.tencent.wxcloudrun.service.ThemeParkLiveService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class ThemeParkLiveScheduler {

  private static final Logger logger = LoggerFactory.getLogger(ThemeParkLiveScheduler.class);

  private final ThemeParkLiveService themeParkLiveService;

  public ThemeParkLiveScheduler(ThemeParkLiveService themeParkLiveService) {
    this.themeParkLiveService = themeParkLiveService;
  }

  @Scheduled(cron = "0 */5 9-22 * * ?", zone = "Asia/Shanghai")
  public void syncThemeParkLiveData() {
    LocalDateTime startTime = LocalDateTime.now();
    logger.info("[SCHEDULER] Theme park sync job started at {}", startTime);
    try {
      List<ThemeParkLiveResponse> responses = themeParkLiveService.fetchLiveData();
      int entityCount = responses.size();
      int totalLiveDataCount = 0;
      for (ThemeParkLiveResponse response : responses) {
        totalLiveDataCount += response.getLiveData() == null ? 0 : response.getLiveData().size();
      }
      logger.info("[SCHEDULER] Theme park sync job finished successfully. entityCount={}, totalLiveDataCount={}, finishedAt={}",
          entityCount, totalLiveDataCount, LocalDateTime.now());
    } catch (Exception exception) {
      logger.error("[SCHEDULER] Theme park sync job failed at {}", LocalDateTime.now(), exception);
    }
  }
}
