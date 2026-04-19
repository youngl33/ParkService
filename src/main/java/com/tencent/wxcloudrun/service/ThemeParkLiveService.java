package com.tencent.wxcloudrun.service;

import com.tencent.wxcloudrun.model.ThemeParkLiveResponse;

import java.util.List;
import java.util.Map;

public interface ThemeParkLiveService {

  List<ThemeParkLiveResponse> fetchLiveData();

  ThemeParkLiveResponse getLatestLiveData();

  List<ThemeParkLiveResponse> getLatestLiveDataList();

  Map<String, Object> getParkWaitTime(String entityId);

  Map<String, Object> getLatestBatchLives();

  List<Map<String, Object>> getLatestBatchRankings();

  List<Map<String, Object>> getLatestPoiWithWaitTimes(String parkId);
}
