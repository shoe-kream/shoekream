package com.shoekream.service;

import com.shoekream.common.exception.ShoeKreamException;
import com.shoekream.domain.address.Address;
import com.shoekream.domain.address.AddressRepository;
import com.shoekream.domain.address.dto.AddressAddRequest;
import com.shoekream.domain.address.dto.AddressResponse;
import com.shoekream.domain.user.User;
import com.shoekream.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.shoekream.common.exception.ErrorCode.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    public AddressResponse addAddress(String email, AddressAddRequest request) {
        User foundUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new ShoeKreamException(USER_NOT_FOUND));

        Address address = addressRepository.save(request.toEntity(foundUser));

        return address.toAddressResponse();
    }
}
