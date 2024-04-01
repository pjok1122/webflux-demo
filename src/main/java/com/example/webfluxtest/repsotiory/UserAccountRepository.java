package com.example.webfluxtest.repsotiory;

import java.util.HashMap;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class UserAccountRepository {

  private final HashMap<Integer, UserAccount> db = new HashMap<>();

  public Mono<UserAccount> get(Integer id) {
    return Mono.just(db.getOrDefault(id, new UserAccount(id, 0)));
  }

  public Flux<UserAccount> list(Flux<Integer> ids) {
    return ids.flatMap(id -> Mono.just(db.getOrDefault(id, new UserAccount(id, 0))));
  }

  public Mono<UserAccount> set(Integer id, Integer money) {
    UserAccount userAccount = new UserAccount(id, money);
    return Mono.justOrEmpty(db.put(id, userAccount))
               .then(Mono.just(userAccount));
  }

  @AllArgsConstructor
  @Data
  public static class UserAccount {

    private Integer id;
    private Integer balance;
  }

}
