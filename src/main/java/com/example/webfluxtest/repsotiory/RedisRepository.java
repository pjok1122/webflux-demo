package com.example.webfluxtest.repsotiory;

import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class RedisRepository {

  private ConcurrentHashMap<String, Integer> redis = new ConcurrentHashMap<>();

  public Mono<Integer> set(String key, int value) {
    return Mono.justOrEmpty(redis.put(key, value) == null ? value : null);
  }

  public Mono<Integer> setIfAbsent(String key, int value) {
    return Mono.justOrEmpty(redis.putIfAbsent(key, value) == null ? value : null);
  }

  public Mono<Void> delete(String key) {
    return Mono.justOrEmpty(redis.remove(key))
               .then();
  }

  public Mono<Integer> get(String key) {
    return Mono.justOrEmpty(redis.get(key));
  }

}
