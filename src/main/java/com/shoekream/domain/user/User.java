package com.shoekream.domain.user;

import com.shoekream.common.exception.ErrorCode;
import com.shoekream.common.exception.ShoeKreamException;
import com.shoekream.common.util.JwtUtil;
import com.shoekream.domain.address.Address;
import com.shoekream.domain.cart.Cart;
import com.shoekream.domain.cart.CartProduct;
import com.shoekream.domain.cart.dto.WishProductResponse;
import com.shoekream.domain.point.Point;
import com.shoekream.domain.point.dto.PointResponse;
import com.shoekream.domain.product.Product;
import com.shoekream.domain.user.dto.*;
import com.shoekream.domain.user.dto.UserChangeNicknameRequest;
import com.shoekream.domain.user.dto.UserCreateResponse;
import com.shoekream.domain.user.dto.UserInfoForTrade;
import com.shoekream.domain.user.dto.UserResponse;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.shoekream.common.exception.ErrorCode.*;
import static com.shoekream.common.util.constants.JwtConstants.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class User extends UserBase {

    private String nickname;

    private String phone;

    @Embedded
    private Account account;

    private Long point;

    private LocalDateTime nicknameModifiedDate;

    @OneToMany(mappedBy = "user")
    private List<Point> pointBreakdown = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "CART_ID")
    private Cart cart;

    //    @JsonManagedReference
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Address> addressList = new ArrayList<>();

    @Builder
    public User(Long id, String email, String password, UserRole userRole, String nickname, String phone, Long point, LocalDateTime nicknameModifiedDate, Cart cart) {
        Assert.hasText(email, "email must not be empty");
        Assert.hasText(password, "password must not be empty");
        Assert.hasText(nickname, "nickName must not be empty");
        Assert.hasText(phone, "phone must not be empty");

        this.email = email;
        this.password = password;
        this.userRole = userRole;
        this.nickname = nickname;
        this.phone = phone;

        this.point = point;
        this.nicknameModifiedDate = nicknameModifiedDate;
    }

    public UserCreateResponse toCreateResponse() {
        return UserCreateResponse.builder()
                .email(this.email)
                .nickname(this.nickname)
                .build();
    }


    public void createCart(Cart cart) {
        this.cart = cart;
    }

    public void checkPassword(BCryptPasswordEncoder encoder, String inputPassword) {
        if (!encoder.matches(inputPassword, this.password)) {
            throw new ShoeKreamException(WRONG_PASSWORD);
        }
    }

    public String createToken(String secretKey) {
        return JwtUtil.createToken(this.email, this.userRole.toString(), secretKey, TOKEN_VALID_MILLIS);
    }

    public void changePassword(BCryptPasswordEncoder encoder, String newPassword) {
        this.password = encoder.encode(newPassword);
    }

    public UserResponse toUserResponse() {
        return UserResponse.builder()
                .userId(this.getId())
                .email(this.email)
                .build();
    }

    public void changeNickname(UserChangeNicknameRequest request) {
        if(!canChangeNickname()){
            throw new ShoeKreamException(CHANGE_NOT_ALLOWED);
        }
        this.nickname = request.getNickname();
        this.nicknameModifiedDate = LocalDateTime.now();
    }

    private boolean canChangeNickname() {
        return this.nicknameModifiedDate.isBefore(LocalDateTime.now().minusDays(7));
    }

    public boolean hasPoint() {
        return this.point > 0;
    }

    public void updateAccount(Account account) {
        this.account = account;
    }

    public void chargePoint(Long amount) {
        this.point += amount;
    }

    public void withdrawalPoint(Long withdrawalAmount) {
        if (this.point < withdrawalAmount) {
            throw new ShoeKreamException(NOT_ALLOWED_WITHDRAWAL_POINT);
        }
        this.point -= withdrawalAmount;
    }

    public PointResponse toPointResponse() {

        return PointResponse.builder()
                .remainingPoint(this.point)
                .build();
    }

    public void changeUserRole() {
        this.userRole = UserRole.ROLE_USER;
    }

    public Set<WishProductResponse> getWishList() {
        return this.cart.getWishList()
                .stream()
                .map(CartProduct::toWishProductResponse)
                .collect(Collectors.toSet());
    }

    public void checkWishProductDuplicate(Product product) {
        boolean hasWishProduct = this.cart.getWishList()
                .stream()
                .anyMatch(cartProduct -> cartProduct.getProduct().getId() == product.getId());

        if (hasWishProduct) {
            throw new ShoeKreamException(DUPLICATED_WISH_PRODUCT);
        }
    }

    public UserFindPasswordResponse toUserFindPasswordResponse(String tempPassword) {
        return UserFindPasswordResponse.builder()
                .email(email)
                .tempPassword(tempPassword)
                .build();
    }

    public UserCertificateResponse toUserCertificateAccountResponse(String certificationNumber) {
        return UserCertificateResponse.builder()
                .email(this.email)
                .certificationNumber(certificationNumber)
                .build();
    }


    public UserVerificationResponse toUserVerificationAccountResponse(String certificationNumber) {
        return UserVerificationResponse.builder()
                .email(this.email)
                .certificationNumber(certificationNumber)
                .build();
    }

    public UserInfoForTrade toUserInfoForTrade() {
        return UserInfoForTrade.builder()
                .addressList(this.addressList)
                .account(this.account)
                .build();
    }

    public void checkEnoughPoint(Long bidPrice) {
        if(bidPrice > this.point) {
            throw new ShoeKreamException(ErrorCode.NOT_ALLOWED_WITHDRAWAL_POINT);
        }
    }

    public void deductPoints(Long bidPrice) {
        this.point -= bidPrice;
    }

    public void checkPointForPurchase(Long bidPrice) {
        if(bidPrice > this.point) {
            throw new ShoeKreamException(ErrorCode.NOT_ALLOWED_WITHDRAWAL_POINT);
        }
    }

    public void returnPoint(Long price) {
        this.point += price;
    }
}
