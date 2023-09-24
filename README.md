# 👟 한정판 신발 거래 서비스 Shoe Kream

한정판 신발 거래 플랫폼 Shoe Kream  

[📖 정리 링크](https://www.notion.so/shoekream-2c61bb48605c45959695a142bb6922c7)


[📖 Web API 문서](http://49.50.162.219:8081/swagger-ui/index.html)

## ERD

![image](https://raw.githubusercontent.com/buinq/imageServer/main/img/230783671-cd54f6ee-782c-4b27-871d-cb24b7ed7a27.png)

## 기술적 issue 해결 과정

<br>

* [#1] Controller 예외 처리 로직에 AOP 적용 -  코드 가독성 향상과 비즈니스 로직에 집중하기

  [**Dto Validation 예외 처리를 AOP를 활용해 개선하기**](https://inkyu-yoon.github.io/docs/Language/SpringBoot/ValidationAop)

<br>

* [#2] 서버 사이드 캐싱 적용을 통해 응답 속도 향상과 DB 부하 줄이기 - Redis 활용

  [**Cache개념과 Spring에서의 Cache**](https://percyfrank.github.io/springboot/Cache01/)  
  
  [**Cache 기능을 Redis로 구현하기까지의 과정(1)**](https://percyfrank.github.io/springboot/Cache02/)  
  
  [**Cache 기능을 Redis로 구현하기까지의 과정(2) - LocalDateTime 직렬화/역직렬화 방법**](https://percyfrank.github.io/springboot/Cache03/)  

<br>

* [#3] In-Memory 데이터 스토리지로 사용자 인증 기능 구현하기 - Redis 활용

  [**Redis로 사용자 인증 구현하기**](https://inkyu-yoon.github.io/docs/Language/SpringBoot/RedisAndAuth)


<br>

* [#4] 응답속도 향상을 위한 이미지 리사이징 적용 - AWS Lambda 활용

  [**AWS Lambda를 이용해 이미지 리사이징 적용 - 이미지 로딩 속도 최적화**](https://percyfrank.github.io/springboot/Lambda01/)



<br>

* [#5] 메일 전송 기능에 비동기 처리 · AOP 적용을 통한 응답속도 개선과 코드 가독성 · 유연성 향상  - AsyncConfiguration · EventHandler · Custom Annotation 활용  

  [**이메일 전송 기능을 비동기 처리해서 응답속도 개선하기**](https://inkyu-yoon.github.io/docs/Language/SpringBoot/EmailAsync)  
  
  [**메일 전송 기능을 EventHandler와 AOP를 활용해 개선하기**](https://inkyu-yoon.github.io/docs/Language/SpringBoot/EmailAop)

<br>

* [#6] MySQL Replication 설정 · Write와 Read 작업 분기 처리를 통한 DB 부하 분산 및 고가용성 향상

  [**MySQL Replication Master-Slave 이중화 시스템 구현하기**](https://inkyu-yoon.github.io/docs/Learned/DataBase/mysql-replication)

  [**Spring Boot MySQL Master-Slave 기능 구현하기**](https://inkyu-yoon.github.io/docs/Language/SpringBoot/datasource-replication)

<br>

* [#7] 동시성 문제 해결 - 낙관적 락과 비관적 락  

  [**동시성 문제 해결 - DB Lock 적용**](https://percyfrank.github.io/springboot/concurrency01/)
