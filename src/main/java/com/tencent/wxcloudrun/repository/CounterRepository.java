package com.tencent.wxcloudrun.repository;

import com.tencent.wxcloudrun.model.Counter;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CounterRepository extends JpaRepository<Counter, Integer> {
}
