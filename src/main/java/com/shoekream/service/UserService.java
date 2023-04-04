package com.shoekream.service;


import com.shoekream.common.exception.ErrorCode;
import com.shoekream.common.exception.ShoeKreamException;
import com.shoekream.domain.cart.Cart;
import com.shoekream.domain.cart.CartRepository;
import com.shoekream.domain.user.User;
import com.shoekream.domain.user.UserRepository;
import com.shoekream.domain.user.dto.UserCreateRequest;
import com.shoekream.domain.user.dto.UserCreateResponse;
import com.shoekream.domain.user.dto.UserLoginRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    @Value("${jwt.secret}")
    private String secretKey;

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final BCryptPasswordEncoder encoder;

    @Transactional
    public UserCreateResponse createUser(UserCreateRequest request){

        if (isExistsByEmail(request)) {
            throw new ShoeKreamException(ErrorCode.DUPLICATE_EMAIL);
        }
        if (isExistsByNickname(request)) {
            throw new ShoeKreamException(ErrorCode.DUPLICATE_NICKNAME);
        }

        request.encodePassword(encoder);

        User savedUser = userRepository.save(request.toEntity());
        savedUser.createCart(cartRepository.save(new Cart()));

        return savedUser.toCreateResponse();
    }

    private boolean isExistsByNickname(UserCreateRequest request) {
        return userRepository.existsByNickname(request.getNickname());
    }

    private boolean isExistsByEmail(UserCreateRequest request) {
        return userRepository.existsByEmail(request.getEmail());
    }

    public String loginUser(UserLoginRequest request) {

        User foundUser = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ShoeKreamException(ErrorCode.USER_NOT_FOUND));

        foundUser.checkPassword(encoder,request.getPassword());

        return foundUser.createToken(secretKey);
    }
}
