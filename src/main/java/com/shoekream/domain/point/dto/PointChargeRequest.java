package com.shoekream.domain.point.dto;

import com.shoekream.domain.point.Point;
import com.shoekream.domain.point.PointDivision;
import com.shoekream.domain.user.User;
import jakarta.validation.constraints.Positive;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@EqualsAndHashCode
public class PointChargeRequest {
    @Positive(message = "0원 이상 입력해주세요.")
    private Long amount;

    public Point toEntity(User user) {
        return Point.builder()
                .amount(this.amount)
                .division(PointDivision.POINT_CHARGE)
                .user(user)
                .build();
    }
}
