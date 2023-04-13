package com.shoekream.service;


import com.shoekream.common.exception.ShoeKreamException;
import com.shoekream.domain.cart.CartRepository;
import com.shoekream.domain.cart.dto.WishProductResponse;
import com.shoekream.domain.user.User;
import com.shoekream.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static com.shoekream.common.exception.ErrorCode.USER_NOT_FOUND;

@Transactional
@Service
@RequiredArgsConstructor
public class CartService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Set<WishProductResponse> getWishList(String email) {
        User foundUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new ShoeKreamException(USER_NOT_FOUND));

        return foundUser.getWishList();
    }
}
