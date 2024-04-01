package com.example.webfluxtest;

import com.example.webfluxtest.AnswerSheetController.BasicInfoResponse;
import com.example.webfluxtest.AnswerSheetController.TransferRequest;
import com.example.webfluxtest.AnswerSheetController.UserInfoResponse;
import com.example.webfluxtest.repsotiory.RedisRepository;
import com.example.webfluxtest.repsotiory.UserAccountRepository;
import com.example.webfluxtest.repsotiory.UserAccountRepository.UserAccount;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class PracticeController {

  private static final String USER_ACCOUNT_KEY_FORMAT = "";
  private static final String USER_ACCOUNT_LOCK_KEY_FORMAT = "";


  private final RedisRepository redisRepository;
  private final UserAccountRepository userAccountRepository;


  /**
   * 유저의 계좌에 request.balance 입금/출금하는 API
   * <p>
   * 해당 유저의 잔고를 조회하여 request.balance를 더한 값을 저장합니다. 이때, 잔고는 음수가 될 수 없습니다. 유저의 잔고는 항상 캐싱되어있어야 한다.
   * <p>
   *
   * 해당 유저의 잔고 업데이트가 성공적으로 수행될 경우 "success"를 반환한다.
   * 해당 유저의 잔고가 모자를 경우 응답으로 "fail"을 반환하고, 상태코드는 200으로 응답합니다.
   */
  @PostMapping("/users/account")
  public Mono<String> addBalance(@RequestBody UserAccount request) {
    return Mono.just("success");
  }

  /**
   * 유저의 정보를 조회해서 반환하는 API 유저의 정보는 MSA 환경으로 구성되어 있으며, 각 서비스는 비동기로 통신한다. 아래의 필요한 정보 목록을 합하여 반환한다. (externalApi.java 참고)
   * 1. 유저의 기본 정보 2. 유저의 계좌 총액 3. 유저에게 보여질 배너 목록
   * <p>
   * 1번 API는 필수적으로 응답에 포함되어있어야 한다. 재시도를 2번 시도하고, 만약 그래도 조회하지 못한 경우라면 응답을 포함하지 않고 500 에러를 반환한다.
   * <p>
   * 2번 API는 필수적으로 응답에 포함되어있어야 한다. 데이터를 빠르게 조회할 수 있어야 하므로 Redis cache를 사용해야 한다.
   * <p>
   * 3번의 API는 외부 API로부터 데이터를 가져오는데 간헐적으로 지연이 발생할 수 있다. 3번의 API를 조회하는데 2초 이상 소요될 경우, 응답에 포함하지 않고 배너 목록을 빈 형태로 반환한다.
   */

  @GetMapping("/users/{id}/info")
  public Mono<UserInfoResponse> getUserInfo(@PathVariable String id) {
    return Mono.empty();
  }

  /**
   * sender가 receiver에게 amount만큼 송금하는 API
   * <p>
   * 이 연산은 반드시 원자성을 보장해야 한다. 즉, 송금이 실패하면 sender/receiver의 계좌 잔액은 변하지 않아야 한다.
   * <p>
   * 해당 요청은 동시에 발생할 수 있다. 따라서 Redis Lock을 이용해서 동시성을 제어해야 한다.
   * 만약 어떤 작업이 진행 중이라면, 그냥 실패로 응답한다.
   * <p>
   * 각 유저의 총 계좌 잔액은 항상 Redis 캐싱되어있어야 한다.
   */

  @PostMapping("/transfer")
  public Mono<Void> transfer(@RequestBody AnswerSheetController.TransferRequest request) {
    return Mono.empty();
  }

  @Data
  static class TransferRequest {

    public Integer sender;
    public Integer receiver;
    public int amount;
  }

  @Data
  static class BasicInfoResponse {

    public String address;
    public String name;
    public int age;
    public int id;
  }

  @Data
  @AllArgsConstructor
  static class UserInfoResponse {

    public AnswerSheetController.BasicInfoResponse basicInfo;
    public Integer account;
    public List<String> banners;
  }


}
