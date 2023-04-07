package com.shoekream.controller;

import com.shoekream.common.Response;
import com.shoekream.domain.address.dto.AddressAddRequest;
import com.shoekream.domain.address.dto.AddressResponse;
import com.shoekream.domain.user.Account;
import com.shoekream.domain.user.dto.*;
import com.shoekream.service.AddressService;
import com.shoekream.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserApiController {
    private final UserService userService;
    private final AddressService addressService;

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

    @PatchMapping("/nickname")
    public ResponseEntity<Response<UserResponse>> changeNickname(Authentication authentication, @Validated @RequestBody UserChangeNicknameRequest request, BindingResult br) {
        String email = authentication.getName();
        UserResponse response = userService.changeNicknameUser(request, email);

        return ResponseEntity.ok(Response.success(response));
    }

    @DeleteMapping
    public ResponseEntity<Response<UserResponse>> withdraw(Authentication authentication,@Validated @RequestBody UserWithdrawRequest request,BindingResult br) {
        String email = authentication.getName();
        UserResponse response = userService.withdrawUser(request,email);

        return ResponseEntity.ok(Response.success(response));
    }

    @PatchMapping("/account")
    public ResponseEntity<Response<UserResponse>> updateAccount(Authentication authentication, @Validated @RequestBody UserUpdateAccountRequest request, BindingResult br) {
        String email = authentication.getName();
        UserResponse response = userService.updateAccountUser(request, email);

        return ResponseEntity.ok(Response.success(response));
    }

    @GetMapping("/account")
    public ResponseEntity<Response<Account>> getAccount(Authentication authentication) {
        String email = authentication.getName();
        Account account = userService.getAccountUser(email);

        return ResponseEntity.ok(Response.success(account));
    }

    @PostMapping("/addresses")
    public ResponseEntity<Response<AddressResponse>> addAddress(Authentication authentication, @Validated @RequestBody AddressAddRequest request, BindingResult bindingResult) {
        String email = authentication.getName();
        AddressResponse response = addressService.addAddress(email, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(Response.success(response));
    }

    @GetMapping("/addresses")
    public ResponseEntity<Response<List<AddressResponse>>> getAddresses(Authentication authentication) {
        String email = authentication.getName();
        List<AddressResponse> response = addressService.getAddresses(email);

        return ResponseEntity.ok(Response.success(response));
    }

    @DeleteMapping("/addresses/{addressId}")
    public ResponseEntity<Response<AddressResponse>> deleteAddress(@PathVariable(name = "addressId") Long addressId, Authentication authentication) {
        String email = authentication.getName();
        AddressResponse response = addressService.deleteAddress(email, addressId);

        return ResponseEntity.ok(Response.success(response));
    }
}
