package com.example.webfluxtest;

import java.util.Arrays;
import java.util.function.Function;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class Ex4_Transform_Operator {

  @Test
  @DisplayName("Mono.map(): 데이터를 변환할 때 사용. Blocking")
  void mono_map() {
    //given
    Mono.just("abc")
        .log()
        .map(String::length)
        .log()
        .subscribe(System.out::println);
  }

  @Test
  @DisplayName("Flux.map(): 데이터를 변환할 때 사용. Blocking")
  void flux_map() {
    //given
    Flux.just("abc", "def", "ghi")
        .log()
        .map(String::length)
        .log()
        .subscribe(System.out::println);
  }

  @Test
  @DisplayName("Mono.flatMap(): 데이터를 변환할 때 사용. non-blocking")
  void mono_flatMap() {
    //given
    Mono.just("abc")
        .log()
        .flatMap(string -> Mono.just(string.length()))
        .log()
        .subscribe(System.out::println);
  }

  @Test
  @DisplayName("Flux.flatMap(): 데이터를 변환할 때 사용. non-blocking")
  void flux_flatMap() {
    //given
    Flux.just("abc", "def", "ghi")
        .log()
        .flatMap(string -> Flux.just(string.length()))
        .log()
        .subscribe(System.out::println);
  }

  @Test
  @DisplayName("Mono<List>를 Flux로 변환")
  void mono_flatMapMany() {
    Mono.just(Arrays.asList(1, 2, 3, 4, 5))
        .flatMapMany(Flux::fromIterable)
        .log()
        .subscribe(System.out::println);

    Mono.just(Arrays.asList(1, 2, 3, 4, 5))
        .flatMapIterable(Function.identity())
        .log()
        .subscribe(System.out::println);
  }

  @Test
  @DisplayName("Flux.collectList() : Flux를 Mono<List>로 변환 : blocking")
  void flux_collectList() {
    //given
    Flux.just("abc", "de", "fgh")
        .collectList()  //blocking
        .subscribe(System.out::println);
  }

  @Test
  @DisplayName("Flux.collectMap() : Flux를 Mono<Map>로 변환 : blocking")
  void flux_collectMap() {
    //given
    Flux.just("abc", "de", "fgh")
        .collectMap(String::length)  //blocking
        .subscribe(System.out::println);
  }

  @Test
  @DisplayName("Flux.collectMultimap() : Flux를 Mono<Map>로 변환 : blocking")
  void flux_collectMultimap() {
    //given
    Flux.just("abc", "de", "fgh")
        .collectMultimap(String::length)  //blocking
        .subscribe(System.out::println);
  }

  @Test
  @DisplayName("Flux.groupBy() : Flux를 Flux<GroupedFlux>로 변환")
  void flux_groupBy() {
    //given
    Flux.just("abc", "de", "fgh")
        .groupBy(String::length)
        .flatMap(groupedFlux -> groupedFlux.collectList())
        .subscribe(System.out::println);
  }
}
