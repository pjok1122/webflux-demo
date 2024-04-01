package com.example.webfluxtest;

import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class Ex2_Mono_Creation {

  @Test
  @DisplayName("Mono.block(): 데이터를 방출할 때까지 main thread를 blocking")
  void mono_block() {
    //given
    String blocked = Mono.just("Hello, WebFlux")
                         .log()
                         .block();
    System.out.println(blocked);
  }

  @Test
  @DisplayName("구독자가 없는 경우")
  void mono_just_v1() {
    //given
    Mono<Integer> mono = Mono.just(1);

    System.out.println(mono.log());
  }

  @Test
  @DisplayName("구독자가 있어야만 데이터가 방출됨.")
  void mono_just() {
    //given
    Mono<Integer> mono = Mono.just(1);

    mono.log().subscribe(System.out::println);
  }

  @Test
  @DisplayName("Mono.empty()는 데이터를 방출하지 않음.")
  void mono_empty() {
    //given
    Mono<Integer> mono = Mono.empty();

    mono.log().subscribe(System.out::println);
  }

  @Test
  @DisplayName("Mono.just와 달리 데이터 생성을 지연시킴. 비동기 상황에서 유용하게 쓸수 있음.")
  void mono_defer() {
    Mono.defer(() -> Mono.just("Hello, WebFlux"))
        .log()
        .subscribe(System.out::println);
  }

  @Test
  @DisplayName("데이터가 있는지 없는지 모를 때! justOrEmpty")
  void mono_optional() {
    Mono.justOrEmpty(null)
        .log()
        .subscribe(System.out::println);
  }

  @Test
  @DisplayName("Mono.defaultIfEmpty(): 데이터가 없을때 초기값을 주고 싶은 경우: sync")
  void mono_defaultIfEmpty() {
    Mono.justOrEmpty(null)
        .defaultIfEmpty("def")
        .subscribe(System.out::println);
  }

  @Test
  @DisplayName("Mono.switchIfEmpty(): 데이터가 없을때 다른 데이터로 대체하고 싶은 경우")
  void mono_switchIfEmpty() {
    Mono.justOrEmpty(null)
        .switchIfEmpty(Mono.just("def"))
        .subscribe(System.out::println);
  }

  @Test
  @DisplayName("Mono.fromCompletionStage(): CompletableFuture를 Mono로 변환")
  void mono_fromCompletionStage() {
    Mono.fromCompletionStage(CompletableFuture.completedFuture("Hello, WebFlux"))
        .log()
        .subscribe(System.out::println);
  }


}
