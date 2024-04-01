package com.example.webfluxtest;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class ExternalApi {

  private static final Random random = new Random();

  @GetMapping("/users/{id}/basic-info")
  public Mono<?> getBasicInfo(@PathVariable Long id) {
    int i = random.nextInt(2);// 0 ~ 1
    if (i == 0) {
      return Mono.error(new RuntimeException("error"));
    }

    System.out.println("randomInt: " + i);
    return Mono.just(Map.of("address", "서울시 강남구",
                            "name", "홍길동",
                            "age", 30,
                            "id", id));
  }

  @GetMapping("/ad/banners")
  public Flux<String> getBanners(@RequestParam Long userId) {
    int delaySeconds = random.nextInt(4);   // 0 ~ 3
    return Mono.just(List.of("http://localhost:8080/banner1",
                             "http://localhost:8080/banner2",
                             "http://localhost:8080/banner3"))
               .delayElement(Duration.ofSeconds(delaySeconds))
        .flatMapIterable(it -> it);
  }

}
