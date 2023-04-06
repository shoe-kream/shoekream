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
        @DisplayName("주소 등록 실패")
        void error() {
            when(userRepository.findByEmail(email))
                    .thenThrow(new ShoeKreamException(USER_NOT_FOUND));

            ShoeKreamException shoeKreamException = assertThrows(ShoeKreamException.class, () -> addressService.addAddress(email, request));

            assertThat(shoeKreamException.getErrorCode()).isEqualTo(USER_NOT_FOUND);

            verify(userRepository, atLeastOnce()).findByEmail(email);
        }
    }
}