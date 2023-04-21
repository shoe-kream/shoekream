package com.shoekream.common.aop;

import com.shoekream.common.Response;
import com.shoekream.common.exception.ShoeKreamException;
import com.shoekream.service.EmailCertificationService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class UserVerify {

    private final EmailCertificationService emailCertificationService;

    @Around(value = "execution(* com.shoekream.controller.UserApiController.verifyCertificationNumber(..))")
    public Object verifyAdviceHandler(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        String certificationNumber = (String) args[0];
        String email = (String) args[1];
        try {
            emailCertificationService.verifyEmail(certificationNumber, email);
        } catch (ShoeKreamException e) {
            return ResponseEntity.ok(Response.error(e.getMessage()));
        }

        return joinPoint.proceed();
    }
}