package com.tencent.wxcloudrun.service.impl;

import com.tencent.wxcloudrun.model.Counter;
import com.tencent.wxcloudrun.repository.CounterRepository;
import com.tencent.wxcloudrun.service.CounterService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CounterServiceImpl implements CounterService {

  private final CounterRepository counterRepository;

  public CounterServiceImpl(CounterRepository counterRepository) {
    this.counterRepository = counterRepository;
  }

  @Override
  public Optional<Counter> getCounter(Integer id) {
    return counterRepository.findById(id);
  }

  @Override
  public void upsertCount(Counter counter) {
    counterRepository.save(counter);
  }

  @Override
  public void clearCount(Integer id) {
    counterRepository.deleteById(id);
  }
}
