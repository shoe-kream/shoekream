package com.shoekream.service;

import com.shoekream.common.exception.ShoeKreamException;
import com.shoekream.domain.cart.Cart;
import com.shoekream.domain.cart.CartRepository;
import com.shoekream.domain.user.User;
import com.shoekream.domain.user.UserRepository;
import com.shoekream.domain.user.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static com.shoekream.common.exception.ErrorCode.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private BCryptPasswordEncoder encoder;

    @Mock
    private User mockUser;
    @Mock
    private Cart mockCart;

    @InjectMocks
    private UserService userService;

    String email;

    UserCreateRequest userCreateRequest;
    UserLoginRequest userLoginRequest;
    UserChangePasswordRequest userChangePasswordRequest;
    UserChangeNicknameRequest userChangeNicknameRequest;
    UserWithdrawRequest userWithdrawRequest;
    UserUpdateAccountRequest userUpdateAccountRequest;


    @BeforeEach
    void SetUp(){
        email = "email";

        userCreateRequest = new UserCreateRequest(email, "password", "nickname", "phone");
        userLoginRequest = new UserLoginRequest(email, "password");
        userChangePasswordRequest = new UserChangePasswordRequest("oldPassword", "newPassword");
        userChangeNicknameRequest = new UserChangeNicknameRequest("newNickname");
        userWithdrawRequest = new UserWithdrawRequest("password1!");
        userUpdateAccountRequest = new UserUpdateAccountRequest("bankName", "accountNumber", "depositor");


    }

    @Nested
    @DisplayName("회원가입 테스트")
    class UserJoin {

        @Test
        @DisplayName("회원가입 성공 테스트")
        public void success() {
            given(userRepository.existsByEmail(userCreateRequest.getEmail()))
                    .willReturn(false);
            given(userRepository.existsByNickname(userCreateRequest.getNickname()))
                    .willReturn(false);
            given(encoder.encode(userCreateRequest.getPassword()))
                    .willReturn("encoded");
            given(userRepository.save(any(User.class)))
                    .willReturn(mockUser);
            given(cartRepository.save(any(Cart.class)))
                    .willReturn(mockCart);

            assertDoesNotThrow(() -> userService.createUser(userCreateRequest));

            verify(userRepository, atLeastOnce()).existsByEmail(userCreateRequest.getEmail());
            verify(userRepository, atLeastOnce()).existsByNickname(userCreateRequest.getNickname());
            verify(userRepository, atLeastOnce()).save(any(User.class));
            verify(cartRepository, atLeastOnce()).save(any(Cart.class));

        }

        @Test
        @DisplayName("회원가입 실패 테스트 (이미 존재하는 이메일인 경우)")
        public void error1() {
            when(userRepository.existsByEmail(userCreateRequest.getEmail()))
                    .thenReturn(true);

            ShoeKreamException shoeKreamException = assertThrows(ShoeKreamException.class, () -> userService.createUser(userCreateRequest));

            assertThat(shoeKreamException.getErrorCode()).isEqualTo(DUPLICATE_EMAIL);

            verify(userRepository, atLeastOnce()).existsByEmail(userCreateRequest.getEmail());

        }

        @Test
        @DisplayName("회원가입 실패 테스트 (이미 존재하는 닉네임인 경우)")
        public void error2() {
            given(userRepository.existsByEmail(userCreateRequest.getEmail()))
                    .willReturn(false);

            when(userRepository.existsByNickname(userCreateRequest.getNickname()))
                    .thenReturn(true);

            ShoeKreamException shoeKreamException = assertThrows(ShoeKreamException.class, () -> userService.createUser(userCreateRequest));

            assertThat(shoeKreamException.getErrorCode()).isEqualTo(DUPLICATE_NICKNAME);

            verify(userRepository, atLeastOnce()).existsByEmail(userCreateRequest.getEmail());
            verify(userRepository, atLeastOnce()).existsByNickname(userCreateRequest.getNickname());

        }
    }

    @Nested
    @DisplayName("회원 로그인 테스트")
    class UserLogin {

        @Test
        @DisplayName("회원 로그인 성공 테스트")
        public void success() {
            given(userRepository.findByEmail(userLoginRequest.getEmail()))
                    .willReturn(Optional.of(mockUser));

            assertDoesNotThrow(() -> userService.loginUser(userLoginRequest));

            verify(userRepository, atLeastOnce()).findByEmail(userLoginRequest.getEmail());
        }

        @Test
        @DisplayName("회원 로그인 실패 테스트 (가입되지 않은 회원인 경우)")
        public void error1() {
            when(userRepository.findByEmail(userLoginRequest.getEmail()))
                    .thenReturn(Optional.empty());

            ShoeKreamException shoeKreamException = assertThrows(ShoeKreamException.class, () -> userService.loginUser(userLoginRequest));

            assertThat(shoeKreamException.getErrorCode()).isEqualTo(USER_NOT_FOUND);

            verify(userRepository, atLeastOnce()).findByEmail(userLoginRequest.getEmail());

        }

        @Test
        @DisplayName("회원 로그인 실패 테스트 (비밀번호가 일치하지 않는 경우)")
        public void error2() {
            given(userRepository.findByEmail(userLoginRequest.getEmail()))
                    .willReturn(Optional.of(mockUser));

            doThrow(new ShoeKreamException(WRONG_PASSWORD))
                    .when(mockUser).checkPassword(encoder, userLoginRequest.getPassword());


            ShoeKreamException shoeKreamException = assertThrows(ShoeKreamException.class, () -> userService.loginUser(userLoginRequest));
            assertThat(shoeKreamException.getErrorCode()).isEqualTo(WRONG_PASSWORD);

            verify(userRepository, atLeastOnce()).findByEmail(userLoginRequest.getEmail());
            verify(mockUser, atLeastOnce()).checkPassword(encoder, userLoginRequest.getPassword());

        }
    }

    @Nested
    @DisplayName("회원 비밀번호 변경 테스트")
    class UserChangePassword {



        @Test
        @DisplayName("회원 비밀번호 변경 성공 테스트")
        public void success() {
            given(userRepository.findByEmail(email))
                    .willReturn(Optional.of(mockUser));

            assertDoesNotThrow(() -> userService.changePasswordUser(userChangePasswordRequest, email));

            verify(userRepository, atLeastOnce()).findByEmail(email);
        }

        @Test
        @DisplayName("회원 비밀번호 변경 실패 테스트 (가입되지 않은 회원인 경우)")
        public void error1() {
            when(userRepository.findByEmail(email))
                    .thenThrow(new ShoeKreamException(USER_NOT_FOUND));

            ShoeKreamException shoeKreamException = assertThrows(ShoeKreamException.class, () -> userService.changePasswordUser(userChangePasswordRequest, email));
            assertThat(shoeKreamException.getErrorCode()).isEqualTo(USER_NOT_FOUND);

            verify(userRepository, atLeastOnce()).findByEmail(email);

        }

        @Test
        @DisplayName("회원 비밀번호 변경 실패 테스트 (변경 전 비밀번호가 일치하지 않는 경우)")
        public void error2() {

            given(userRepository.findByEmail(email))
                    .willReturn(Optional.of(mockUser));

            doThrow(new ShoeKreamException(WRONG_PASSWORD))
                    .when(mockUser).checkPassword(encoder, userChangePasswordRequest.getOldPassword());

            ShoeKreamException shoeKreamException = assertThrows(ShoeKreamException.class, () -> userService.changePasswordUser(userChangePasswordRequest, email));
            assertThat(shoeKreamException.getErrorCode()).isEqualTo(WRONG_PASSWORD);

            verify(userRepository, atLeastOnce()).findByEmail(email);
            verify(mockUser, atLeastOnce()).checkPassword(encoder, userChangePasswordRequest.getOldPassword());

        }
    }
    @Nested
    @DisplayName("회원 닉네임 변경 테스트")
    class UserChangeNickname {


        @Test
        @DisplayName("회원 닉네임 변경 성공 테스트")
        public void success() {
            given(userRepository.findByEmail(email))
                    .willReturn(Optional.of(mockUser));

            assertDoesNotThrow(() -> userService.changeNicknameUser(userChangeNicknameRequest, email));

            verify(userRepository, atLeastOnce()).findByEmail(email);
        }

        @Test
        @DisplayName("회원 닉네임 변경 실패 테스트 (가입되지 않은 회원인 경우)")
        public void error1() {
            when(userRepository.findByEmail(email))
                    .thenThrow(new ShoeKreamException(USER_NOT_FOUND));

            ShoeKreamException shoeKreamException = assertThrows(ShoeKreamException.class, () -> userService.changeNicknameUser(userChangeNicknameRequest, email));
            assertThat(shoeKreamException.getErrorCode()).isEqualTo(USER_NOT_FOUND);

            verify(userRepository, atLeastOnce()).findByEmail(email);

        }

        @Test
        @DisplayName("회원 닉네임 변경 실패 테스트 (중복되는 닉네임인 경우)")
        public void error2() {

            given(userRepository.findByEmail(email))
                    .willReturn(Optional.of(mockUser));

            when(userRepository.existsByNickname(userChangeNicknameRequest.getNickname()))
                    .thenReturn(true);

            ShoeKreamException shoeKreamException = assertThrows(ShoeKreamException.class, () -> userService.changeNicknameUser(userChangeNicknameRequest, email));
            assertThat(shoeKreamException.getErrorCode()).isEqualTo(DUPLICATE_NICKNAME);

            verify(userRepository, atLeastOnce()).findByEmail(email);
            verify(userRepository, atLeastOnce()).existsByNickname(userChangeNicknameRequest.getNickname());

        }

        @Test
        @DisplayName("회원 닉네임 변경 실패 테스트 (변경한지 7일이 지나지 않은 경우)")
        public void error3() {

            given(userRepository.findByEmail(email))
                    .willReturn(Optional.of(mockUser));

            given(userRepository.existsByNickname(userChangeNicknameRequest.getNickname()))
                    .willReturn(false);

            doThrow(new ShoeKreamException(CHANGE_NOT_ALLOWED))
                    .when(mockUser).changeNickname(userChangeNicknameRequest);

            ShoeKreamException shoeKreamException = assertThrows(ShoeKreamException.class, () -> userService.changeNicknameUser(userChangeNicknameRequest, email));
            assertThat(shoeKreamException.getErrorCode()).isEqualTo(CHANGE_NOT_ALLOWED);

            verify(userRepository, atLeastOnce()).findByEmail(email);
            verify(userRepository, atLeastOnce()).existsByNickname(userChangeNicknameRequest.getNickname());
            verify(mockUser, atLeastOnce()).changeNickname(userChangeNicknameRequest);

        }
    }

    @Nested
    @DisplayName("회원 탈퇴 테스트")
    class UserWithdraw {


        @Test
        @DisplayName("회원 탈퇴 성공 테스트")
        public void success() {
            given(userRepository.findByEmail(email))
                    .willReturn(Optional.of(mockUser));

            assertDoesNotThrow(() -> userService.withdrawUser(userWithdrawRequest, email));

            verify(userRepository, atLeastOnce()).findByEmail(email);
        }

        @Test
        @DisplayName("회원 탈퇴 실패 테스트 (가입되지 않은 회원인 경우)")
        public void error1() {
            when(userRepository.findByEmail(email))
                    .thenThrow(new ShoeKreamException(USER_NOT_FOUND));

            ShoeKreamException shoeKreamException = assertThrows(ShoeKreamException.class, () -> userService.withdrawUser(userWithdrawRequest, email));
            assertThat(shoeKreamException.getErrorCode()).isEqualTo(USER_NOT_FOUND);

            verify(userRepository, atLeastOnce()).findByEmail(email);

        }

        @Test
        @DisplayName("회원 탈퇴 실패 테스트 (비밀번호가 일치하지 않는 경우)")
        public void error2() {

            given(userRepository.findByEmail(email))
                    .willReturn(Optional.of(mockUser));

            doThrow(new ShoeKreamException(WRONG_PASSWORD))
                    .when(mockUser).checkPassword(encoder, userWithdrawRequest.getPassword());

            ShoeKreamException shoeKreamException = assertThrows(ShoeKreamException.class, () -> userService.withdrawUser(userWithdrawRequest, email));
            assertThat(shoeKreamException.getErrorCode()).isEqualTo(WRONG_PASSWORD);

            verify(userRepository, atLeastOnce()).findByEmail(email);
            verify(mockUser, atLeastOnce()).checkPassword(encoder, userWithdrawRequest.getPassword());

        }

        @Test
        @DisplayName("회원 탈퇴 실패 테스트 (잔여 포인트가 남아 있는 경우)")
        public void error3() {

            given(userRepository.findByEmail(email))
                    .willReturn(Optional.of(mockUser));
            doNothing().when(mockUser)
                    .checkPassword(encoder, userWithdrawRequest.getPassword());

            when(mockUser.hasPoint())
                    .thenReturn(true);

            ShoeKreamException shoeKreamException = assertThrows(ShoeKreamException.class, () -> userService.withdrawUser(userWithdrawRequest, email));
            assertThat(shoeKreamException.getErrorCode()).isEqualTo(WITHDRAWAL_NOT_ALLOWED);

            verify(userRepository, atLeastOnce()).findByEmail(email);
            verify(mockUser, atLeastOnce()).checkPassword(encoder, userWithdrawRequest.getPassword());

        }
    }

    @Nested
    @DisplayName("회원 계좌 정보 입력 테스트")
    class UserUpdateAccount {


        @Test
        @DisplayName("회원 계좌 정보 입력 성공 테스트")
        public void success() {
            given(userRepository.findByEmail(email))
                    .willReturn(Optional.of(mockUser));

            assertDoesNotThrow(() -> userService.updateAccountUser(userUpdateAccountRequest, email));

            verify(userRepository, atLeastOnce()).findByEmail(email);
        }

        @Test
        @DisplayName("회원 계좌 정보 입력 실패 테스트 (가입되지 않은 회원인 경우)")
        public void error1() {
            when(userRepository.findByEmail(email))
                    .thenThrow(new ShoeKreamException(USER_NOT_FOUND));

            ShoeKreamException shoeKreamException = assertThrows(ShoeKreamException.class, () -> userService.updateAccountUser(userUpdateAccountRequest, email));
            assertThat(shoeKreamException.getErrorCode()).isEqualTo(USER_NOT_FOUND);

            verify(userRepository, atLeastOnce()).findByEmail(email);

        }

    }

    @Nested
    @DisplayName("회원 계좌 정보 조회 테스트")
    class UserGetAccount {

        @Test
        @DisplayName("회원 계좌 정보 조회 성공 테스트")
        public void success() {
            given(userRepository.findByEmail(email))
                    .willReturn(Optional.of(mockUser));

            assertDoesNotThrow(() -> userService.getAccountUser(email));

            verify(userRepository, atLeastOnce()).findByEmail(email);
        }

        @Test
        @DisplayName("회원 계좌 정보 조회 실패 테스트 (가입되지 않은 회원인 경우)")
        public void error1() {
            when(userRepository.findByEmail(email))
                    .thenThrow(new ShoeKreamException(USER_NOT_FOUND));

            ShoeKreamException shoeKreamException = assertThrows(ShoeKreamException.class, () -> userService.getAccountUser(email));
            assertThat(shoeKreamException.getErrorCode()).isEqualTo(USER_NOT_FOUND);

            verify(userRepository, atLeastOnce()).findByEmail(email);
        }

    }
}