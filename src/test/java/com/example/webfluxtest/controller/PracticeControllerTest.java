package com.example.webfluxtest.controller;

import com.example.webfluxtest.AnswerSheetController;
import com.example.webfluxtest.repsotiory.RedisRepository;
import com.example.webfluxtest.repsotiory.UserAccountRepository.UserAccount;
import java.util.concurrent.atomic.AtomicBoolean;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.test.StepVerifier;

@SpringBootTest
@AutoConfigureWebTestClient
public class PracticeControllerTest {

  @Autowired
  private WebTestClient webTestClient;

  @Autowired
  private RedisRepository redisRepository;


  public static final String ADD_BALANCE_URI = "/users/account";
  public static final String GET_USER_INFO_URI = "/users/{id}/info";

  @Test
  @DisplayName("유저 잔고 증가가 정상적으로 이루어진다.")
  void addBalance_success() {
    webTestClient.post().uri(ADD_BALANCE_URI)
                 .bodyValue(new UserAccount(1, 100))
                 .exchange()
                 .expectStatus().isOk()
                 .expectBody(String.class).isEqualTo("success");

    StepVerifier.create(redisRepository.get(AnswerSheetController.USER_ACCOUNT_KEY_FORMAT.replace("{id}", "1")))
                .expectNext(100)
                .verifyComplete();
  }

  @Test
  @DisplayName("유저 잔고가 음수일 경우 실패한다.")
  void addBalance_failure() {
    webTestClient.post().uri(ADD_BALANCE_URI)
                 .bodyValue(new UserAccount(2, -100))
                 .exchange()
                 .expectStatus().isOk()
                 .expectBody(String.class).isEqualTo("fail");

    StepVerifier.create(redisRepository.get(AnswerSheetController.USER_ACCOUNT_KEY_FORMAT.replace("{id}", "2")))
                .expectNextCount(0)
                .verifyComplete();
  }

  @Test
  @DisplayName("유저 잔고가 정상적으로 증가하고, 캐시 또한 업데이트된다.")
  void addBalance_success2() {
    webTestClient.post().uri(ADD_BALANCE_URI)
                 .bodyValue(new UserAccount(3, 100))
                 .exchange()
                 .expectStatus().isOk()
                 .expectBody(String.class).isEqualTo("success");

    webTestClient.post().uri(ADD_BALANCE_URI)
                 .bodyValue(new UserAccount(3, 100))
                 .exchange()
                 .expectStatus().isOk()
                 .expectBody(String.class).isEqualTo("success");

    StepVerifier.create(redisRepository.get(AnswerSheetController.USER_ACCOUNT_KEY_FORMAT.replace("{id}", "3")))
                .expectNext(200)
                .verifyComplete();
  }

  @Test
  @DisplayName("유저 잔고보다 높은 금액을 출금할 경우, 실패하며 캐싱 값은 변화하지 않는다.")
  void addBalance_fail2() {
    webTestClient.post().uri(ADD_BALANCE_URI)
                 .bodyValue(new UserAccount(4, 150))
                 .exchange()
                 .expectStatus().isOk()
                 .expectBody(String.class).isEqualTo("success");

    webTestClient.post().uri(ADD_BALANCE_URI)
                 .bodyValue(new UserAccount(4, -80))
                 .exchange()
                 .expectStatus().isOk()
                 .expectBody(String.class).isEqualTo("success");

    webTestClient.post().uri(ADD_BALANCE_URI)
                 .bodyValue(new UserAccount(4, -100))
                 .exchange()
                 .expectStatus().isOk()
                 .expectBody(String.class).isEqualTo("fail");

    StepVerifier.create(redisRepository.get(AnswerSheetController.USER_ACCOUNT_KEY_FORMAT.replace("{id}", "4")))
                .expectNext(70)
                .verifyComplete();
  }

