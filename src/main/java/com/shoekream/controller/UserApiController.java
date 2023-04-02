package com.shoekream.controller;

import com.shoekream.common.Response;
import com.shoekream.common.exception.BindingException;
import com.shoekream.common.exception.ErrorCode;
import com.shoekream.common.exception.ShoeKreamException;
import com.shoekream.domain.user.dto.UserCreateRequest;
import com.shoekream.domain.user.dto.UserCreateResponse;
import com.shoekream.domain.user.dto.UserLoginRequest;
import com.shoekream.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserApiController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<Response<UserCreateResponse>> create(@Validated @RequestBody UserCreateRequest request, BindingResult br) {

        UserCreateResponse response = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(Response.success(response));
    }

    @PostMapping("/login")
    public ResponseEntity<Response<String>> login(@Validated @RequestBody UserLoginRequest request, BindingResult br) {

        String jwt = userService.loginUser(request);
        return ResponseEntity.ok(Response.success(jwt));
    }

}
