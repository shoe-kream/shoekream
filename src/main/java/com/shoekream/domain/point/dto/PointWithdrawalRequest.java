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
public class PointWithdrawalRequest {
    @Positive(message = "0원 이상 입력해주세요.")
    private Long withdrawalAmount;
    private String password;

    public Point toEntity(User user) {
        return Point.builder()
                .amount(this.withdrawalAmount)
                .division(PointDivision.POINT_WITHDRAW)
                .user(user)
                .build();
    }
}
