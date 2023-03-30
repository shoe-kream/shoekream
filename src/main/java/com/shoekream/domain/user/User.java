package com.shoekream.domain.user;

import com.shoekream.domain.cart.Cart;
import com.shoekream.domain.point.Point;
import com.shoekream.domain.user.dto.UserCreateResponse;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
}
