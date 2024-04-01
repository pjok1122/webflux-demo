package com.example.webfluxtest;

import java.time.Duration;
import java.util.Arrays;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class Ex3_Flux_Creation {

  @Test
  @DisplayName("구독자가 없는 경우")
  void flux_just_v1() {
    //given
    Flux<Integer> flux = Flux.just(1, 2, 3, 4, 5);
    flux.log();
  }

  @Test
  @DisplayName("구독자가 있어야만 데이터가 방출됨.")
  void flux_just() {
    //given
    Flux<Integer> flux = Flux.just(1, 2, 3, 4, 5);

    flux.log().subscribe(System.out::println);
  }

  @Test
  @DisplayName("flux.empty()는 데이터를 방출하지 않음.")
  void flux_empty() {
    //given
    Flux<Integer> flux = Flux.empty();

    flux.log().subscribe(System.out::println);
  }

  @Test
  @DisplayName("Flux.just와 달리 데이터 생성을 지연시킴. 비동기 상황에서 유용하게 쓸수 있음.")
  void flux_defer() {
    Flux.defer(() -> Flux.just(1, 2, 3, 4, 5))
        .log()
        .subscribe(System.out::println);
  }

  @Test
  @DisplayName("Flux.fromIterable(), Flux.fromArray(), Flux.fromStream()")
  void flux_fromIterable() {
    Flux.fromIterable(Arrays.asList(1, 2, 3, 4, 5))
        .log()
        .subscribe(System.out::println);
  }

  @Test
  @DisplayName("flux range")
  void flux_range() {
    Flux.range(1, 5)
        .log()
        .subscribe(System.out::println);
  }

  @Test
  @DisplayName("flux interval")
  void flux_interval() throws InterruptedException {
    Flux.interval(Duration.ofSeconds(1))
        .log()
        .subscribe(System.out::println);
    Thread.sleep(5000);
  }
}
