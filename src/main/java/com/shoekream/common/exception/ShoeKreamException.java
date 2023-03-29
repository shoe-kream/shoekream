package com.shoekream.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ShoeKreamException extends RuntimeException{

    private ErrorCode errorCode;
    private String message;

    public ShoeKreamException(ErrorCode errorCode) {
        this.errorCode = errorCode;
        this.message = errorCode.getMessage();
    }

}