  @Test
  @DisplayName("모든 데이터를 정상적으로 다 조회하는 경우")
  void getUserInfo_success() {
    AtomicBoolean isFailed = new AtomicBoolean(false);

    redisRepository.set(AnswerSheetController.USER_ACCOUNT_KEY_FORMAT.replace("{id}", "5"), 100).subscribe();

    for (int i = 0; i < 50; i++) {
      if (i == 49) {
        isFailed.set(true);
      }
      try {
        webTestClient.get().uri(GET_USER_INFO_URI, 5)
                     .exchange()
                     .expectStatus().isOk()
                     .expectBody()
                     .jsonPath("$.basicInfo").isNotEmpty()
                     .jsonPath("$.basicInfo.address").isEqualTo("서울시 강남구")
                     .jsonPath("$.basicInfo.name").isEqualTo("홍길동")
                     .jsonPath("$.basicInfo.age").isEqualTo(30)
                     .jsonPath("$.basicInfo.id").isEqualTo(5)
                     .jsonPath("$.account").isEqualTo(100)
                     .jsonPath("$.banners").isNotEmpty()
                     .jsonPath("$.banners[0]").isEqualTo("http://localhost:8080/banner1")
                     .jsonPath("$.banners[1]").isEqualTo("http://localhost:8080/banner2")
                     .jsonPath("$.banners[2]").isEqualTo("http://localhost:8080/banner3");

        if (1 == 1) {
          System.out.println("[TRY]: " + i);
          break;
        }
      } catch (AssertionError e) {
      }
    }
    Assertions.assertThat(isFailed).isFalse();
  }

  @Test
  @DisplayName("유저 정보를 불러오지 못한 경우")
  void getUserInfo_fail() {
    AtomicBoolean isFailed = new AtomicBoolean(false);
    redisRepository.set(AnswerSheetController.USER_ACCOUNT_KEY_FORMAT.replace("{id}", "6"), 100).subscribe();

    for (int i = 0; i < 50; i++) {
      if (i == 49) {
        isFailed.set(true);
      }
      try {
        webTestClient.get().uri(GET_USER_INFO_URI, 6)
                     .exchange()
                     .expectStatus().is5xxServerError();

        if (1 == 1) {
          System.out.println("[TRY]: " + i);
          break;
        }

      } catch (AssertionError e) {
      }
    }
    Assertions.assertThat(isFailed).isFalse();
  }

  @Test
  @DisplayName("배너 정보를 불러오지 못한 경우")
  void getUserInfo_banner_fail() {
    AtomicBoolean isFailed = new AtomicBoolean(false);
    redisRepository.set(AnswerSheetController.USER_ACCOUNT_KEY_FORMAT.replace("{id}", "7"), 100).subscribe();

    for (int i = 0; i < 50; i++) {
      if (i == 49) {
        isFailed.set(true);
      }
      try {
        webTestClient.get().uri(GET_USER_INFO_URI, 7)
                     .exchange()
                     .expectStatus().isOk()
                     .expectBody()
                     .jsonPath("$.basicInfo").isNotEmpty()
                     .jsonPath("$.basicInfo.address").isEqualTo("서울시 강남구")
                     .jsonPath("$.basicInfo.name").isEqualTo("홍길동")
                     .jsonPath("$.basicInfo.age").isEqualTo(30)
                     .jsonPath("$.basicInfo.id").isEqualTo(7)
                     .jsonPath("$.account").isEqualTo(100)
                     .jsonPath("$.banners.length()").isEqualTo(0);

        if (1 == 1) {
          System.out.println("[TRY]: " + i);
          break;
        }


      } catch (AssertionError e) {
      }
    }
    Assertions.assertThat(isFailed).isFalse();
  }

  @Test
  @DisplayName("account 정보가 없는 경우")
  void getUserInfo_account_fail() {
    AtomicBoolean isFailed = new AtomicBoolean(false);
    for (int i = 0; i < 50; i++) {
      if (i == 49) {
        isFailed.set(true);
      }

      try {
        webTestClient.get().uri(GET_USER_INFO_URI, 8)
                     .exchange()
                     .expectStatus().isOk()
                     .expectBody()
                     .jsonPath("$.basicInfo").isNotEmpty()
                     .jsonPath("$.basicInfo.address").isEqualTo("서울시 강남구")
                     .jsonPath("$.basicInfo.name").isEqualTo("홍길동")
                     .jsonPath("$.basicInfo.age").isEqualTo(30)
                     .jsonPath("$.basicInfo.id").isEqualTo(8)
                     .jsonPath("$.account").isEqualTo(0);

        StepVerifier.create(redisRepository.get(AnswerSheetController.USER_ACCOUNT_KEY_FORMAT.replace("{id}", "8")))
                    .expectNext(0)
                    .verifyComplete();

        if (1 == 1) {
          System.out.println("[TRY]: " + i);
          break;
        }

      } catch (AssertionError e) {
      }
    }
    Assertions.assertThat(isFailed).isFalse();
  }

}
