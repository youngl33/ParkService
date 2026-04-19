package com.tencent.wxcloudrun.service;

import com.tencent.wxcloudrun.model.ThemeParkLiveResponse;

public interface ThemeParkEntityStorageService {

  void saveEntity(ThemeParkLiveResponse response, java.time.LocalDateTime fetchedAt);
}
