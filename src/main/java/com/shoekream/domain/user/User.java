package com.shoekream.domain.user;

import com.shoekream.domain.cart.Cart;
import com.shoekream.domain.point.Point;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class User extends UserBase{

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
}
