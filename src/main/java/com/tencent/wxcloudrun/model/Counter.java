package com.tencent.wxcloudrun.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "Counters")
public class Counter implements Serializable {

  @Id
  private Integer id;

  @Column(name = "count", nullable = false)
  private Integer count;

  @Column(name = "createdAt", insertable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "updatedAt", insertable = false, updatable = false)
  private LocalDateTime updatedAt;
}
