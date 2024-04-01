package com.example.webfluxtest;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class Ex7_Utils_Operator {

  @Test
  @DisplayName("Flux.log(): 데이터의 흐름을 확인할 때 사용.")
  void flux_log() {
    //given
    Flux.just("abc", "de", "fgh")
        .log()
        .subscribe(System.out::println);
  }

  @Test
  @DisplayName("elapsed(): 데이터가 방출되는데 걸린 시간을 알려줌.")
  void mono_elapsed() throws InterruptedException {
    Mono.just("abc")
        .delayElement(Duration.ofSeconds(3))
        .elapsed()
        .subscribe(tuple -> {
          System.out.println("elapsed : " + tuple.getT1() + "ms");
          System.out.println("data : " + tuple.getT2());
        });

    Thread.sleep(5_000);
  }

  @Test
  @DisplayName("Mono.delayElement(): 데이터를 지연해서 방출함.")
  void mono_delay() throws InterruptedException {
    Mono.just("abc").delayElement(Duration.ofSeconds(3))
        .subscribe(System.out::println);

    Thread.sleep(5_000);
  }

  @Test
  @DisplayName("Mono.delaySubscription(): 구독을 지연함.")
  void mono_delaySubscription() throws InterruptedException {
    Mono.just("abc").delaySubscription(Duration.ofSeconds(3))
        .subscribe(System.out::println);

    Thread.sleep(5_000);
  }

  @Test
  @DisplayName("Flux.delayElements(): 데이터를 지연해서 방출함. item 하나마다 적용됨.")
  void flux_delay() throws InterruptedException {
    Flux.just("abc", "def").delayElements(Duration.ofSeconds(1))
        .subscribe(System.out::println);

    Thread.sleep(3_000);
  }

  @Test
  @DisplayName("Mono.delayUntil(): Trigger 함수가 성공할 때까지 지연함.")
  void mono_delayUntil() throws InterruptedException {
    Mono.just("abc").delayUntil(data -> Mono.just("def").delayElement(Duration.ofSeconds(3)))
        .subscribe(System.out::println);

    Thread.sleep(5_000);
  }

  @Test
  @DisplayName("Flux.delayUntil(): Trigger 함수가 성공할 때까지 지연함. item 하나마다 적용됨. : sequence가 끝나야 다음 sequence가 시작됨.")
  void flux_delayUntil() throws InterruptedException {
    Flux.just("abc", "def")
        .delayUntil(data -> Flux.just("ghi").delayElements(Duration.ofSeconds(2)))
        .subscribe(System.out::println);

    Thread.sleep(5_000);
  }

  @Test
  @DisplayName("Mono.cache(): 데이터를 캐싱함. 구독자가 여러명일 때 유용함.")
  void mono_cache() throws InterruptedException {
    Mono<String> abc = Mono.just("abc").delayElement(Duration.ofSeconds(3)).cache().log();

    abc.subscribe(System.out::println);
    Thread.sleep(3_000);
    abc.subscribe(System.out::println);

    Thread.sleep(1_000);
  }

  @Test
  @DisplayName("Flux.cache(): 데이터를 캐싱함. 구독자가 여러명일 때 유용함. : Flux의 item 하나하나마다 캐싱됨. 캐싱되면 선 방출")
  void flux_cache() throws InterruptedException {
    Flux<String> abc = Flux.just("abc", "def").delayElements(Duration.ofSeconds(3)).cache().log();

    abc.subscribe(System.out::println);
    Thread.sleep(4_000);  //abc가 캐싱되었으므로 바로 방출됨.
    abc.subscribe(System.out::println);

    Thread.sleep(3_000);
  }

  @Test
  @DisplayName("Mono.repeat(): 데이터를 반복해서 방출함.")
  void mono_repeat() throws InterruptedException {
    Mono.just("abc").delayElement(Duration.ofSeconds(1))
        .repeat(3)
        .subscribe(System.out::println);

    Thread.sleep(5_000);
  }

  @Test
  @DisplayName("Flux.repeat(): 데이터를 반복해서 방출함.")
  void flux_repeat() throws InterruptedException {
    Flux.just("abc", "def").delayElements(Duration.ofSeconds(1))
        .repeat(1)
        .subscribe(System.out::println);

    Thread.sleep(5_000);
  }

  @Test
  @DisplayName("Mono.timeout(): 시간이 지나면 에러를 발생시킴.")
  void mono_timeout() throws InterruptedException {
    Mono.just("abc").delayElement(Duration.ofSeconds(3))
        .timeout(Duration.ofSeconds(2))
        .subscribe(System.out::println, System.out::println);

    Thread.sleep(5_000);
  }

  @Test
  @DisplayName("Mono.timeout(): 시간이 지나면 에러를 발생시킴. 에러가 발생했을 때, 대체 데이터를 방출함. fallback pattern")
  void mono_timeout2() throws InterruptedException {
    Mono.just("abc").delayElement(Duration.ofSeconds(3))
        .timeout(Duration.ofSeconds(2), Mono.just("timeout"))
        .subscribe(System.out::println, System.out::println);

    Thread.sleep(5_000);
  }

  @Test
  @DisplayName("Flux.timeout(): 시간이 지나면 에러를 발생시킴. : Flux item 하나하나마다 timeout이 적용됨.")
  void flux_timeout() throws InterruptedException {
    Flux.just("abc", "def").delayElements(Duration.ofSeconds(3))
        .timeout(Duration.ofSeconds(4))
        .subscribe(System.out::println, System.out::println);

    Thread.sleep(7_000);
  }

  @Test
  @DisplayName("Flux.timeout(): 시간이 지나면 에러를 발생시킴. 에러가 발생했을 때, 대체 데이터를 방출함. fallback pattern. 이미 방출된 데이터가 있을 수도 있음.")
  void flux_timeout2() throws InterruptedException {
    Flux.just("abc", "def").delayElements(Duration.ofSeconds(2))
        .timeout(Duration.ofSeconds(1), Flux.just("fallback"))
        .subscribe(System.out::println, System.out::println);

    Thread.sleep(5_000);
  }

  @Test
  @DisplayName("Mono.retry(): 에러가 발생했을 때, 재시도함.")
  void mono_retry() throws InterruptedException {
    AtomicInteger count = new AtomicInteger(0);
    Mono.just("abc").delayElement(Duration.ofSeconds(1))
        .map(data -> {
          System.out.println("map: " + count.get());
          if (count.getAndIncrement() != 3) {
            throw new RuntimeException("retry!");
          }
          return data;
        })
        .retry(2)
        .subscribe(System.out::println, System.out::println);

    Thread.sleep(5_000);
  }

  @Test
  @DisplayName("Flux.retry(): 에러가 발생했을 때, 재시도함. : Flux 데이터 전체에 대한 retry임. 앞에 데이터가 방출된 상태여도 다시 재방출함.")
  void flux_retry() throws InterruptedException {
    AtomicInteger count = new AtomicInteger(0);
    Flux.just("abc", "def")
        .flatMap(data -> {
          int c = count.getAndIncrement();
          System.out.println("flatMap: " + c);
          if (c == 1) {
            return Mono.error(new RuntimeException("retry!"));
          }
          return Mono.just(data);
        })
        .retry(3)
        .subscribe(System.out::println, System.out::println);

    Thread.sleep(5_000);
  }


}
