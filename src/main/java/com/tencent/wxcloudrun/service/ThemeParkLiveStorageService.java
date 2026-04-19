package com.tencent.wxcloudrun.service;

import com.tencent.wxcloudrun.model.ThemeParkLiveResponse;

import java.time.LocalDateTime;

public interface ThemeParkLiveStorageService {

  void saveLives(ThemeParkLiveResponse response, LocalDateTime fetchedAt);
}
