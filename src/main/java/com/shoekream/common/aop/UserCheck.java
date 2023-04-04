package com.shoekream.common.aop;

import com.shoekream.common.exception.ErrorCode;
import com.shoekream.common.exception.ShoeKreamException;
import com.shoekream.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Aspect
@Component
@RequiredArgsConstructor
public class UserCheck {

    private final UserRepository userRepository;

    @Around(value = "execution(* com.shoekream.controller..*.*(..))")
    public Object validAdviceHandler(ProceedingJoinPoint joinPoint) throws Throwable {

        Stream.of(joinPoint.getArgs())
                .filter(arg -> arg instanceof Authentication)
                .map(arg -> (Authentication) arg)
                .findAny()
                .ifPresent((authentication) ->
                        userRepository.findByEmail(authentication.getName())
                                .orElseThrow(() -> new ShoeKreamException(ErrorCode.USER_NOT_FOUND))
                );

        return joinPoint.proceed();
    }
}