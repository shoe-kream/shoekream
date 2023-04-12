package com.shoekream.controller;

import com.shoekream.common.Response;
import com.shoekream.domain.address.dto.AddressRequest;
import com.shoekream.domain.address.dto.AddressResponse;
import com.shoekream.domain.point.dto.PointChargeRequest;
import com.shoekream.domain.point.dto.PointHistoryResponse;
import com.shoekream.domain.point.dto.PointResponse;
import com.shoekream.domain.point.dto.PointWithdrawalRequest;
import com.shoekream.domain.user.Account;
import com.shoekream.domain.user.dto.*;
import com.shoekream.service.AddressService;
import com.shoekream.service.EmailCertificationService;
import com.shoekream.service.PointService;
import com.shoekream.service.UserService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
import java.util.List;

import static com.shoekream.domain.point.PointDivision.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserApiController {
    private final UserService userService;
    private final AddressService addressService;
    private final EmailCertificationService emailCertificationService;
    private final PointService pointService;


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
    public ResponseEntity<Response<UserResponse>> withdraw(Authentication authentication, @Validated @RequestBody UserWithdrawRequest request, BindingResult br) {
        String email = authentication.getName();
        UserResponse response = userService.withdrawUser(request, email);

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
    public ResponseEntity<Response<AddressResponse>> addAddress(Authentication authentication, @Validated @RequestBody AddressRequest request, BindingResult bindingResult) {
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

    @PatchMapping("/addresses/{addressId}")
    public ResponseEntity<Response<AddressResponse>> updateAddress(@PathVariable(name = "addressId") Long addressId, Authentication authentication, @Validated @RequestBody AddressRequest request, BindingResult bindingResult) {
        String email = authentication.getName();
        AddressResponse response = addressService.updateAddress(email, addressId, request);

        return ResponseEntity.ok(Response.success(response));
    }

    @GetMapping("/points")
    public ResponseEntity<Response<Long>> get(Authentication authentication) {
        Long response = pointService.getUserPoint(authentication.getName());
        return ResponseEntity.ok(Response.success(response));
    }

    @PostMapping("/points/charge")
    public ResponseEntity<Response<PointResponse>> charge(Authentication authentication, @Validated @RequestBody PointChargeRequest requestDto, BindingResult br) {
        PointResponse response = pointService.chargePoint(authentication.getName(), requestDto);
        return ResponseEntity.ok(Response.success(response));
    }

    @PostMapping("/points/withdrawal")
    public ResponseEntity<Response<PointResponse>> withdrawal(Authentication authentication, @Validated @RequestBody PointWithdrawalRequest requestDto, BindingResult br) {
        PointResponse response = pointService.withdrawalPoint(authentication.getName(), requestDto);
        return ResponseEntity.ok(Response.success(response));
    }

    @GetMapping("/points/charge-history")
    public ResponseEntity<Response<List<PointHistoryResponse>>> getChargeHistory(Authentication authentication) {
        List<PointHistoryResponse> response = pointService.getHistoryPointByDivision(authentication.getName(), POINT_CHARGE);
        return ResponseEntity.ok(Response.success(response));
    }

    @GetMapping("/points/withdrawal-history")
    public ResponseEntity<Response<List<PointHistoryResponse>>> getWithdrawalHistory(Authentication authentication) {
        List<PointHistoryResponse> response = pointService.getHistoryPointByDivision(authentication.getName(), POINT_WITHDRAW);
        return ResponseEntity.ok(Response.success(response));
    }

    @PostMapping("/send-certification")
    public ResponseEntity<Response<String>> sendCertificationNumber(@Validated @RequestBody UserCertificateAccountRequest request, BindingResult bindingResult) throws NoSuchAlgorithmException, MessagingException {
        emailCertificationService.sendEmailForCertification(request.getEmail());

        return ResponseEntity.ok(Response.success("ok"));
    }

    @GetMapping ("/verify")
    public ResponseEntity<Response<String>> verifyCertificationNumber(@RequestParam(name = "certificationNumber") String certificationNumber, @RequestParam(name = "email") String email) {

        emailCertificationService.verifyEmail(certificationNumber, email);
        userService.changeVerifiedUserRole(email);
        return ResponseEntity.ok(Response.success("ok"));
    }

    @PutMapping("/find-password")
    public ResponseEntity<Response<String>> findPassword(@Validated @RequestBody UserFindPasswordRequest request) throws MessagingException, NoSuchAlgorithmException {
        String tempPassword = emailCertificationService.sendEmailForFindPassword(request.getEmail());
        userService.findPassword(request, tempPassword);
        return ResponseEntity.ok(Response.success("ok"));
    }
}
