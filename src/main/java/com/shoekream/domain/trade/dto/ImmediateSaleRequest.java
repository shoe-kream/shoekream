package com.shoekream.domain.trade.dto;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class ImmediateSaleRequest {

    private Long tradeId;
    private Long productId;
    private Long addressId;
}
