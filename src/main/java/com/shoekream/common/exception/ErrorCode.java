package com.shoekream.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "가입된 회원을 찾을 수 없습니다."),
    BRAND_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 브랜드를 찾을 수 없습니다."),
    DUPLICATED_BRAND(HttpStatus.CONFLICT, "이미 등록되어 있는 브랜드입니다.");

    private HttpStatus httpStatus;
    private String message;
}