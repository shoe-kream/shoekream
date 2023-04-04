package com.shoekream.controller;

import com.shoekream.common.Response;
import com.shoekream.domain.user.dto.*;
import com.shoekream.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
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

    @PatchMapping("/password")
    public ResponseEntity<Response<UserResponse>> changePassword(Authentication authentication, @Validated @RequestBody UserChangePasswordRequest request, BindingResult br) {
        String email = authentication.getName();
        UserResponse response = userService.changePasswordUser(request, email);

        return ResponseEntity.ok(Response.success(response));
    }
}
