package com.shoekream.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Response<T> {

    private String message;
    private T result;

    public static <T> Response<T> success(T result) {
        return new Response<>("SUCCESS", result);
    }
    public static <T> Response<T> error(T result) {
        return new Response<>("ERROR", result);
    }

}
