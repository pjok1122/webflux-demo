package com.example.webfluxtest;

import java.time.Duration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class Ex8_Merge_Concat_Operator {

  @Test
  @DisplayName("Mono.concatWith(): 두 개의 Mono를 합침. 단, 첫 번째 Mono가 완료된 후에 두 번째 Mono가 실행됨.")
  void mono_concatWith() throws InterruptedException {
    Mono.just("abc").concatWith(Mono.just("def"))
        .subscribe(System.out::println);

    //test
    Mono.just("abc").delayElement(Duration.ofSeconds(3))
        .concatWith(Mono.just("def").delayElement(Duration.ofSeconds(3)))
        .elapsed()
        .flatMap(tuple -> {
          System.out.println("elapsed : " + tuple.getT1() + "ms");
          return Mono.just(tuple.getT2());
        })
        .subscribe(System.out::println);

    Thread.sleep(10_000);
  }

  @Test
  @DisplayName("Flux.concatWith(): 두 개의 Flux를 합침. 단, 첫 번째 Flux가 완료된 후에 두 번째 Flux가 실행됨.")
  void flux_concatWith() {
    //given
    Flux.just("abc", "de")
        .concatWith(Flux.just("fgh"))
        .subscribe(System.out::println);
  }

  @Test
  @DisplayName("Mono.zip(): 두 개의 Mono를 합침. 두 Mono를 동시에 수행하고, 하나의 Mono<Tuple>로 합침. 둘 중 하나가 empty이거나 에러가 발생하면 동작하지 않음.")
  void mono_zip() throws InterruptedException {
    Mono.zip(Mono.just("abc"), Mono.just("def"))
        .subscribe(System.out::println);

    Mono.just("abc").delayElement(Duration.ofSeconds(3))
        .zipWith(Mono.just("def").delayElement(Duration.ofSeconds(3)))
        .elapsed()
        .flatMap(tuple -> {
          System.out.println("elapsed : " + tuple.getT1() + "ms");
          return Mono.just(tuple.getT2());
        })
        .subscribe(System.out::println);

    Thread.sleep(10_000);
  }

  @Test
  @DisplayName("Mono.zipWhen(): 두 개의 Mono를 합침.")
  void mono_zipWhen() throws InterruptedException {
    Mono.just("hello")
        .zipWhen(data -> Mono.just(data +"  world"))
        .subscribe(System.out::println);
  }

  @Test
  @DisplayName("Flux.zip(): 두 개의 Flux를 합침. 두 Flux를 동시에 수행하고, 하나의 Flux<Tuple>로 합침.")
  void flux_zip() throws InterruptedException {
    //given
    Flux.zip(Flux.just("abc", "de"), Flux.just("fgh", "ijk"))
        .subscribe(System.out::println);

    Flux.just("abc", "de").delayElements(Duration.ofSeconds(1))
        .zipWith(Flux.just("fgh", "ijk").delayElements(Duration.ofSeconds(1)))
        .elapsed()
        .flatMap(tuple -> {
          System.out.println("elapsed : " + tuple.getT1() + "ms");
          return Flux.just(tuple.getT2());
        })
        .subscribe(System.out::println);

    Thread.sleep(5_000);
  }

  @Test
  @DisplayName("Mono.merge(): 두 개의 Mono를 합침. 두 Mono를 동시에 수행하고, 하나의 Mono로 합침. 하지만 둘 중 먼저 끝나는 Mono의 데이터가 먼저 방출됨.")
  void mono_merge() throws InterruptedException {
    Mono.just("abc").delayElement(Duration.ofSeconds(2))
        .mergeWith(Mono.just("def").delayElement(Duration.ofSeconds(1)))
        .elapsed()
        .flatMap(tuple -> {
          System.out.println("elapsed : " + tuple.getT1() + "ms");
          return Mono.just(tuple.getT2());
        })
        .subscribe(System.out::println);

    Thread.sleep(3_000);
  }

  @Test
  @DisplayName("Flux.mergeWith(): 두 개의 Flux를 합침. 두 Flux를 동시에 수행하고, 하나의 Flux로 합침. 하지만 둘 중 먼저 끝나는 Flux의 데이터가 먼저 방출됨.")
  void flux_mergeWith() throws InterruptedException {
    //given
    Flux.just("abc", "de").delayElements(Duration.ofSeconds(1))
        .mergeWith(Flux.just("fgh"))
        .subscribe(System.out::println);

    Thread.sleep(3_000);
  }
}
