package com.shoekream.service;

import com.shoekream.common.exception.ErrorCode;
import com.shoekream.common.exception.ShoeKreamException;
import com.shoekream.domain.cart.Cart;
import com.shoekream.domain.cart.CartRepository;
import com.shoekream.domain.user.User;
import com.shoekream.domain.user.UserRepository;
import com.shoekream.domain.user.dto.UserCreateRequest;
import com.shoekream.domain.user.dto.UserLoginRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
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
        public void success(){
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

            verify(userRepository,atLeastOnce()).existsByEmail(request.getEmail());
            verify(userRepository,atLeastOnce()).existsByNickname(request.getNickname());
            verify(userRepository,atLeastOnce()).save(any(User.class));
            verify(cartRepository,atLeastOnce()).save(any(Cart.class));

        }

        @Test
        @DisplayName("회원가입 실패 테스트 (이미 존재하는 이메일인 경우)")
        public void error1(){
            when(userRepository.existsByEmail(request.getEmail()))
                    .thenReturn(true);

            ShoeKreamException shoeKreamException = assertThrows(ShoeKreamException.class, () -> userService.createUser(request));

            assertThat(shoeKreamException.getErrorCode()).isEqualTo(ErrorCode.DUPLICATE_EMAIL);

            verify(userRepository,atLeastOnce()).existsByEmail(request.getEmail());

        }

        @Test
        @DisplayName("회원가입 실패 테스트 (이미 존재하는 닉네임인 경우)")
        public void error2(){
            given(userRepository.existsByEmail(request.getEmail()))
                    .willReturn(false);

            when(userRepository.existsByNickname(request.getNickname()))
                    .thenReturn(true);

            ShoeKreamException shoeKreamException = assertThrows(ShoeKreamException.class, () -> userService.createUser(request));

            assertThat(shoeKreamException.getErrorCode()).isEqualTo(ErrorCode.DUPLICATE_NICKNAME);

            verify(userRepository,atLeastOnce()).existsByEmail(request.getEmail());
            verify(userRepository,atLeastOnce()).existsByNickname(request.getNickname());

        }
    }

    @Nested
    @DisplayName("회원 로그인 테스트")
    class UserLogin{
        UserLoginRequest request = new UserLoginRequest("email", "password");

        @Test
        @DisplayName("회원 로그인 성공 테스트")
        public void success(){
            given(userRepository.findByEmail(request.getEmail()))
                    .willReturn(Optional.of(mockUser));

            assertDoesNotThrow(() -> userService.loginUser(request));

            verify(userRepository, atLeastOnce()).findByEmail(request.getEmail());
        }

        @Test
        @DisplayName("회원 로그인 실패 테스트 (가입되지 않은 회원인 경우)")
        public void error1(){
            when(userRepository.findByEmail(request.getEmail()))
                    .thenReturn(Optional.empty());

            ShoeKreamException shoeKreamException = assertThrows(ShoeKreamException.class, () -> userService.loginUser(request));

            assertThat(shoeKreamException.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);

            verify(userRepository, atLeastOnce()).findByEmail(request.getEmail());

        }

        @Test
        @DisplayName("회원 로그인 실패 테스트 (비밀번호가 일치하지 않는 경우)")
        public void error2(){
            given(userRepository.findByEmail(request.getEmail()))
                    .willReturn(Optional.of(mockUser));

            doThrow(new ShoeKreamException(ErrorCode.WRONG_PASSWORD))
                    .when(mockUser).checkPassword(encoder, request.getPassword());


            ShoeKreamException shoeKreamException = assertThrows(ShoeKreamException.class, () -> userService.loginUser(request));
            assertThat(shoeKreamException.getErrorCode()).isEqualTo(ErrorCode.WRONG_PASSWORD);

            verify(userRepository, atLeastOnce()).findByEmail(request.getEmail());
            verify(mockUser, atLeastOnce()).checkPassword(encoder,request.getPassword());

        }
    }
}