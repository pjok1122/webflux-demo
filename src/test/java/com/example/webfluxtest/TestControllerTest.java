package com.example.webfluxtest;

import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
class TestControllerTest {

  private RestTemplate restTemplate = new RestTemplate();

  @Test
  public void test() throws InterruptedException {
    ExecutorService es = Executors.newFixedThreadPool(100);
    for (int i = 0; i < 100; i++) {
      es.execute(() -> {
        String result = restTemplate.getForObject("http://localhost:8080/v4/test", String.class);
        System.out.println(result);
      });
    }

    es.awaitTermination(10, TimeUnit.SECONDS);
    es.shutdown();

  }


}