package com.example.webfluxtest;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class TestController {

  private final WebClient webClient = WebClient.builder()
                                               .baseUrl("http://localhost:8080/") // 기본 URL 설정
                                               .defaultHeader(HttpHeaders.CONTENT_TYPE,
                                                              MediaType.APPLICATION_JSON_VALUE) // 기본 헤더 설정
                                               .build();

  @GetMapping("/v1/test")
  public Mono<String> testV1() {
    System.out.println("[CALLED] V1");
    return Mono.just("helloV1");
  }

  @GetMapping("/v2/test")
  public String testV2() {
    System.out.println("[CALLED] V2");
    return "helloV2";
  }

  @GetMapping("/v3/test")
  public Mono<String> testV3() {
    System.out.println("[CALLED] V3");
    Mono<String> hello = webClient.get()
                                  .uri("http://localhost:8080/v1/test")
                                  .retrieve()
                                  .bodyToMono(String.class);

    Mono<String> helloV2 = webClient.get()
                                    .uri("http://localhost:8080/v2/test")
                                    .retrieve()
                                    .bodyToMono(String.class);

    // hello -(onNext)-> zip : subscribe
    // helloV2 -(onNext)-> zip : subscribe
    // zip -> (onNext) -> flatMap : subscribe
    // flatMap -(onNext) -> Spring 누군가! : subscribe
    return Mono.zip(hello.log(), helloV2.log())
               .log()
               .flatMap(tuple -> Mono.just(tuple.getT1() + tuple.getT2()))
               .log();
  }

  @GetMapping("/v4/test")
  public Mono<String> helloV4() {
    System.out.println("[CALLED] V4");
    Mono<String> hello = webClient.get()
                                  .uri("http://localhost:8080/v1/test")
                                  .retrieve()
                                  .bodyToMono(String.class);

    Mono<String> helloV2 = webClient.get()
                                    .uri("http://localhost:8080/v2/test")
                                    .retrieve()
                                    .bodyToMono(String.class);

    // hello -(onNext)-> zip : subscribe
    // helloV2 -(onNext)-> zip : subscribe
    // zip -> (onNext) -> flatMap : subscribe
    // flatMap -(onNext) -> Spring 누군가! : subscribe
    return Mono.zip(hello.log(), helloV2.log())
               .log()
               .flatMap(tuple -> Mono.just(tuple.getT1() + tuple.getT2()))
               .delayElement(Duration.ofSeconds(3))
               .log();
  }

  @GetMapping("/v5/test")
  public Mono<String> helloV5() {
    System.out.println("[CALLED] V5");
    Mono<String> hello = webClient.get()
                                  .uri("http://localhost:8080/v1/test")
                                  .retrieve()
                                  .bodyToMono(String.class);

    Mono<String> helloV2 = webClient.get()
                                    .uri("http://localhost:8080/v2/test")
                                    .retrieve()
                                    .bodyToMono(String.class);

    // hello -(onNext)-> zip : subscribe
    // helloV2 -(onNext)-> zip : subscribe
    // zip -> (onNext) -> flatMap : subscribe
    // flatMap -(onNext) -> Spring 누군가! : subscribe
    return Mono.zip(hello.log(), helloV2.log())
               .log()
               .flatMap(tuple -> Mono.just(tuple.getT1() + tuple.getT2()))
               .delayElement(Duration.ofSeconds(3))
               .log();
  }

}
