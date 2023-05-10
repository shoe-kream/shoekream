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
import com.shoekream.service.PointService;
import com.shoekream.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
    private final PointService pointService;

    @Tag(name = "User", description = "회원 정보 관련 API")
    @Operation(summary = "회원 가입", description = "이메일 중복 · 닉네임 중복 시 에러 발생")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "SUCCESS", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"SUCCESS\",\"result\":{\"email\":\"abcd@email.com\",\"nickname\":\"nickname\"}}")}, schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "409", description = "ERROR (이메일 중복 · 닉네임 중복)", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"ERROR\",\"result\":\"에러 메세지\"}")}, schema = @Schema(implementation = Response.class)))
    })
    @PostMapping
    public ResponseEntity<Response<UserCreateResponse>> create(@Validated @RequestBody UserCreateRequest request, BindingResult br) {
        UserCreateResponse response = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(Response.success(response));
    }

    @Tag(name = "User", description = "회원 정보 관련 API")
    @Operation(summary = "회원 로그인", description = "가입된 회원이 존재하지 않을 시 · 비밀번호가 일치하지 않을 시 에러 발생")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"SUCCESS\",\"result\":\"jwt 토큰 값\"}")}, schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "401", description = "ERROR (비밀번호가 일치하지 않을 시)", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"ERROR\",\"result\":\"에러 메세지\"}")}, schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "ERROR (가입된 회원이 존재하지 않을 시)", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"ERROR\",\"result\":\"에러 메세지\"}")}, schema = @Schema(implementation = Response.class)))
    })
    @PostMapping("/login")
    public ResponseEntity<Response<String>> login(@Validated @RequestBody UserLoginRequest request, BindingResult br) {
        String jwt = userService.loginUser(request);
        return ResponseEntity.ok(Response.success(jwt));
    }

    @Tag(name = "User", description = "회원 정보 관련 API")
    @Operation(summary = "회원 비밀번호 변경", description = "JWT 토큰 필요(Authorization Header에 추가) | 가입된 회원이 존재하지 않을 시 · 비밀번호가 일치하지 않을 시 에러 발생")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"SUCCESS\",\"result\":{\"userId\":1,\"email\":\"abcd@email.com\"}}")}, schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "401", description = "ERROR (비밀번호가 일치하지 않을 시)", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"ERROR\",\"result\":\"에러 메세지\"}")}, schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "ERROR (가입된 회원이 존재하지 않을 시)", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"ERROR\",\"result\":\"에러 메세지\"}")}, schema = @Schema(implementation = Response.class)))
    })
    @PatchMapping("/password")
    public ResponseEntity<Response<UserResponse>> changePassword(Authentication authentication, @Validated @RequestBody UserChangePasswordRequest request, BindingResult br) {
        String email = authentication.getName();
        UserResponse response = userService.changePasswordUser(request, email);

        return ResponseEntity.ok(Response.success(response));
    }

    @Tag(name = "User", description = "회원 정보 관련 API")
    @Operation(summary = "회원 닉네임 변경", description = "JWT 토큰 필요(Authorization Header에 추가) | 가입된 회원이 존재하지 않을 시 · 이미 존재하는 닉네임으로 요청시 에러 발생")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"SUCCESS\",\"result\":{\"userId\":1,\"email\":\"abcd@email.com\"}}")}, schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "ERROR (가입된 회원이 존재하지 않을 시)", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"ERROR\",\"result\":\"에러 메세지\"}")}, schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "409", description = "ERROR (이미 존재하는 닉네임으로 요청한 경우)", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"ERROR\",\"result\":\"에러 메세지\"}")}, schema = @Schema(implementation = Response.class)))
    })
    @PatchMapping("/nickname")
    public ResponseEntity<Response<UserResponse>> changeNickname(Authentication authentication, @Validated @RequestBody UserChangeNicknameRequest request, BindingResult br) {
        String email = authentication.getName();
        UserResponse response = userService.changeNicknameUser(request, email);

        return ResponseEntity.ok(Response.success(response));
    }

    @Tag(name = "User", description = "회원 정보 관련 API")
    @Operation(summary = "회원 탈퇴", description = "JWT 토큰 필요(Authorization Header에 추가) | 가입된 회원이 존재하지 않을 시 · 비밀번호가 일치하지 않을 시 · 잔여 포인트가 남아있을 시 · 이미 존재하는 닉네임으로 요청할 시 에러 발생")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"SUCCESS\",\"result\":{\"userId\":1,\"email\":\"abcd@email.com\"}}")}, schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "400", description = "ERROR (잔여 포인트가 남아있을 시)", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"ERROR\",\"result\":\"에러 메세지\"}")}, schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "400", description = "ERROR (비밀번호가 일치하지 않을 시)", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"ERROR\",\"result\":\"에러 메세지\"}")}, schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "ERROR (가입된 회원이 존재하지 않을 시)", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"ERROR\",\"result\":\"에러 메세지\"}")}, schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "409", description = "ERROR (이미 존재하는 닉네임으로 요청할 시)", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"ERROR\",\"result\":\"에러 메세지\"}")}, schema = @Schema(implementation = Response.class)))
    })
    @DeleteMapping
    public ResponseEntity<Response<UserResponse>> withdraw(Authentication authentication, @Validated @RequestBody UserWithdrawRequest request, BindingResult br) {
        String email = authentication.getName();
        UserResponse response = userService.withdrawUser(request, email);

        return ResponseEntity.ok(Response.success(response));
    }

    @Tag(name = "User", description = "회원 정보 관련 API")
    @Operation(summary = "회원 비밀번호 찾기", description = " 입력한 이메일로 임시 비밀번호 발급 | 가입된 회원이 존재하지 않을 시 에러 발생")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"SUCCESS\",\"result\":\"ok\"}")}, schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "ERROR (가입된 회원이 존재하지 않을 시)", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"ERROR\",\"result\":\"에러 메세지\"}")}, schema = @Schema(implementation = Response.class)))
    })
    @PutMapping("/find-password")
    public ResponseEntity<Response<String>> findPassword(@Validated @RequestBody UserFindPasswordRequest request) throws NoSuchAlgorithmException {
        userService.findPassword(request);
        return ResponseEntity.ok(Response.success("ok"));
    }

    @Tag(name = "Account", description = "회원 계좌 정보 관련 API")
    @Operation(summary = "계좌 정보 등록", description = "JWT 토큰 필요(Authorization Header에 추가) | 가입된 회원이 존재하지 않을 시 에러 발생")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"SUCCESS\",\"result\":{\"userId\":1,\"email\":\"abcd@email.com\"}}")}, schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "ERROR (가입된 회원이 존재하지 않을 시)", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"ERROR\",\"result\":\"에러 메세지\"}")}, schema = @Schema(implementation = Response.class)))
    })
    @PatchMapping("/account")
    public ResponseEntity<Response<UserResponse>> updateAccount(Authentication authentication, @Validated @RequestBody UserUpdateAccountRequest request, BindingResult br) {
        String email = authentication.getName();
        UserResponse response = userService.updateAccountUser(request, email);

        return ResponseEntity.ok(Response.success(response));
    }

    @Tag(name = "Account", description = "회원 계좌 정보 관련 API")
    @Operation(summary = "계좌 정보 조회", description = "JWT 토큰 필요(Authorization Header에 추가) | 가입된 회원이 존재하지 않을 시 에러 발생")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"SUCCESS\",\"result\":{\"bankName\":\"bankName\",\"accountNumber\":\"accountNumber\",\"depositor\":\"depositor\"}}")}, schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "ERROR (가입된 회원이 존재하지 않을 시)", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"ERROR\",\"result\":\"에러 메세지\"}")}, schema = @Schema(implementation = Response.class)))
    })
    @GetMapping("/account")
    public ResponseEntity<Response<Account>> getAccount(Authentication authentication) {
        String email = authentication.getName();
        Account account = userService.getAccountUser(email);

        return ResponseEntity.ok(Response.success(account));
    }

    @Tag(name = "Address", description = "회원 주소 정보 관련 API")
    @Operation(summary = "주소 정보 등록", description = "JWT 토큰 필요(Authorization Header에 추가) | 가입된 회원이 존재하지 않을 시 에러 발생")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "SUCCESS", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"SUCCESS\",\"result\":{\"addressId\":1,\"addressName\":\"addressName\",\"address\":\"address\"}}")}, schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "ERROR (가입된 회원이 존재하지 않을 시)", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"ERROR\",\"result\":\"에러 메세지\"}")}, schema = @Schema(implementation = Response.class)))
    })
    @PostMapping("/addresses")
    public ResponseEntity<Response<AddressResponse>> addAddress(Authentication authentication, @Validated @RequestBody AddressRequest request, BindingResult bindingResult) {
        String email = authentication.getName();
        AddressResponse response = addressService.addAddress(email, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(Response.success(response));
    }

    @Tag(name = "Address", description = "회원 주소 정보 관련 API")
    @Operation(summary = "주소 정보 조회", description = "JWT 토큰 필요(Authorization Header에 추가) | 가입된 회원이 존재하지 않을 시 에러 발생")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"SUCCESS\",\"result\": [{\"addressId\":1,\"addressName\":\"addressName\",\"address\":\"address\"},{\"addressId\":2,\"addressName\":\"addressName2\",\"address\":\"address2\"}]}")}, schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "ERROR (가입된 회원이 존재하지 않을 시)", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"ERROR\",\"result\":\"에러 메세지\"}")}, schema = @Schema(implementation = Response.class)))
    })
    @GetMapping("/addresses")
    public ResponseEntity<Response<List<AddressResponse>>> getAddresses(Authentication authentication) {
        String email = authentication.getName();
        List<AddressResponse> response = addressService.getAddresses(email);

        return ResponseEntity.ok(Response.success(response));
    }

    @Tag(name = "Address", description = "회원 주소 정보 관련 API")
    @Operation(summary = "주소 정보 삭제", description = "JWT 토큰 필요(Authorization Header에 추가) | 가입된 회원이 존재하지 않을 시 · 등록된 주소 정보가 존재하지 않을 시 · 본인 정보가 아닐 시 에러 발생")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"SUCCESS\",\"result\":{\"addressId\":1,\"addressName\":\"addressName\",\"address\":\"address\"}}")}, schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "401", description = "ERROR (본인 정보가 아닐 시)", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"ERROR\",\"result\":\"에러 메세지\"}")}, schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "ERROR (가입된 회원이 존재하지 않을 시 · 등록된 주소 정보가 존재하지 않을 시)", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"ERROR\",\"result\":\"에러 메세지\"}")}, schema = @Schema(implementation = Response.class)))
    })
    @DeleteMapping("/addresses/{addressId}")
    public ResponseEntity<Response<AddressResponse>> deleteAddress(@PathVariable(name = "addressId") Long addressId, Authentication authentication) {
        String email = authentication.getName();
        AddressResponse response = addressService.deleteAddress(email, addressId);

        return ResponseEntity.ok(Response.success(response));
    }

    @Tag(name = "Address", description = "회원 주소 정보 관련 API")
    @Operation(summary = "주소 정보 수정", description = "JWT 토큰 필요(Authorization Header에 추가) | 가입된 회원이 존재하지 않을 시 · 등록된 주소 정보가 존재하지 않을 시 · 본인 정보가 아닐 시 에러 발생")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"SUCCESS\",\"result\":{\"addressId\":1,\"addressName\":\"addressName\",\"address\":\"address\"}}")}, schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "401", description = "ERROR (본인 정보가 아닐 시 에러)", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"ERROR\",\"result\":\"에러 메세지\"}")}, schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "ERROR (가입된 회원이 존재하지 않을 시 · 등록된 주소 정보가 존재하지 않을 시 에러)", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"ERROR\",\"result\":\"에러 메세지\"}")}, schema = @Schema(implementation = Response.class)))
    })
    @PatchMapping("/addresses/{addressId}")
    public ResponseEntity<Response<AddressResponse>> updateAddress(@PathVariable(name = "addressId") Long addressId, Authentication authentication, @Validated @RequestBody AddressRequest request, BindingResult bindingResult) {
        String email = authentication.getName();
        AddressResponse response = addressService.updateAddress(email, addressId, request);

        return ResponseEntity.ok(Response.success(response));
    }

    @Tag(name = "Point", description = "회원 포인트 정보 관련 API")
    @Operation(summary = "회원 포인트 정보 조회", description = "JWT 토큰 필요(Authorization Header에 추가) | 가입된 회원이 존재하지 않을 시 에러 발생")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"SUCCESS\",\"result\":1000}")}, schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "ERROR (가입된 회원이 존재하지 않을 시)", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"ERROR\",\"result\":\"에러 메세지\"}")}, schema = @Schema(implementation = Response.class)))
    })
    @GetMapping("/points")
    public ResponseEntity<Response<Long>> get(Authentication authentication) {
        Long response = pointService.getUserPoint(authentication.getName());
        return ResponseEntity.ok(Response.success(response));
    }

    @Tag(name = "Point", description = "회원 포인트 정보 관련 API")
    @Operation(summary = "회원 포인트 충전", description = "JWT 토큰 필요(Authorization Header에 추가) | 가입된 회원이 존재하지 않을 시 에러 발생")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"SUCCESS\",\"result\":{\"remainingPoint\":1000}}")}, schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "ERROR (가입된 회원이 존재하지 않을 시)", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"ERROR\",\"result\":\"에러 메세지\"}")}, schema = @Schema(implementation = Response.class)))
    })
    @PostMapping("/points/charge")
    public ResponseEntity<Response<PointResponse>> charge(Authentication authentication, @Validated @RequestBody PointChargeRequest requestDto, BindingResult br) {
        PointResponse response = pointService.chargePoint(authentication.getName(), requestDto);
        return ResponseEntity.ok(Response.success(response));
    }

    @Tag(name = "Point", description = "회원 포인트 정보 관련 API")
    @Operation(summary = "회원 포인트 출금", description = "JWT 토큰 필요(Authorization Header에 추가) | 가입된 회원이 존재하지 않을 시 · 비밀번호가 일치하지 않을 시 · 보유한 포인트보다 많은 포인트 출금 요청 시 에러 발생 ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"SUCCESS\",\"result\":{\"remainingPoint\":1000}}")}, schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "400", description = "ERROR (보유한 포인트보다 많은 포인트 출금 요청 시)", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"ERROR\",\"result\":\"에러 메세지\"}")}, schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "401", description = "ERROR (비밀번호가 일치하지 않을 시)", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"ERROR\",\"result\":\"에러 메세지\"}")}, schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "ERROR (가입된 회원이 존재하지 않을 시)", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"ERROR\",\"result\":\"에러 메세지\"}")}, schema = @Schema(implementation = Response.class)))
    })
    @PostMapping("/points/withdrawal")
    public ResponseEntity<Response<PointResponse>> withdrawal(Authentication authentication, @Validated @RequestBody PointWithdrawalRequest requestDto, BindingResult br) {
        PointResponse response = pointService.withdrawalPoint(authentication.getName(), requestDto);
        return ResponseEntity.ok(Response.success(response));
    }

    @Tag(name = "Point", description = "회원 포인트 정보 관련 API")
    @Operation(summary = "회원 포인트 충전 내역 조회", description = "JWT 토큰 필요(Authorization Header에 추가) | 가입된 회원이 존재하지 않을 시 에러 발생 ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"SUCCESS\",\"result\":{\"time\":\"출금일자\",\"amount\":1000}}")}, schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "ERROR (가입된 회원이 존재하지 않을 시)", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"ERROR\",\"result\":\"에러 메세지\"}")}, schema = @Schema(implementation = Response.class)))
    })
    @GetMapping("/points/charge-history")
    public ResponseEntity<Response<List<PointHistoryResponse>>> getChargeHistory(Authentication authentication) {
        List<PointHistoryResponse> response = pointService.getHistoryPointByDivision(authentication.getName(), POINT_CHARGE);
        return ResponseEntity.ok(Response.success(response));
    }

    @Tag(name = "Point", description = "회원 포인트 정보 관련 API")
    @Operation(summary = "회원 포인트 출금 내역 조회", description = "JWT 토큰 필요(Authorization Header에 추가) | 가입된 회원이 존재하지 않을 시 에러 발생 ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"SUCCESS\",\"result\":[{\"time\":\"출금일자\",\"amount\":1000},{\"time\":\"출금일자\",\"amount\":1000}]}")}, schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "ERROR (가입된 회원이 존재하지 않을 시)", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"ERROR\",\"result\":\"에러 메세지\"}")}, schema = @Schema(implementation = Response.class)))
    })
    @GetMapping("/points/withdrawal-history")
    public ResponseEntity<Response<List<PointHistoryResponse>>> getWithdrawalHistory(Authentication authentication) {
        List<PointHistoryResponse> response = pointService.getHistoryPointByDivision(authentication.getName(), POINT_WITHDRAW);
        return ResponseEntity.ok(Response.success(response));
    }

    @Tag(name = "Certification", description = "회원 인증 관련 API")
    @Operation(summary = "회원 인증 번호 발급 요청", description = "가입된 회원이 존재하지 않을 시 에러 발생 ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"SUCCESS\",\"result\":{\"email\":\"abcd@email.com\",\"certificationNumber\":\"인증번호\"}}")}, schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "ERROR (가입된 회원이 존재하지 않을 시)", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"ERROR\",\"result\":\"에러 메세지\"}")}, schema = @Schema(implementation = Response.class)))
    })
    @PostMapping("/send-certification")
    public ResponseEntity<Response<UserCertificateResponse>> sendCertificationNumber(@Validated @RequestBody UserCertificateRequest request, BindingResult bindingResult) throws NoSuchAlgorithmException, MessagingException {
        UserCertificateResponse response = userService.checkUserExistForCertificate(request);
        return ResponseEntity.ok(Response.success(response));
    }

    @Tag(name = "Certification", description = "회원 인증 관련 API")
    @Operation(summary = "회원 인증 번호 확인 요청", description = "가입된 회원이 존재하지 않을 시 에러 발생 ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"SUCCESS\",\"result\":\"ok\"}")}, schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "ERROR (가입된 회원이 존재하지 않을 시)", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"ERROR\",\"result\":\"에러 메세지\"}")}, schema = @Schema(implementation = Response.class)))
    })
    @GetMapping("/verify")
    public ResponseEntity<Response<String>> verifyCertificationNumber(@RequestParam(name = "certificationNumber") String certificationNumber, @RequestParam(name = "email") String email) {
        userService.changeVerifiedUserRole(email);
        return ResponseEntity.ok(Response.success("ok"));
    }

}
