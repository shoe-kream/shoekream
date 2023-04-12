package com.shoekream.service;

import com.shoekream.common.exception.ErrorCode;
import com.shoekream.common.exception.ShoeKreamException;
import com.shoekream.domain.point.Point;
import com.shoekream.domain.point.PointDivision;
import com.shoekream.domain.point.PointRepository;
import com.shoekream.domain.point.dto.PointChargeRequest;
import com.shoekream.domain.point.dto.PointWithdrawalRequest;
import com.shoekream.domain.user.User;
import com.shoekream.domain.user.UserRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Optional;

import static com.shoekream.common.exception.ErrorCode.*;
import static com.shoekream.domain.point.PointDivision.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class PointServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PointRepository pointRepository;

    @Mock
    private BCryptPasswordEncoder encoder;

    @InjectMocks
    private PointService pointService;

    @Mock
    private User mockUser;

    @Mock
    private Point mockPoint;

    @Nested
    @DisplayName("포인트 조회 테스트")
    class GetPoint {

        String email = "email";

        @Test
        @DisplayName("회원 포인트 조회 성공 테스트")
        public void getUserPointSuccess() {
            given(userRepository.findByEmail(email))
                    .willReturn(Optional.of(mockUser));

            assertDoesNotThrow(() -> pointService.getUserPoint(email));

            verify(userRepository, atLeastOnce()).findByEmail(email);
        }

        @Test
        @DisplayName("회원 포인트 조회 실패 테스트 (가입된 회원이 아닌 경우)")
        public void getUserPointError() {
            when(userRepository.findByEmail(email))
                    .thenThrow(new ShoeKreamException(USER_NOT_FOUND));

            ShoeKreamException shoeKreamException = assertThrows(ShoeKreamException.class, () -> pointService.getUserPoint(email));
            assertThat(shoeKreamException.getErrorCode()).isEqualTo(USER_NOT_FOUND);

            verify(userRepository, atLeastOnce()).findByEmail(email);
        }

        @Test
        @DisplayName("회원 분류별 포인트 리스트 조회 성공 테스트")
        public void getUserPointHistorySuccess() {
            given(userRepository.findByEmail(email))
                    .willReturn(Optional.of(mockUser));
            given(pointRepository.findAllByUserAndDivisionOrderByCreatedDateDesc(mockUser, POINT_CHARGE))
                    .willReturn(List.of(mockPoint));

            assertDoesNotThrow(() -> pointService.getHistoryPointByDivision(email, POINT_CHARGE));

            verify(userRepository, atLeastOnce()).findByEmail(email);
            verify(pointRepository, atLeastOnce()).findAllByUserAndDivisionOrderByCreatedDateDesc(mockUser, POINT_CHARGE);
        }

        @Test
        @DisplayName("회원 분류별 포인트 리스트 조회 실패 테스트 (가입된 회원이 아닌 경우)")
        public void getUserPointHistoryError() {
            when(userRepository.findByEmail(email))
                    .thenThrow(new ShoeKreamException(USER_NOT_FOUND));

            ShoeKreamException shoeKreamException = assertThrows(ShoeKreamException.class, () -> pointService.getHistoryPointByDivision(email, POINT_CHARGE));
            assertThat(shoeKreamException.getErrorCode()).isEqualTo(USER_NOT_FOUND);

            verify(userRepository, atLeastOnce()).findByEmail(email);
        }
    }

    @Nested
    @DisplayName("포인트 충전 테스트")
    class ChargePoint {
        String email = "email";
        Long amount = 1000L;

        PointChargeRequest request = PointChargeRequest.builder()
                .amount(amount).build();

        @Test
        @DisplayName("포인트 충전 성공 테스트")
        public void chargePointSuccess() {
            given(userRepository.findByEmail(email))
                    .willReturn(Optional.of(mockUser));

            assertDoesNotThrow(() -> pointService.chargePoint(email,request));

            verify(userRepository, atLeastOnce()).findByEmail(email);
        }

        @Test
        @DisplayName("포인트 충전 실패 테스트 (가입된 회원이 아닌 경우)")
        public void chargePointError() {
            when(userRepository.findByEmail(email))
                    .thenThrow(new ShoeKreamException(USER_NOT_FOUND));

            ShoeKreamException shoeKreamException = assertThrows(ShoeKreamException.class, () -> pointService.chargePoint(email,request));
            assertThat(shoeKreamException.getErrorCode()).isEqualTo(USER_NOT_FOUND);

            verify(userRepository, atLeastOnce()).findByEmail(email);
        }
    }

    @Nested
    @DisplayName("포인트 출금 테스트")
    class WithdrawalPoint {
        String email = "email";
        String password = "password";
        Long amount = 1000L;

        PointWithdrawalRequest request = PointWithdrawalRequest.builder()
                .withdrawalAmount(amount)
                .password(password).build();

        @Test
        @DisplayName("포인트 출금 성공 테스트")
        public void chargePointSuccess() {
            given(userRepository.findByEmail(email))
                    .willReturn(Optional.of(mockUser));
            doNothing().when(mockUser)
                            .checkPassword(encoder,request.getPassword());


            assertDoesNotThrow(() -> pointService.withdrawalPoint(email,request));

            verify(userRepository, atLeastOnce()).findByEmail(email);
            verify(mockUser, atLeastOnce()).checkPassword(encoder,request.getPassword());
        }

        @Test
        @DisplayName("포인트 충전 실패 테스트 (가입된 회원이 아닌 경우)")
        public void chargePointError() {
            when(userRepository.findByEmail(email))
                    .thenThrow(new ShoeKreamException(USER_NOT_FOUND));

            ShoeKreamException shoeKreamException = assertThrows(ShoeKreamException.class, () -> pointService.withdrawalPoint(email,request));
            assertThat(shoeKreamException.getErrorCode()).isEqualTo(USER_NOT_FOUND);

            verify(userRepository, atLeastOnce()).findByEmail(email);
        }

        @Test
        @DisplayName("포인트 충전 실패 테스트 (비밀번호가 일치하지 않는 경우)")
        public void chargePointError2() {
            given(userRepository.findByEmail(email))
                    .willReturn(Optional.of(mockUser));

            doThrow(new ShoeKreamException(WRONG_PASSWORD)).when(mockUser)
                    .checkPassword(encoder,request.getPassword());

            ShoeKreamException shoeKreamException = assertThrows(ShoeKreamException.class, () -> pointService.withdrawalPoint(email,request));
            assertThat(shoeKreamException.getErrorCode()).isEqualTo(WRONG_PASSWORD);

            verify(userRepository, atLeastOnce()).findByEmail(email);
            verify(mockUser, atLeastOnce()).checkPassword(encoder,request.getPassword());

        }
    }

}