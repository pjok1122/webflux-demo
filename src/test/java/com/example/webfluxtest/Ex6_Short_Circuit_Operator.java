package com.example.webfluxtest;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class Ex6_Short_Circuit_Operator {

  @Test
  @DisplayName("Mono.hasElement()")
  void mono_hasElement() {
    //given
    Mono.justOrEmpty("abc")
        .hasElement()
        .subscribe(System.out::println);
  }

  @Test
  @DisplayName("Flux.hasElements(): 데이터가 하나라도 있으면 바로 반환.")
  void flux_hasElements() {
    //given
    Flux.just("abc", "de", "fgh")
        .hasElements()
        .subscribe(System.out::println);
  }

  @Test
  @DisplayName("Flux.any(): 데이터가 하나라도 조건을 만족하면 Mono<Boolean>을 반환.")
  void flux_any() {
    //given
    Flux.just("abc", "de", "fgh")
        .any(string -> string.length() > 2)
        .subscribe(System.out::println);
  }

  @Test
  @DisplayName("Flux.all(): 모든 데이터가 조건을 만족하면 Mono<Boolean>을 반환.")
  void flux_all() {
    //given
    Flux.just("abc", "de", "fgh")
        .all(string -> string.length() > 2)
        .subscribe(System.out::println);
  }

  @Test
  @DisplayName("Flux.next(): Flux 중 가장 먼저 emit 되는 원소 하나를 가지고 Mono를 만들 때 사용.")
  void flux_next() {
    //given
    Flux.just("abc", "de", "fgh")
        .next()
        .subscribe(System.out::println);
  }

}
