package com.example.webfluxtest;

import java.time.Duration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class Ex9_doOnXxx_Operator {

  @Test
  @DisplayName("Mono.doOnNext(): 데이터가 방출될 때, 어떤 작업을 수행함.")
  void mono_doOnNext() {
    Mono.just(1)
        .doOnNext(data -> System.out.println("doOnNext: " + data))
        .subscribe(System.out::println);
  }

  @Test
  @DisplayName("Flux.doOnNext(): 데이터가 방출될 때, 어떤 작업을 수행함.")
  void flux_doOnNext() {
    Flux.just("abc", "def")
        .doOnNext(data -> System.out.println("doOnNext: " + data))
        .log()
        .subscribe(item -> System.out.println("아이템이 방출될 때마다:" + item),
                   err -> System.out.println("에러가 발생했을 때: onError()" + err),
                   () -> System.out.println("모든 아이템이 방출되었을 때: onComplete()"));
  }

  @Test
  @DisplayName("mono.doOnSuccess")
  void mono_doOnSuccess() {
    Mono.empty()
        .doOnSuccess(data -> System.out.println("doOnSuccess: " + data))
        .doOnNext(data -> System.out.println("doOnNext: " + data))
        .subscribe();
  }

  @Test
  @DisplayName("flux.doOnComplete() : onComplete()이 호출될 때 수행.")
  void mono_doOnComplete() {
    Flux.just("abc", "def")
        .doOnComplete(() -> System.out.println("doOnComplete"))
        .subscribe(System.out::println);
  }

  @Test
  @DisplayName("flux.doOnError")
  void flux_doOnError() {
    Flux.error(new RuntimeException("에러 발생"))
        .filter(data -> data.equals("abc"))
        .doOnError(err -> System.out.println("doOnError"))
        .subscribe(System.out::println, System.out::println);
  }
}
