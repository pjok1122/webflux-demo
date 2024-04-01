package com.example.webfluxtest;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class Ex5_Filter_Operator {

  @Test
  @DisplayName("Mono.filter()는 데이터를 걸러냄.")
  void mono_filter() {
    //given
    Mono.just("abc")
        .filter(string -> string.length() > 10)
        .subscribe(System.out::println);
  }

  @Test
  @DisplayName("Flux.filter()는 데이터를 걸러냄.")
  void flux_filter() {
    //given
    Flux.just("abc", "de", "fgh")
        .filter(string -> string.length() > 2)
        .subscribe(System.out::println);
  }

  @Test
  @DisplayName("Mono.filterWhen()는 데이터를 걸러냄. 비동기 상황에서 유용하게 쓸수 있음.")
  void mono_filterWhen() {
    Mono.just("abc")
        .filterWhen(string -> Mono.just(string.length() < 10))
        .subscribe(System.out::println);
  }

  @Test
  @DisplayName("Flux.filterWhen()는 데이터를 걸러냄. Produce/Consume하는 쓰레드가 EventLoop로 동일함. 따라서 병렬 처리가 생각대로 잘 동작하지 않음. 따라서, 병렬 처리를 하려면 flatMap을 사용해야 함.")
  void flux_filterWhen() throws InterruptedException {
    //생각과 다르게 동작하는 코드 (synchronous하게 동작함)
    AtomicInteger count = new AtomicInteger(5);
    Flux.just("abc", "de", "fgh")
        .filterWhen(string -> Mono.just(string.length() < 10)
                                  .delayElement(Duration.ofSeconds(count.getAndAdd(-2)))
                                  .log())
        .subscribe(System.out::println);

    Thread.sleep(10_000);
    System.out.println("===== 개선 코드 ====");
    AtomicInteger count2 = new AtomicInteger(5);

    Flux.just("abc", "de", "fgh")
        .flatMap(str -> Mono.justOrEmpty(str.length() < 10 ? str : null)
                            .delayElement(Duration.ofSeconds(count2.getAndAdd(-2)))
                            .log())
        .subscribe(System.out::println);
    Thread.sleep(10_000);
  }

  @Test
  @DisplayName("Flux.hasElements(): 데이터가 하나라도 있으면 바로 반환.")
  void flux_hasElement() {
    //given
    Flux.just("abc", "de", "fgh")
        .hasElements()
        .subscribe(System.out::println);
  }

  @Test
  @DisplayName("distinct() : 중복된 데이터를 걸러냄.")
  void flux_distinct() {
    //given
    Flux.just("abc", "de", "fgh", "abc", "de")
        .distinct()
        .subscribe(System.out::println);
  }

  @Test
  @DisplayName("Flux.skip, take: 데이터를 스킵하거나 필요한 개수만큼 가져옴.")
  void flux_skip_take() {
    Flux.just("abc", "def", "ghi", "jkl", "mno")
        .skip(2)
        .take(2)
        .subscribe(System.out::println);
  }
}
