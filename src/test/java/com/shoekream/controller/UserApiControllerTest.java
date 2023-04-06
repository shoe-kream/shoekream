package com.shoekream.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shoekream.common.aop.BindingCheck;
import com.shoekream.common.config.SecurityConfig;
import com.shoekream.common.exception.ErrorCode;
import com.shoekream.common.exception.ShoeKreamException;
import com.shoekream.common.util.JwtUtil;
import com.shoekream.domain.address.dto.AddressAddRequest;
import com.shoekream.domain.address.dto.AddressResponse;
import com.shoekream.domain.user.Account;
import com.shoekream.domain.user.dto.*;
import com.shoekream.service.AddressService;
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

import static com.shoekream.common.exception.ErrorCode.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.when;
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
                    .thenThrow(new ShoeKreamException(WITHDRAWAL_NOT_ALLOWED_POINT));

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
    class UserAddAddress{
        Long addressId = 1L;
        AddressAddRequest request = AddressAddRequest.builder()
                .addressName("addressName")
                .roadNameAddress("roadNameAddress")
                .detailedAddress("detailedAddress")
                .postalCode("postalCode")
                .build();

        AddressResponse response = AddressResponse.builder()
                .address("full address")
                .addressId(addressId)
                .build();

        String token = JwtUtil.createToken("email", "ROLE_USER", secretKey, 1000L * 60 * 60);
        @Test
        @DisplayName("회원 주소 등록 성공")
        void success() throws Exception {

            given(addressService.addAddress(anyString(), any(AddressAddRequest.class)))
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
                    .andExpect(jsonPath("$.result.address").value("full address"));
        }

        @Test
        @DisplayName("회원 주소 등록 실패 (가입된 회원이 아닌 경우)")
        void error() throws Exception {

            when(addressService.addAddress(anyString(), any(AddressAddRequest.class)))
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
}