package com.example.webfluxtest;

import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class Ex1_Publisher_Subscriber {


  @Test
  void test() throws InterruptedException {
    Iterable<Integer> iter = Arrays.asList(1, 2, 3, 4, 5);
    ExecutorService es = Executors.newSingleThreadExecutor();
    // 구독하는 방법에 대한 기준만 정의.
    Publisher p = new Publisher() {
      @Override
      public void subscribe(Subscriber subscriber) {
        Iterator<Integer> it = iter.iterator();     //DB에서 조회한 데이터라고 생각.

        subscriber.onSubscribe(new Subscription() {
          @Override
          public void request(long n) {

            es.execute(() -> {
              int i = 0;
              try {
                while (i++ < n) {
                  if (it.hasNext()) {
                    subscriber.onNext(it.next());
                  } else {
                    subscriber.onComplete();
                    break;
                  }
                }
              } catch (RuntimeException e) {
                subscriber.onError(e);
              }
            });
          }

          @Override
          public void cancel() {

          }
        });
      }
    };

    Subscriber<Integer> sub = new Subscriber<>() {
      Subscription subscription;

      @Override
      public void onSubscribe(Subscription subscription) {
        // 구독이라는 중간 매개체를 전달 받음. (처리 속도를 조절할 수 있음)
        // Publisher -> [Subscription] -> Subscriber
        System.out.println(Thread.currentThread().getName() + " onSubscribe");
        this.subscription = subscription;
        this.subscription.request(2L);                 //subscriber가 처리할 수 있는 데이터의 양을 요구.
        // request 즉시 데이터를 달라는 의미는 아니고 구독에 대한 상세 설정 같은 느낌. 따라서 pull 방식과 다름 (pub/sub의 속도 차 조절도 가능해짐).
        // 여기서 속도 조절이 가능함.

      }

      int bufferSize = 2;

      @Override
      public void onNext(Integer item) {  //publisher가 데이터를 주면 onNext에서 받음.
        System.out.println(Thread.currentThread().getName() + " onNext - " + item);

        //처리가 끝났으니 publisher에게 '구독'을 통해서 현재 몇 개 처리할 수 있는지 상황을 알려줌. (버퍼의 사이즈가 절반찼을 때 받는다거나 등등)
        if (--bufferSize <= 0) {
          bufferSize = 2;
          this.subscription.request(2);
        }
      }

      @Override
      public void onError(Throwable throwable) {      //에러가 발생하면 해당 메서드가 받음. 따라서 try-catch 구문이 필요가 없음. 이 메서드가 호출되면 더 이상의 구독은 종료.
        System.out.println("onError: " + throwable.getMessage());
      }

      @Override
      public void onComplete() {      //publisher가 더 이상 처리할 수 있는 데이터가 없을 때 subscriber의 onComplete 메서드를 호출해줄 수 있음. 이 메서드가 호출되면 더 이상의 구독은 종료.
        System.out.println(Thread.currentThread().getName() + " onComplete");
      }
    };

    p.subscribe(sub);

    es.awaitTermination(10, TimeUnit.SECONDS);
    es.shutdown();
  }
}
