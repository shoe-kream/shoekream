package com.shoekream.common.aop;

import com.shoekream.common.exception.BindingException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import java.util.stream.Stream;

@Aspect
@Component
public class BindingCheck {

    @Around(value = "execution(* com.shoekream.controller..*.*(..))")
    public Object validAdviceHandler(ProceedingJoinPoint joinPoint) throws Throwable {

        Stream.of(joinPoint.getArgs())
                .filter(arg -> arg instanceof BindingResult)
                .map(arg -> (BindingResult) arg)
                .filter(br -> br.hasErrors())
                .findAny()
                .ifPresent((br)->{
                    String errorMessage = br.getFieldError().getDefaultMessage();
                    throw new BindingException(errorMessage);
                });

        return joinPoint.proceed();
    }
}