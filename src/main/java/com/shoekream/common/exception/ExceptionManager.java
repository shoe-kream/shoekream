package com.shoekream.common.exception;

import com.shoekream.common.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
@Slf4j
public class ExceptionManager {

    @ExceptionHandler(ShoeKreamException.class)
    public ResponseEntity<?> appExceptionHandler(ShoeKreamException e) {
        return ResponseEntity.status(e.getErrorCode().getHttpStatus())
                .body(Response.error(e.getErrorCode()));
    }

}