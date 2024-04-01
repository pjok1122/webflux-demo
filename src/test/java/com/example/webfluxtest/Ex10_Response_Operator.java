package com.example.webfluxtest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class Ex10_Response_Operator {

  @Test
  @DisplayName("Flux.onErrorComplete() : onError()가 발생하면, onComplete()로 시그널을 변경한다. 에러를 무시하고 싶을 때 사용한다.")
  void flux_onErrorComplete() {
    Flux.just("abc", "def")
        .concatWith(Flux.error(new RuntimeException("에러 발생")))
        .onErrorComplete()
        .doOnComplete(() -> System.out.println("onComplete()"))
        .subscribe(System.out::println, System.out::println);
  }

  @Test
  @DisplayName("flux.onErrorReturn() : onError()가 발생하면, onComplete()로 시그널을 변경한다. 에러를 무시하고 싶을 때 사용한다.")
  void flux_onErrorReturn() {
    Flux.just("abc", "def")
        .concatWith(Flux.error(new RuntimeException("에러 발생")))
        .onErrorReturn("에러 발생 시 대체할 데이터")
        .doOnComplete(() -> System.out.println("onComplete()"))
        .subscribe(System.out::println, System.out::println);
  }

  @Test
  @DisplayName("flux.onErrorResume() : onError()가 발생하면, 다른 Flux로 대체한다.")
  void flux_onErrorResume() {
    Flux.just("abc", "def")
        .concatWith(Flux.error(new RuntimeException("에러 발생")))
        .onErrorResume(err -> {
          System.out.println("에러 발생: " + err);
          return Flux.just("a1", "a2", "a3");
        })
        .doOnComplete(() -> System.out.println("onComplete()"))
        .subscribe(System.out::println, System.out::println);
  }

  @Test
  @DisplayName("flatMapDelayError() : DelayError()가 붙은 Operator는 모든 item이 방출될 때까지 에러를 무시하고 계속 진행한다. 일부가 실패하더라도 끝까지 실행하고자 할 때 사용한다.")
  void flatMapDelayError() {
    Flux.just(1, 2, 3, 4, 5, 6, 7, 8)
            .flatMapDelayError(i -> {
              if (i % 3 == 0) {
                return Flux.error(new RuntimeException("에러 발생"));
              }
              return Mono.just(i);
            }, 2, 3)
        .doOnComplete(() -> System.out.println("onComplete()"))
        .subscribe(System.out::println, System.out::println);
  }

  @Test
  @DisplayName("then() : onComplete()이벤트가 발생하면, 다른 Mono를 반환한다.")
  void then() {
    Mono.just("db save()")
        .then(Mono.just("SUCCESS"))
        .subscribe(System.out::println);
  }

  @Test
  @DisplayName("thenReturn() : onComplete()이벤트가 발생하면, 다른 Mono를 반환한다.")
  void thenReturn() {
    Mono.just("db save()")
        .thenReturn("SUCCESS")
        .subscribe(System.out::println);
  }
}
