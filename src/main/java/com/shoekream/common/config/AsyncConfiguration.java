package com.shoekream.common.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
@Slf4j
public class AsyncConfiguration implements AsyncConfigurer {

    @Override
    @Bean(name = "mailExecutor")
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 처리할 작업의 예상되는 동시 처리 수, 설정한 수 만큼 스레드가 미리 만들어져 있다.
        executor.setCorePoolSize(2);

        // 만들어져 있는 스레드가 부족한 경우, max로 설정한 값만큼 스레드를 만들어서 작업을 처리한다.
        executor.setMaxPoolSize(5);

        // max로 설정한 스레드보다 동시 요청이 많아지면, 대기할 요청의 개수
        executor.setQueueCapacity(10);

        // 스레드 이름 지정 (MailExecutor-{스레드넘버)
        executor.setThreadNamePrefix("MailExecutor-");

        // 스레드 풀 시작
        executor.initialize();
        return executor;
    }

    // 비동기 작업 중 발생한 예외 처리 (ex. 스레드 대기 큐에 설정한 값을 초과하는 경우..등)
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (ex, method, params) -> {
            log.error("Exception message - " + ex.getMessage());
            log.error("Method name - " + method.getName());
        };
    }
}