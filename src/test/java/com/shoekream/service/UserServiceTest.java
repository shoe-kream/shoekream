package com.shoekream.service;

import com.shoekream.common.exception.ErrorCode;
import com.shoekream.common.exception.ShoeKreamException;
import com.shoekream.domain.cart.Cart;
import com.shoekream.domain.cart.CartRepository;
import com.shoekream.domain.user.User;
import com.shoekream.domain.user.UserRepository;
import com.shoekream.domain.user.dto.UserCreateRequest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

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

            Assertions.assertThat(shoeKreamException.getErrorCode()).isEqualTo(ErrorCode.DUPLICATE_EMAIL);

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

            Assertions.assertThat(shoeKreamException.getErrorCode()).isEqualTo(ErrorCode.DUPLICATE_NICKNAME);

            verify(userRepository,atLeastOnce()).existsByEmail(request.getEmail());
            verify(userRepository,atLeastOnce()).existsByNickname(request.getNickname());

        }
    }
}