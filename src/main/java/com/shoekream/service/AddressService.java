package com.shoekream.service;

import com.shoekream.common.exception.ShoeKreamException;
import com.shoekream.domain.address.Address;
import com.shoekream.domain.address.AddressRepository;
import com.shoekream.domain.address.dto.AddressRequest;
import com.shoekream.domain.address.dto.AddressResponse;
import com.shoekream.domain.user.User;
import com.shoekream.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.shoekream.common.exception.ErrorCode.ADDRESS_NOT_FOUND;
import static com.shoekream.common.exception.ErrorCode.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional
public class AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    public AddressResponse addAddress(String email, AddressRequest request) {
        User foundUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new ShoeKreamException(USER_NOT_FOUND));

        Address address = addressRepository.save(request.toEntity(foundUser));

        return address.toAddressResponse();
    }

    @Transactional(readOnly = true)
    public List<AddressResponse> getAddresses(String email) {
        User foundUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new ShoeKreamException(USER_NOT_FOUND));

        return addressRepository.findAllByUser(foundUser)
                .stream()
                .map(Address::toAddressResponse)
                .collect(Collectors.toList());
    }

    public AddressResponse deleteAddress(String email, Long addressId) {
        User foundUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new ShoeKreamException(USER_NOT_FOUND));

        Address foundAddress = addressRepository.findById(addressId)
                .orElseThrow(() -> new ShoeKreamException(ADDRESS_NOT_FOUND));

        foundAddress.checkUser(foundUser);

        addressRepository.delete(foundAddress);

        return foundAddress.toAddressResponse();
    }

    public AddressResponse updateAddress(String email, Long addressId, AddressRequest request) {
        User foundUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new ShoeKreamException(USER_NOT_FOUND));

        Address foundAddress = addressRepository.findById(addressId)
                .orElseThrow(() -> new ShoeKreamException(ADDRESS_NOT_FOUND));

        foundAddress.checkUser(foundUser);

        foundAddress.update(request);

        return foundAddress.toAddressResponse();
    }
}
