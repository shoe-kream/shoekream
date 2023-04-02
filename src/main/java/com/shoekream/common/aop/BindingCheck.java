package com.shoekream.common.aop;

import com.shoekream.common.exception.BindingException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

@Aspect
@Component
public class BindingCheck {

    @Around(value = "execution(* com.shoekream.controller..*.*(..))")
    public Object validAdviceHandler(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if (arg instanceof BindingResult) {
                BindingResult bindingResult = (BindingResult) arg;
                if (bindingResult.hasErrors()) {
                    String errorMessage = bindingResult.getFieldError().getDefaultMessage();
                    throw new BindingException(errorMessage);
                }
            }
        }
        return joinPoint.proceed();
    }
}