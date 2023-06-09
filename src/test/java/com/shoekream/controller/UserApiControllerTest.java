package com.shoekream.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shoekream.common.aop.BindingCheck;
import com.shoekream.common.config.SecurityConfig;
import com.shoekream.common.exception.ErrorCode;
import com.shoekream.common.exception.ShoeKreamException;
import com.shoekream.common.util.JwtUtil;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.List;

import static com.shoekream.common.exception.ErrorCode.*;
import static com.shoekream.domain.point.PointDivision.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.doThrow;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(value = UserApiController.class)
@EnableAspectJAutoProxy
@Import({SecurityConfig.class, BindingCheck.class})
class UserApiControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    WebApplicationContext wac;
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    private UserService userService;
    @MockBean
    private AddressService addressService;
    @MockBean
    private PointService pointService;

    @MockBean
    private EmailCertificationService emailCertificationService;

    @Value("${jwt.secret}")
    String secretKey;


    @BeforeEach
    public void setUpMockMvc() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(wac)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();


    }

    @Nested
    @DisplayName("회원가입 테스트")
    class UserJoin {
        UserCreateRequest request = new UserCreateRequest("email@email.com", "password1!", "nickname", "010-0000-0000");
        UserCreateResponse response = new UserCreateResponse("email@email.com", "nickname");

        @Test
        @DisplayName("회원가입 성공 테스트")
        void success() throws Exception {

            given(userService.createUser(request))
                    .willReturn(response);

            mockMvc.perform(post("/api/v1/users")
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("SUCCESS"))
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result.email").value(request.getEmail()))
                    .andExpect(jsonPath("$.result.nickname").value(request.getNickname()));
        }


        @Test
        @DisplayName("회원가입 실패 테스트 (이미 존재하는 이메일인 경우)")
        void error1() throws Exception {

            when(userService.createUser(request))
                    .thenThrow(new ShoeKreamException(ErrorCode.DUPLICATE_EMAIL));

            mockMvc.perform(post("/api/v1/users")
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("ERROR"))
                    .andExpect(jsonPath("$.result").exists());
        }

        @Test
        @DisplayName("회원가입 실패 테스트 (이미 존재하는 닉네임인 경우)")
        void error2() throws Exception {

            when(userService.createUser(request))
                    .thenThrow(new ShoeKreamException(ErrorCode.DUPLICATE_NICKNAME));

            mockMvc.perform(post("/api/v1/users")
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("ERROR"))
                    .andExpect(jsonPath("$.result").exists());
        }

        @Test
        @DisplayName("회원가입 실패 테스트 (Binding Error 발생 이메일 형식만 대표로 테스트)")
        void error3() throws Exception {

            UserCreateRequest request = new UserCreateRequest("email", "password1!", "nickname", "010-0000-0000");

            mockMvc.perform(post("/api/v1/users")
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("ERROR"))
                    .andExpect(jsonPath("$.result").exists());
        }
    }

    @Nested
    @DisplayName("로그인 테스트")
    class UserLogin {

        UserLoginRequest request = new UserLoginRequest("email", "password");
        String jwt = "jwt";

        @Test
        @DisplayName("로그인 성공 테스트")
        void success() throws Exception {

            given(userService.loginUser(request))
                    .willReturn(jwt);

            mockMvc.perform(post("/api/v1/users/login")
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("SUCCESS"))
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result").value(jwt));
        }


        @Test
        @DisplayName("로그인 실패 테스트 (가입된 회원이 아닌 경우)")
        void error1() throws Exception {

            when(userService.loginUser(request))
                    .thenThrow(new ShoeKreamException(ErrorCode.USER_NOT_FOUND));

            mockMvc.perform(post("/api/v1/users/login")
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("ERROR"))
                    .andExpect(jsonPath("$.result").exists());
        }

        @Test
        @DisplayName("로그인 실패 테스트 (비밀번호가 일치하지 않는 경우)")
        void error2() throws Exception {

            when(userService.loginUser(request))
                    .thenThrow(new ShoeKreamException(ErrorCode.WRONG_PASSWORD));

            mockMvc.perform(post("/api/v1/users/login")
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("ERROR"))
                    .andExpect(jsonPath("$.result").exists());
        }

        @Test
        @DisplayName("로그인 실패 테스트 (Binding error 발생)")
        void error3() throws Exception {

            UserLoginRequest request = new UserLoginRequest("", "password");

            mockMvc.perform(post("/api/v1/users/login")
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("ERROR"))
                    .andExpect(jsonPath("$.result").exists());
        }
    }

    @Nested
    @DisplayName("회원 비밀번호 변경 테스트")
    class UserChangePassword {
        Long userId = 1L;
        String email = "email";

        UserChangePasswordRequest request = new UserChangePasswordRequest("oldPassword12!", "oldPassword12!");
        UserResponse response = new UserResponse(userId, email);

        String token = JwtUtil.createToken(email, "ROLE_USER", secretKey, 1000L * 60 * 60);

        @Test
        @DisplayName("회원 비밀번호 변경 성공 테스트")
        void success() throws Exception {

            given(userService.changePasswordUser(request, email))
                    .willReturn(response);

            mockMvc.perform(patch("/api/v1/users/password")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("SUCCESS"))
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result.userId").value(userId))
                    .andExpect(jsonPath("$.result.email").value(email));
        }

        @Test
        @DisplayName("회원 비밀번호 변경 실패 테스트 (가입된 회원이 아닌 경우)")
        void error1() throws Exception {

            when(userService.changePasswordUser(request, email))
                    .thenThrow(new ShoeKreamException(ErrorCode.USER_NOT_FOUND));

            mockMvc.perform(patch("/api/v1/users/password")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("ERROR"))
                    .andExpect(jsonPath("$.result").exists());
        }

        @Test
        @DisplayName("회원 비밀번호 변경 실패 테스트 (비밀번호 일치하지 않는 경우 발생)")
        void error2() throws Exception {

            when(userService.changePasswordUser(request, email))
                    .thenThrow(new ShoeKreamException(WRONG_PASSWORD));

            mockMvc.perform(patch("/api/v1/users/password")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("ERROR"))
                    .andExpect(jsonPath("$.result").exists());
        }

        @Test
        @DisplayName("회원 비밀번호 변경 실패 테스트 (Binding error 발생)")
        void error3() throws Exception {

            UserChangePasswordRequest request = new UserChangePasswordRequest("oldPassword", null);

            mockMvc.perform(patch("/api/v1/users/password")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("ERROR"))
                    .andExpect(jsonPath("$.result").exists());
        }

    }

    @Nested
    @DisplayName("회원 닉네임 변경 테스트")
    class UserChangeNickname {
        Long userId = 1L;
        String email = "email";
        UserChangeNicknameRequest request = new UserChangeNicknameRequest("newNickname");
        UserResponse response = new UserResponse(userId, email);

        String token = JwtUtil.createToken(email, "ROLE_USER", secretKey, 1000L * 60 * 60);

        @Test
        @DisplayName("회원 닉네임 변경 성공 테스트")
        void success() throws Exception {

            given(userService.changeNicknameUser(request, email))
                    .willReturn(response);

            mockMvc.perform(patch("/api/v1/users/nickname")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("SUCCESS"))
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result.userId").value(userId))
                    .andExpect(jsonPath("$.result.email").value(email));
        }

        @Test
        @DisplayName("회원 닉네임 변경 실패 테스트 (회원이 존재하지 않는 경우)")
        void error1() throws Exception {

            when(userService.changeNicknameUser(request, email))
                    .thenThrow(new ShoeKreamException(ErrorCode.USER_NOT_FOUND));

            mockMvc.perform(patch("/api/v1/users/nickname")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("ERROR"))
                    .andExpect(jsonPath("$.result").exists());
        }

        @Test
        @DisplayName("회원 닉네임 변경 실패 테스트 (닉네임 변경한지 7일이 지나지 않은 경우)")
        void error2() throws Exception {

            when(userService.changeNicknameUser(request, email))
                    .thenThrow(new ShoeKreamException(CHANGE_NOT_ALLOWED));

            mockMvc.perform(patch("/api/v1/users/nickname")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("ERROR"))
                    .andExpect(jsonPath("$.result").exists());
        }

        @Test
        @DisplayName("회원 닉네임 변경 실패 테스트 (Binding error 발생)")
        void error3() throws Exception {

            UserChangeNicknameRequest request = new UserChangeNicknameRequest();

            mockMvc.perform(patch("/api/v1/users/nickname")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("ERROR"))
                    .andExpect(jsonPath("$.result").exists());
        }
    }

    @Nested
    @DisplayName("회원 탈퇴 테스트")
    class UserWithdraw {
        Long userId = 1L;
        String email = "email";
        UserWithdrawRequest request = new UserWithdrawRequest("password");
        UserResponse response = new UserResponse(userId, email);

        String token = JwtUtil.createToken(email, "ROLE_USER", secretKey, 1000L * 60 * 60);

        @Test
        @DisplayName("회원 탈퇴 성공 테스트")
        void success() throws Exception {

            given(userService.withdrawUser(request, email))
                    .willReturn(response);

            mockMvc.perform(delete("/api/v1/users")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("SUCCESS"))
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result.userId").value(userId))
                    .andExpect(jsonPath("$.result.email").value(email));
        }

        @Test
        @DisplayName("회원 탈퇴 실패 테스트 (가입된 회원이 아닌 경우)")
        void error1() throws Exception {

            when(userService.withdrawUser(request, email))
                    .thenThrow(new ShoeKreamException(ErrorCode.USER_NOT_FOUND));

            mockMvc.perform(delete("/api/v1/users")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("ERROR"))
                    .andExpect(jsonPath("$.result").exists());
        }

        @Test
        @DisplayName("회원 탈퇴 실패 테스트 (비밀번호 일치하지 않는 경우)")
        void error2() throws Exception {

            when(userService.withdrawUser(request, email))
                    .thenThrow(new ShoeKreamException(WRONG_PASSWORD));

            mockMvc.perform(delete("/api/v1/users")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("ERROR"))
                    .andExpect(jsonPath("$.result").exists());
        }

        @Test
        @DisplayName("회원 탈퇴 실패 테스트 (잔여 포인트가 남아 있는 경우)")
        void error3() throws Exception {

            when(userService.withdrawUser(request, email))
                    .thenThrow(new ShoeKreamException(WITHDRAWAL_NOT_ALLOWED));

            mockMvc.perform(delete("/api/v1/users")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("ERROR"))
                    .andExpect(jsonPath("$.result").exists());
        }

        @Test
        @DisplayName("회원 탈퇴 실패 테스트 (Binding error 발생)")
        void error4() throws Exception {

            UserWithdrawRequest request = new UserWithdrawRequest(null);

            mockMvc.perform(delete("/api/v1/users")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("ERROR"))
                    .andExpect(jsonPath("$.result").exists());
        }

    }

    @Nested
    @DisplayName("회원 계좌 정보 등록 테스트")
    class UserUpdateAccount {
        Long userId = 1L;
        String email = "email";

        UserUpdateAccountRequest request = new UserUpdateAccountRequest("bankName", "accountNumber", "depositor");

        UserResponse response = new UserResponse(userId, email);

        String token = JwtUtil.createToken(email, "ROLE_USER", secretKey, 1000L * 60 * 60);

        @Test
        @DisplayName("계좌 정보 등록 성공")
        void success() throws Exception {

            given(userService.updateAccountUser(request, email))
                    .willReturn(response);

            mockMvc.perform(patch("/api/v1/users/account")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("SUCCESS"))
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result.userId").value(userId))
                    .andExpect(jsonPath("$.result.email").value(email));
        }

        @Test
        @DisplayName("계좌 정보 등록 실패 (가입된 회원이 아닌 경우)")
        void error1() throws Exception {

            when(userService.updateAccountUser(request, email))
                    .thenThrow(new ShoeKreamException(USER_NOT_FOUND));

            mockMvc.perform(patch("/api/v1/users/account")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("ERROR"))
                    .andExpect(jsonPath("$.result").exists());
        }

        @Test
        @DisplayName("계좌 정보 등록 실패 (BindingError 발생)")
        void error2() throws Exception {

            UserUpdateAccountRequest request = new UserUpdateAccountRequest(null, "accountNumber", "depositor");

            when(userService.updateAccountUser(request, email))
                    .thenThrow(new ShoeKreamException(USER_NOT_FOUND));

            mockMvc.perform(patch("/api/v1/users/account")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("ERROR"))
                    .andExpect(jsonPath("$.result").exists());
        }

    }

    @Nested
    @DisplayName("회원 계좌 조회 등록 테스트")
    class UserGetAccount {
        String email = "email";

        String bankName = "bankName";
        String accountNumber = "accountNumber";
        String depositor = "depositor";

        Account account = Account.builder()
                .bankName(bankName)
                .accountNumber(accountNumber)
                .depositor(depositor)
                .build();

        String token = JwtUtil.createToken(email, "ROLE_USER", secretKey, 1000L * 60 * 60);

        @Test
        @DisplayName("계좌 정보 조회 성공")
        void success() throws Exception {

            given(userService.getAccountUser(email))
                    .willReturn(account);

            mockMvc.perform(get("/api/v1/users/account")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andDo(print())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("SUCCESS"))
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result.bankName").value(bankName))
                    .andExpect(jsonPath("$.result.accountNumber").value(accountNumber))
                    .andExpect(jsonPath("$.result.depositor").value(depositor));
        }

        @Test
        @DisplayName("계좌 정보 조회 실패 (가입된 회원이 아닌 경우)")
        void error1() throws Exception {

            when(userService.getAccountUser(email))
                    .thenThrow(new ShoeKreamException(USER_NOT_FOUND));

            mockMvc.perform(get("/api/v1/users/account")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andDo(print())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("ERROR"))
                    .andExpect(jsonPath("$.result").exists());
        }

    }

    @Nested
    @DisplayName("회원 주소 추가")
    class UserAddAddress {
        Long addressId = 1L;
        AddressRequest request = AddressRequest.builder()
                .addressName("addressName")
                .roadNameAddress("roadNameAddress")
                .detailedAddress("detailedAddress")
                .postalCode("postalCode")
                .build();

        AddressResponse response = AddressResponse.builder()
                .address("full address")
                .addressId(addressId)
                .addressName("addressName")
                .build();

        String token = JwtUtil.createToken("email", "ROLE_USER", secretKey, 1000L * 60 * 60);

        @Test
        @DisplayName("회원 주소 등록 성공")
        void success() throws Exception {

            given(addressService.addAddress(anyString(), any(AddressRequest.class)))
                    .willReturn(response);

            mockMvc.perform(post("/api/v1/users/addresses")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("SUCCESS"))
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result.addressId").value(addressId))
                    .andExpect(jsonPath("$.result.address").value("full address"))
                    .andExpect(jsonPath("$.result.address").value("full address"));
        }

        @Test
        @DisplayName("회원 주소 등록 실패 (가입된 회원이 아닌 경우)")
        void error() throws Exception {

            when(addressService.addAddress(anyString(), any(AddressRequest.class)))
                    .thenThrow(new ShoeKreamException(USER_NOT_FOUND));

            mockMvc.perform(post("/api/v1/users/addresses")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("ERROR"))
                    .andExpect(jsonPath("$.result").exists());
        }
    }

    @Nested
    @DisplayName("회원 주소 조회")
    class UserGetAddress {
        Long addressId = 1L;

        AddressResponse response = AddressResponse.builder()
                .address("full address")
                .addressId(addressId)
                .addressName("addressName")
                .build();

        String token = JwtUtil.createToken("email", "ROLE_USER", secretKey, 1000L * 60 * 60);

        @Test
        @DisplayName("회원 주소 조회 성공")
        void success() throws Exception {

            given(addressService.getAddresses(anyString()))
                    .willReturn(List.of(response));

            mockMvc.perform(get("/api/v1/users/addresses")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andDo(print())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("SUCCESS"))
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result[0].addressId").value(addressId))
                    .andExpect(jsonPath("$.result[0].addressName").value("addressName"))
                    .andExpect(jsonPath("$.result[0].address").value("full address"));
        }

        @Test
        @DisplayName("회원 주소 조회 실패 (가입된 회원이 아닌 경우)")
        void error() throws Exception {

            when(addressService.getAddresses(anyString()))
                    .thenThrow(new ShoeKreamException(USER_NOT_FOUND));

            mockMvc.perform(get("/api/v1/users/addresses")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andDo(print())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("ERROR"))
                    .andExpect(jsonPath("$.result").exists());
        }
    }

    @Nested
    @DisplayName("회원 주소 삭제")
    class UserDeleteAddress {
        Long addressId = 1L;

        AddressResponse response = AddressResponse.builder()
                .address("full address")
                .addressId(addressId)
                .addressName("addressName")
                .build();

        String token = JwtUtil.createToken("email", "ROLE_USER", secretKey, 1000L * 60 * 60);

        @Test
        @DisplayName("회원 주소 삭제 성공")
        void success() throws Exception {

            given(addressService.deleteAddress(anyString(), anyLong()))
                    .willReturn(response);

            mockMvc.perform(delete("/api/v1/users/addresses/" + addressId)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andDo(print())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("SUCCESS"))
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result.addressId").value(addressId))
                    .andExpect(jsonPath("$.result.addressName").value("addressName"))
                    .andExpect(jsonPath("$.result.address").value("full address"));
        }

        @Test
        @DisplayName("회원 주소 삭제 실패 (가입된 회원이 아닌 경우)")
        void error() throws Exception {

            when(addressService.deleteAddress(anyString(), anyLong()))
                    .thenThrow(new ShoeKreamException(USER_NOT_FOUND));

            mockMvc.perform(delete("/api/v1/users/addresses/" + addressId)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andDo(print())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("ERROR"))
                    .andExpect(jsonPath("$.result").exists());
        }

        @Test
        @DisplayName("회원 주소 삭제 실패 (삭제할 주소가 존재하지 않는 경우)")
        void error2() throws Exception {

            when(addressService.deleteAddress(anyString(), anyLong()))
                    .thenThrow(new ShoeKreamException(ADDRESS_NOT_FOUND));

            mockMvc.perform(delete("/api/v1/users/addresses/" + addressId)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andDo(print())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("ERROR"))
                    .andExpect(jsonPath("$.result").exists());
        }

        @Test
        @DisplayName("회원 주소 삭제 실패 (비밀번호가 일치하지 않는 경우)")
        void error3() throws Exception {

            when(addressService.deleteAddress(anyString(), anyLong()))
                    .thenThrow(new ShoeKreamException(WRONG_PASSWORD));

            mockMvc.perform(delete("/api/v1/users/addresses/" + addressId)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andDo(print())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("ERROR"))
                    .andExpect(jsonPath("$.result").exists());
        }
    }

    @Nested
    @DisplayName("회원 주소 수정")
    class UserUpdateAddress {
        Long addressId = 1L;

        AddressRequest request = AddressRequest.builder()
                .addressName("addressName")
                .roadNameAddress("roadNameAddress")
                .detailedAddress("detailedAddress")
                .postalCode("postalCode")
                .build();
        AddressResponse response = AddressResponse.builder()
                .address("full address")
                .addressId(addressId)
                .addressName("addressName")
                .build();

        String token = JwtUtil.createToken("email", "ROLE_USER", secretKey, 1000L * 60 * 60);

        @Test
        @DisplayName("회원 주소 수정 성공")
        void success() throws Exception {

            given(addressService.updateAddress(anyString(), anyLong(), any(AddressRequest.class)))
                    .willReturn(response);

            mockMvc.perform(patch("/api/v1/users/addresses/" + addressId)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("SUCCESS"))
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result.addressId").value(addressId))
                    .andExpect(jsonPath("$.result.addressName").value("addressName"))
                    .andExpect(jsonPath("$.result.address").value("full address"));
        }

        @Test
        @DisplayName("회원 주소 수정 실패 (가입된 회원이 아닌 경우)")
        void error() throws Exception {

            when(addressService.updateAddress(anyString(), anyLong(), any(AddressRequest.class)))
                    .thenThrow(new ShoeKreamException(USER_NOT_FOUND));

            mockMvc.perform(patch("/api/v1/users/addresses/" + addressId)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("ERROR"))
                    .andExpect(jsonPath("$.result").exists());
        }
    }

    @Nested
    @DisplayName("회원 포인트 조회 테스트")
    class GetPoint {
        String email = "email";
        Long point = 1000L;

        String token = JwtUtil.createToken("email", "ROLE_USER", secretKey, 1000L * 60 * 60);

        @Test
        @DisplayName("회원 포인트 조회 성공")
        public void GetPointSuccess() throws Exception {
            given(pointService.getUserPoint(email))
                    .willReturn(point);


            mockMvc.perform(get("/api/v1/users/points")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andDo(print())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("SUCCESS"))
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result").value(point));
        }

        @Test
        @DisplayName("회원 포인트 조회 실패 (가입된 회원이 아닌 경우) ")
        public void GetPointError() throws Exception {
            when(pointService.getUserPoint(email))
                    .thenThrow(new ShoeKreamException(USER_NOT_FOUND));

            mockMvc.perform(get("/api/v1/users/points")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andDo(print())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("ERROR"))
                    .andExpect(jsonPath("$.result").exists());
        }
    }

    @Nested
    @DisplayName("회원 포인트 내역 조회 테스트")
    class GetPointHistory {
        String email = "email";
        Long point = 1000L;
        LocalDateTime time = LocalDateTime.now();

        String token = JwtUtil.createToken("email", "ROLE_USER", secretKey, 1000L * 60 * 60);

        PointHistoryResponse response = PointHistoryResponse.builder()
                .amount(point)
                .time(time)
                .build();

        @Test
        @DisplayName("회원 포인트 충전 내역 조회 성공")
        public void GetPointHistorySuccess() throws Exception {
            given(pointService.getHistoryPointByDivision(email, POINT_CHARGE))
                    .willReturn(List.of(response));


            mockMvc.perform(get("/api/v1/users/points/charge-history")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andDo(print())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("SUCCESS"))
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result[0].amount").value(point))
                    .andExpect(jsonPath("$.result[0].time").exists());
        }

        @Test
        @DisplayName("회원 포인트 조회 실패 (가입된 회원이 아닌 경우) ")
        public void GetPointHistoryError() throws Exception {
            when(pointService.getHistoryPointByDivision(email, POINT_CHARGE))
                    .thenThrow(new ShoeKreamException(USER_NOT_FOUND));

            mockMvc.perform(get("/api/v1/users/points/charge-history")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andDo(print())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("ERROR"))
                    .andExpect(jsonPath("$.result").exists());
        }
    }

    @Nested
    @DisplayName("회원 포인트 충전 테스트")
    class ChargePointHistory {
        String email = "email";
        Long point = 1000L;

        String token = JwtUtil.createToken("email", "ROLE_USER", secretKey, 1000L * 60 * 60);

        PointChargeRequest request = PointChargeRequest.builder()
                .amount(point)
                .build();

        PointResponse response = PointResponse.builder()
                .remainingPoint(point)
                .build();

        @Test
        @DisplayName("회원 포인트 충전 성공")
        public void ChargePointSuccess() throws Exception {
            given(pointService.chargePoint(email, request))
                    .willReturn(response);

            mockMvc.perform(post("/api/v1/users/points/charge")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("SUCCESS"))
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result.remainingPoint").value(point));
        }

        @Test
        @DisplayName("회원 포인트 충전 실패 (가입된 회원이 아닌 경우) ")
        public void ChargePointError() throws Exception {
            when(pointService.chargePoint(email, request))
                    .thenThrow(new ShoeKreamException(USER_NOT_FOUND));

            mockMvc.perform(post("/api/v1/users/points/charge")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("ERROR"))
                    .andExpect(jsonPath("$.result").exists());
        }
    }

    @Nested
    @DisplayName("회원 포인트 출금 테스트")
    class WithdrawalPointHistory {
        String email = "email";
        Long point = 1000L;
        String token = JwtUtil.createToken("email", "ROLE_USER", secretKey, 1000L * 60 * 60);

        PointWithdrawalRequest request = PointWithdrawalRequest.builder()
                .withdrawalAmount(point)
                .password("password")
                .build();

        PointResponse response = PointResponse.builder()
                .remainingPoint(point)
                .build();

        @Test
        @DisplayName("회원 포인트 출금 성공")
        public void WithdrawalPointSuccess() throws Exception {
            given(pointService.withdrawalPoint(email, request))
                    .willReturn(response);

            mockMvc.perform(post("/api/v1/users/points/withdrawal")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("SUCCESS"))
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result.remainingPoint").value(point));
        }

        @Test
        @DisplayName("회원 포인트 충전 실패 (가입된 회원이 아닌 경우) ")
        public void WithdrawalPointError() throws Exception {
            when(pointService.withdrawalPoint(email, request))
                    .thenThrow(new ShoeKreamException(USER_NOT_FOUND));

            mockMvc.perform(post("/api/v1/users/points/withdrawal")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("ERROR"))
                    .andExpect(jsonPath("$.result").exists());
        }

        @Test
        @DisplayName("회원 포인트 충전 실패 (비밀번호가 일치하지 않은 경우) ")
        public void WithdrawalPointError2() throws Exception {
            when(pointService.withdrawalPoint(email, request))
                    .thenThrow(new ShoeKreamException(WRONG_PASSWORD));

            mockMvc.perform(post("/api/v1/users/points/withdrawal")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("ERROR"))
                    .andExpect(jsonPath("$.result").exists());
        }
    }

    @Nested
    @DisplayName("회원 인증번호 전송 테스트")
    class UserCertification {
        String email = "email";
        UserCertificateRequest request = new UserCertificateRequest(email);
        UserCertificateResponse response = new UserCertificateResponse(email,"certificationNumber");
        @Test
        @DisplayName("회원 인증 성공")
        public void UserCertificationSuccess() throws Exception {
            given(userService.checkUserExistForCertificate(request))
                    .willReturn(response);

            mockMvc.perform(post("/api/v1/users/send-certification")
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("SUCCESS"))
                    .andExpect(jsonPath("$.result").exists());
        }
    }

    @Nested
    @DisplayName("회원 인증 테스트")
    class UserVerify {
        String email = "email";
        String certificationNumber = "certificationNumber";

        @Test
        @DisplayName("회원 인증 성공")
        public void UserVerifySuccess() throws Exception {
            willDoNothing().given(emailCertificationService)
                    .verifyEmail(certificationNumber,email);

            mockMvc.perform(get("/api/v1/users/verify" + String.format("?certificationNumber=%s&email=%s", certificationNumber, email)))
                    .andDo(print())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("SUCCESS"))
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result").value("ok"));
        }
    }
}