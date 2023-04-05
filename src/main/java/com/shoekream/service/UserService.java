package com.shoekream.service;


import com.shoekream.common.exception.ShoeKreamException;
import com.shoekream.domain.cart.Cart;
import com.shoekream.domain.cart.CartRepository;
import com.shoekream.domain.user.User;
import com.shoekream.domain.user.UserRepository;
import com.shoekream.domain.user.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.shoekream.common.exception.ErrorCode.*;

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
            throw new ShoeKreamException(DUPLICATE_EMAIL);
        }
        if (isExistsByNickname(request.getNickname())) {
            throw new ShoeKreamException(DUPLICATE_NICKNAME);
        }

        request.encodePassword(encoder);

        User savedUser = userRepository.save(request.toEntity());
        savedUser.createCart(cartRepository.save(new Cart()));

        return savedUser.toCreateResponse();
    }

    private boolean isExistsByNickname(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    private boolean isExistsByEmail(UserCreateRequest request) {
        return userRepository.existsByEmail(request.getEmail());
    }

    public String loginUser(UserLoginRequest request) {

        User foundUser = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ShoeKreamException(USER_NOT_FOUND));

        foundUser.checkPassword(encoder,request.getPassword());

        return foundUser.createToken(secretKey);
    }

    @Transactional
    public UserResponse changePasswordUser(UserChangePasswordRequest request, String email) {
        User foundUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new ShoeKreamException(USER_NOT_FOUND));

        foundUser.checkPassword(encoder, request.getOldPassword());

        foundUser.changePassword(encoder, request.getNewPassword());

        return foundUser.toUserResponse();
    }

    @Transactional
    public UserResponse changeNicknameUser(UserChangeNicknameRequest request, String email) {
        User foundUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new ShoeKreamException(USER_NOT_FOUND));

        if (isExistsByNickname(request.getNickname())) {
            throw new ShoeKreamException(DUPLICATE_NICKNAME);
        }

        foundUser.changeNickname(request);

        return foundUser.toUserResponse();
    }

    public UserResponse withdrawUser(UserWithdrawRequest request,String email) {
        User foundUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new ShoeKreamException(USER_NOT_FOUND));

        foundUser.checkPassword(encoder, request.getPassword());

        userRepository.delete(foundUser);

        return foundUser.toUserResponse();
    }
}
