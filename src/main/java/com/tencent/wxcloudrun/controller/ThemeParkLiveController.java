package com.tencent.wxcloudrun.controller;

import com.tencent.wxcloudrun.config.ApiResponse;
import com.tencent.wxcloudrun.service.ThemeParkLiveService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ThemeParkLiveController {

  private final ThemeParkLiveService themeParkLiveService;

  public ThemeParkLiveController(ThemeParkLiveService themeParkLiveService) {
    this.themeParkLiveService = themeParkLiveService;
  }

  @GetMapping("/api/themepark/live")
  public ApiResponse getLatestLiveData() {
    return ApiResponse.ok(themeParkLiveService.getLatestLiveDataList());
  }

  @GetMapping("/api/park/live/{entityId}")
  public ApiResponse getParkWaitTime(@PathVariable String entityId) {
    try {
      return ApiResponse.ok(themeParkLiveService.getParkWaitTime(entityId));
    } catch (IllegalArgumentException exception) {
      return ApiResponse.error(exception.getMessage());
    }
  }

  @GetMapping("/api/park/live/latest")
  public ApiResponse getLatestBatchLives() {
    try {
      return ApiResponse.ok(themeParkLiveService.getLatestBatchLives());
    } catch (IllegalArgumentException exception) {
      return ApiResponse.error(exception.getMessage());
    }
  }

  @GetMapping("/api/park/live/latest/rank")
  public ApiResponse getLatestBatchRankings() {
    try {
      return ApiResponse.ok(themeParkLiveService.getLatestBatchRankings());
    } catch (IllegalArgumentException exception) {
      return ApiResponse.error(exception.getMessage());
    }
  }

  @GetMapping("/api/park/live/latest/poi")
  public ApiResponse getLatestPoiWaitTimes() {
    try {
      return ApiResponse.ok(themeParkLiveService.getLatestPoiWithWaitTimes());
    } catch (IllegalArgumentException exception) {
      return ApiResponse.error(exception.getMessage());
    }
  }
}
