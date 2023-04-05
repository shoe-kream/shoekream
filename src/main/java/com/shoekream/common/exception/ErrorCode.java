package com.shoekream.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "이미 존재하는 이메일입니다."),
    DUPLICATE_NICKNAME(HttpStatus.CONFLICT, "이미 존재하는 닉네임입니다."),
    DUPLICATED_PRODUCT(HttpStatus.CONFLICT, "이미 존재하는 상품입니다."),

    WRONG_PASSWORD(HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다."),

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "가입된 회원을 찾을 수 없습니다."),
    BRAND_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 브랜드를 찾을 수 없습니다."),
    DUPLICATED_BRAND(HttpStatus.CONFLICT, "이미 등록되어 있는 브랜드입니다."),
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 상품을 찾을 수 없습니다."),

    CHANGE_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "닉네임은 7일에 한번 변경할 수 있습니다");

    private HttpStatus httpStatus;
    private String message;
}