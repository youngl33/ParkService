package com.tencent.wxcloudrun.controller;

import com.tencent.wxcloudrun.config.ApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class HealthController {

  private final JdbcTemplate jdbcTemplate;

  @Value("${MYSQL_ADDRESS:127.0.0.1:3306}")
  private String mysqlAddress;

  @Value("${MYSQL_DATABASE:park}")
  private String mysqlDatabase;

  @Value("${MYSQL_USERNAME:young}")
  private String mysqlUsername;

  public HealthController(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @GetMapping("/api/health/db")
  public ApiResponse databaseHealth() {
    Map<String, Object> data = new LinkedHashMap<>();
    data.put("mysqlAddress", mysqlAddress);
    data.put("mysqlDatabase", mysqlDatabase);
    data.put("mysqlUsername", mysqlUsername);

    try {
      Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
      data.put("connected", true);
      data.put("ping", result);
      return ApiResponse.ok(data);
    } catch (Exception exception) {
      data.put("connected", false);
      data.put("errorType", exception.getClass().getSimpleName());
      data.put("errorMessage", exception.getMessage());
      return ApiResponse.error("Database connection failed", data);
    }
  }
}
