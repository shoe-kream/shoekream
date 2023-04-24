package com.shoekream.domain.point;

import com.shoekream.domain.BaseTimeEntity;
import com.shoekream.domain.point.dto.PointHistoryResponse;
import com.shoekream.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
@AllArgsConstructor
public class Point extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    private User user;

    @Enumerated(EnumType.STRING)
    private PointDivision division;

    private Long amount;

    public static Point returnPurchasePoint(User buyer, Long price) {
        return Point.builder()
            .user(buyer)
            .division(PointDivision.PURCHASE_RETURN)
            .amount(price)
            .build();
    }

    public static Point receivePurchasePoint(User seller, Long price) {
        return Point.builder()
                .user(seller)
                .division(PointDivision.POINT_REVENUE)
                .amount(price)
                .build();
    }

    public PointHistoryResponse toPointHistoryResponse() {
        return PointHistoryResponse.builder()
                .time(this.getCreatedDate())
                .amount(this.amount)
                .build();
    }

    public static Point registerPointDeductionHistory(User user, Long price) {
        return Point.builder()
                .user(user)
                .amount(price)
                .division(PointDivision.PURCHASE_DEDUCTION)
                .build();
    }
}
