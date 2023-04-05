package com.shoekream.service;

import com.shoekream.common.exception.ShoeKreamException;
import com.shoekream.domain.cart.Cart;
import com.shoekream.domain.cart.CartRepository;
import com.shoekream.domain.user.User;
import com.shoekream.domain.user.UserRepository;
import com.shoekream.domain.user.dto.*;
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

    @Nested
    @DisplayName("회원가입 테스트")
    class UserJoin {

        UserCreateRequest request = new UserCreateRequest("email", "password", "nickname", "phone");

        @Test
        @DisplayName("회원가입 성공 테스트")
        public void success() {
            given(userRepository.existsByEmail(request.getEmail()))
                    .willReturn(false);
            given(userRepository.existsByNickname(request.getNickname()))
                    .willReturn(false);
            given(encoder.encode(request.getPassword()))
                    .willReturn("encoded");
            given(userRepository.save(any(User.class)))
                    .willReturn(mockUser);
            given(cartRepository.save(any(Cart.class)))
                    .willReturn(mockCart);

            assertDoesNotThrow(() -> userService.createUser(request));

            verify(userRepository, atLeastOnce()).existsByEmail(request.getEmail());
            verify(userRepository, atLeastOnce()).existsByNickname(request.getNickname());
            verify(userRepository, atLeastOnce()).save(any(User.class));
            verify(cartRepository, atLeastOnce()).save(any(Cart.class));

        }

        @Test
        @DisplayName("회원가입 실패 테스트 (이미 존재하는 이메일인 경우)")
        public void error1() {
            when(userRepository.existsByEmail(request.getEmail()))
                    .thenReturn(true);

            ShoeKreamException shoeKreamException = assertThrows(ShoeKreamException.class, () -> userService.createUser(request));

            assertThat(shoeKreamException.getErrorCode()).isEqualTo(DUPLICATE_EMAIL);

            verify(userRepository, atLeastOnce()).existsByEmail(request.getEmail());

        }

        @Test
        @DisplayName("회원가입 실패 테스트 (이미 존재하는 닉네임인 경우)")
        public void error2() {
            given(userRepository.existsByEmail(request.getEmail()))
                    .willReturn(false);

            when(userRepository.existsByNickname(request.getNickname()))
                    .thenReturn(true);

            ShoeKreamException shoeKreamException = assertThrows(ShoeKreamException.class, () -> userService.createUser(request));

            assertThat(shoeKreamException.getErrorCode()).isEqualTo(DUPLICATE_NICKNAME);

            verify(userRepository, atLeastOnce()).existsByEmail(request.getEmail());
            verify(userRepository, atLeastOnce()).existsByNickname(request.getNickname());

        }
    }

    @Nested
    @DisplayName("회원 로그인 테스트")
    class UserLogin {
        UserLoginRequest request = new UserLoginRequest("email", "password");

        @Test
        @DisplayName("회원 로그인 성공 테스트")
        public void success() {
            given(userRepository.findByEmail(request.getEmail()))
                    .willReturn(Optional.of(mockUser));

            assertDoesNotThrow(() -> userService.loginUser(request));

            verify(userRepository, atLeastOnce()).findByEmail(request.getEmail());
        }

        @Test
        @DisplayName("회원 로그인 실패 테스트 (가입되지 않은 회원인 경우)")
        public void error1() {
            when(userRepository.findByEmail(request.getEmail()))
                    .thenReturn(Optional.empty());

            ShoeKreamException shoeKreamException = assertThrows(ShoeKreamException.class, () -> userService.loginUser(request));

            assertThat(shoeKreamException.getErrorCode()).isEqualTo(USER_NOT_FOUND);

            verify(userRepository, atLeastOnce()).findByEmail(request.getEmail());

        }

        @Test
        @DisplayName("회원 로그인 실패 테스트 (비밀번호가 일치하지 않는 경우)")
        public void error2() {
            given(userRepository.findByEmail(request.getEmail()))
                    .willReturn(Optional.of(mockUser));

            doThrow(new ShoeKreamException(WRONG_PASSWORD))
                    .when(mockUser).checkPassword(encoder, request.getPassword());


            ShoeKreamException shoeKreamException = assertThrows(ShoeKreamException.class, () -> userService.loginUser(request));
            assertThat(shoeKreamException.getErrorCode()).isEqualTo(WRONG_PASSWORD);

            verify(userRepository, atLeastOnce()).findByEmail(request.getEmail());
            verify(mockUser, atLeastOnce()).checkPassword(encoder, request.getPassword());

        }
    }

    @Nested
    @DisplayName("회원 비밀번호 변경 테스트")
    class UserChangePassword {

        String email = "email";

        UserChangePasswordRequest request = new UserChangePasswordRequest("oldPassword", "newPassword");

        @Test
        @DisplayName("회원 비밀번호 변경 성공 테스트")
        public void success() {
            given(userRepository.findByEmail(email))
                    .willReturn(Optional.of(mockUser));

            assertDoesNotThrow(() -> userService.changePasswordUser(request, email));

            verify(userRepository, atLeastOnce()).findByEmail(email);
        }

        @Test
        @DisplayName("회원 비밀번호 변경 실패 테스트 (가입되지 않은 회원인 경우)")
        public void error1() {
            when(userRepository.findByEmail(email))
                    .thenThrow(new ShoeKreamException(USER_NOT_FOUND));

            ShoeKreamException shoeKreamException = assertThrows(ShoeKreamException.class, () -> userService.changePasswordUser(request, email));
            assertThat(shoeKreamException.getErrorCode()).isEqualTo(USER_NOT_FOUND);

            verify(userRepository, atLeastOnce()).findByEmail(email);

        }

        @Test
        @DisplayName("회원 비밀번호 변경 실패 테스트 (변경 전 비밀번호가 일치하지 않는 경우)")
        public void error2() {

            given(userRepository.findByEmail(email))
                    .willReturn(Optional.of(mockUser));

            doThrow(new ShoeKreamException(WRONG_PASSWORD))
                    .when(mockUser).checkPassword(encoder, request.getOldPassword());

            ShoeKreamException shoeKreamException = assertThrows(ShoeKreamException.class, () -> userService.changePasswordUser(request, email));
            assertThat(shoeKreamException.getErrorCode()).isEqualTo(WRONG_PASSWORD);

            verify(userRepository, atLeastOnce()).findByEmail(email);
            verify(mockUser, atLeastOnce()).checkPassword(encoder, request.getOldPassword());

        }
    }
    @Nested
    @DisplayName("회원 닉네임 변경 테스트")
    class UserChangeNickname {

        String email = "email";

        UserChangeNicknameRequest request = new UserChangeNicknameRequest("newNickname");

        @Test
        @DisplayName("회원 닉네임 변경 성공 테스트")
        public void success() {
            given(userRepository.findByEmail(email))
                    .willReturn(Optional.of(mockUser));

            assertDoesNotThrow(() -> userService.changeNicknameUser(request, email));

            verify(userRepository, atLeastOnce()).findByEmail(email);
        }

        @Test
        @DisplayName("회원 닉네임 변경 실패 테스트 (가입되지 않은 회원인 경우)")
        public void error1() {
            when(userRepository.findByEmail(email))
                    .thenThrow(new ShoeKreamException(USER_NOT_FOUND));

            ShoeKreamException shoeKreamException = assertThrows(ShoeKreamException.class, () -> userService.changeNicknameUser(request, email));
            assertThat(shoeKreamException.getErrorCode()).isEqualTo(USER_NOT_FOUND);

            verify(userRepository, atLeastOnce()).findByEmail(email);

        }

        @Test
        @DisplayName("회원 닉네임 변경 실패 테스트 (중복되는 닉네임인 경우)")
        public void error2() {

            given(userRepository.findByEmail(email))
                    .willReturn(Optional.of(mockUser));

            when(userRepository.existsByNickname(request.getNickname()))
                    .thenReturn(true);

            ShoeKreamException shoeKreamException = assertThrows(ShoeKreamException.class, () -> userService.changeNicknameUser(request, email));
            assertThat(shoeKreamException.getErrorCode()).isEqualTo(DUPLICATE_NICKNAME);

            verify(userRepository, atLeastOnce()).findByEmail(email);
            verify(userRepository, atLeastOnce()).existsByNickname(request.getNickname());

        }

        @Test
        @DisplayName("회원 닉네임 변경 실패 테스트 (변경한지 7일이 지나지 않은 경우)")
        public void error3() {

            given(userRepository.findByEmail(email))
                    .willReturn(Optional.of(mockUser));

            given(userRepository.existsByNickname(request.getNickname()))
                    .willReturn(false);

            doThrow(new ShoeKreamException(CHANGE_NOT_ALLOWED))
                    .when(mockUser).changeNickname(request);

            ShoeKreamException shoeKreamException = assertThrows(ShoeKreamException.class, () -> userService.changeNicknameUser(request, email));
            assertThat(shoeKreamException.getErrorCode()).isEqualTo(CHANGE_NOT_ALLOWED);

            verify(userRepository, atLeastOnce()).findByEmail(email);
            verify(userRepository, atLeastOnce()).existsByNickname(request.getNickname());
            verify(mockUser, atLeastOnce()).changeNickname(request);

        }
    }

    @Nested
    @DisplayName("회원 탈퇴 테스트")
    class UserWithdraw {

        String email = "email";

        UserWithdrawRequest request = new UserWithdrawRequest("password1!");

        @Test
        @DisplayName("회원 탈퇴 성공 테스트")
        public void success() {
            given(userRepository.findByEmail(email))
                    .willReturn(Optional.of(mockUser));

            assertDoesNotThrow(() -> userService.withdrawUser(request, email));

            verify(userRepository, atLeastOnce()).findByEmail(email);
        }

        @Test
        @DisplayName("회원 탈퇴 실패 테스트 (가입되지 않은 회원인 경우)")
        public void error1() {
            when(userRepository.findByEmail(email))
                    .thenThrow(new ShoeKreamException(USER_NOT_FOUND));

            ShoeKreamException shoeKreamException = assertThrows(ShoeKreamException.class, () -> userService.withdrawUser(request, email));
            assertThat(shoeKreamException.getErrorCode()).isEqualTo(USER_NOT_FOUND);

            verify(userRepository, atLeastOnce()).findByEmail(email);

        }

        @Test
        @DisplayName("회원 탈퇴 실패 테스트 (비밀번호가 일치하지 않는 경우)")
        public void error2() {

            given(userRepository.findByEmail(email))
                    .willReturn(Optional.of(mockUser));

            doThrow(new ShoeKreamException(WRONG_PASSWORD))
                    .when(mockUser).checkPassword(encoder, request.getPassword());

            ShoeKreamException shoeKreamException = assertThrows(ShoeKreamException.class, () -> userService.withdrawUser(request, email));
            assertThat(shoeKreamException.getErrorCode()).isEqualTo(WRONG_PASSWORD);

            verify(userRepository, atLeastOnce()).findByEmail(email);
            verify(mockUser, atLeastOnce()).checkPassword(encoder, request.getPassword());

        }

        @Test
        @DisplayName("회원 탈퇴 실패 테스트 (잔여 포인트가 남아 있는 경우)")
        public void error3() {

            given(userRepository.findByEmail(email))
                    .willReturn(Optional.of(mockUser));
            doNothing().when(mockUser)
                    .checkPassword(encoder,request.getPassword());

            when(mockUser.hasPoint())
                    .thenReturn(true);

            ShoeKreamException shoeKreamException = assertThrows(ShoeKreamException.class, () -> userService.withdrawUser(request, email));
            assertThat(shoeKreamException.getErrorCode()).isEqualTo(WITHDRAWAL_NOT_ALLOWED_POINT);

            verify(userRepository, atLeastOnce()).findByEmail(email);
            verify(mockUser, atLeastOnce()).checkPassword(encoder, request.getPassword());

        }
    }

    @Nested
    @DisplayName("회원 계좌 정보 입력 테스트")
    class UserUpdateAccount {

        String email = "email";

        UserUpdateAccountRequest request = new UserUpdateAccountRequest("bankName", "accountNumber", "depositor");

        @Test
        @DisplayName("회원 계좌 정보 입력 성공 테스트")
        public void success() {
            given(userRepository.findByEmail(email))
                    .willReturn(Optional.of(mockUser));

            assertDoesNotThrow(() -> userService.updateAccountUser(request, email));

            verify(userRepository, atLeastOnce()).findByEmail(email);
        }

        @Test
        @DisplayName("회원 계좌 정보 입력 실패 테스트 (가입되지 않은 회원인 경우)")
        public void error1() {
            when(userRepository.findByEmail(email))
                    .thenThrow(new ShoeKreamException(USER_NOT_FOUND));

            ShoeKreamException shoeKreamException = assertThrows(ShoeKreamException.class, () -> userService.updateAccountUser(request, email));
            assertThat(shoeKreamException.getErrorCode()).isEqualTo(USER_NOT_FOUND);

            verify(userRepository, atLeastOnce()).findByEmail(email);

        }

    }
}