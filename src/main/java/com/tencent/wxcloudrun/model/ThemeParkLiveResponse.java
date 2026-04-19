package com.tencent.wxcloudrun.model;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ThemeParkLiveResponse {

  private String id;
  private String name;
  private String slug;
  private String entityType;
  private String parentId;
  private String destinationId;
  private String timezone;
  private String externalId;
  private Location location;
  private List<Tag> tags;
  private List<LiveDataItem> liveData;

  @Data
  public static class Location {
    private Double latitude;
    private Double longitude;
  }

  @Data
  public static class Tag {
    private String tag;
    private String tagName;
    private String id;
    private Object value;
  }

  @Data
  public static class LiveDataItem {
    private String id;
    private String name;
    private String entityType;
    private String parkId;
    private String externalId;
    private Map<String, QueueInfo> queue;
    private String status;
    private String lastUpdated;
  }

  @Data
  public static class QueueInfo {
    private Integer waitTime;
  }
}
