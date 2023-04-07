package com.shoekream.service;

import com.shoekream.common.exception.ErrorCode;
import com.shoekream.common.exception.ShoeKreamException;
import com.shoekream.domain.address.Address;
import com.shoekream.domain.address.AddressRepository;
import com.shoekream.domain.address.dto.AddressAddRequest;
import com.shoekream.domain.user.User;
import com.shoekream.domain.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static com.shoekream.common.exception.ErrorCode.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private AddressRepository addressRepository;

    @Mock
    private User mockUser;
    @Mock
    private Address mockAddress;
    @InjectMocks
    private AddressService addressService;

    @Nested
    @DisplayName("주소 등록 테스트")
    class AddAddress {

        String email = "email";

        AddressAddRequest request = AddressAddRequest.builder()
                .addressName("addressName")
                .roadNameAddress("roadNameAddress")
                .detailedAddress("detailedAddress")
                .postalCode("postalCode")
                .build();

        @Test
        @DisplayName("주소 등록 성공")
        void success() {
            given(userRepository.findByEmail(email))
                    .willReturn(Optional.of(mockUser));
            given(addressRepository.save(any()))
                    .willReturn(mockAddress);

            assertDoesNotThrow(() -> addressService.addAddress(email, request));

            verify(userRepository, atLeastOnce()).findByEmail(email);
        }

        @Test
        @DisplayName("주소 등록 실패 (가입된 회원이 아닌 경우) ")
        void error() {
            when(userRepository.findByEmail(email))
                    .thenThrow(new ShoeKreamException(USER_NOT_FOUND));

            ShoeKreamException shoeKreamException = assertThrows(ShoeKreamException.class, () -> addressService.addAddress(email, request));

            assertThat(shoeKreamException.getErrorCode()).isEqualTo(USER_NOT_FOUND);

            verify(userRepository, atLeastOnce()).findByEmail(email);
        }
    }

    @Nested
    @DisplayName("주소 조회 테스트")
    class GetAddress {

        String email = "email";

        @Test
        @DisplayName("주소 조회 성공")
        void success() {
            given(userRepository.findByEmail(email))
                    .willReturn(Optional.of(mockUser));
            given(addressRepository.findAllByUser(mockUser))
                    .willReturn(List.of(mockAddress));

            assertDoesNotThrow(() -> addressService.getAddresses(email));

            verify(userRepository, atLeastOnce()).findByEmail(email);
            verify(addressRepository, atLeastOnce()).findAllByUser(mockUser);
        }

        @Test
        @DisplayName("주소 조회 실패 (가입된 회원이 아닌 경우)")
        void error() {
            when(userRepository.findByEmail(email))
                    .thenThrow(new ShoeKreamException(USER_NOT_FOUND));

            ShoeKreamException shoeKreamException = assertThrows(ShoeKreamException.class, () -> addressService.getAddresses(email));

            assertThat(shoeKreamException.getErrorCode()).isEqualTo(USER_NOT_FOUND);

            verify(userRepository, atLeastOnce()).findByEmail(email);
        }
    }

    @Nested
    @DisplayName("주소 삭제 테스트")
    class DeleteAddress {

        String email = "email";
        Long addressId = 1L;

        @Test
        @DisplayName("주소 삭제 성공")
        void success() {
            given(userRepository.findByEmail(email))
                    .willReturn(Optional.of(mockUser));
            given(addressRepository.findById(addressId))
                    .willReturn(Optional.of(mockAddress));

            assertDoesNotThrow(() -> addressService.deleteAddress(email,addressId));

            verify(userRepository, atLeastOnce()).findByEmail(email);
            verify(addressRepository, atLeastOnce()).findById(addressId);
        }

        @Test
        @DisplayName("주소 삭제 실패 (가입된 회원이 아닌 경우)")
        void error() {
            when(userRepository.findByEmail(email))
                    .thenThrow(new ShoeKreamException(USER_NOT_FOUND));

            ShoeKreamException shoeKreamException = assertThrows(ShoeKreamException.class, () -> addressService.deleteAddress(email,addressId));

            assertThat(shoeKreamException.getErrorCode()).isEqualTo(USER_NOT_FOUND);

            verify(userRepository, atLeastOnce()).findByEmail(email);
        }

        @Test
        @DisplayName("주소 삭제 실패 (주소를 찾을 수 없는 경우)")
        void error2() {
            given(userRepository.findByEmail(email))
                    .willReturn(Optional.of(mockUser));

            when(addressRepository.findById(addressId))
                    .thenThrow(new ShoeKreamException(ADDRESS_NOT_FOUND));

            ShoeKreamException shoeKreamException = assertThrows(ShoeKreamException.class, () -> addressService.deleteAddress(email,addressId));

            assertThat(shoeKreamException.getErrorCode()).isEqualTo(ADDRESS_NOT_FOUND);

            verify(userRepository, atLeastOnce()).findByEmail(email);
            verify(addressRepository, atLeastOnce()).findById(addressId);

        }

        @Test
        @DisplayName("주소 삭제 실패 (본인이 요청안한 경우)")
        void error3() {
            given(userRepository.findByEmail(email))
                    .willReturn(Optional.of(mockUser));
            given(addressRepository.findById(addressId))
                    .willReturn(Optional.of(mockAddress));

            doThrow(new ShoeKreamException(USER_NOT_MATCH))
                    .when(mockAddress).checkUser(mockUser);

            ShoeKreamException shoeKreamException = assertThrows(ShoeKreamException.class, () -> addressService.deleteAddress(email,addressId));

            assertThat(shoeKreamException.getErrorCode()).isEqualTo(USER_NOT_MATCH);

            verify(userRepository, atLeastOnce()).findByEmail(email);
            verify(addressRepository, atLeastOnce()).findById(addressId);

        }
    }
}