package com.shoekream.domain.trade.dto;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class TradeBidInfos {

    private Long tradeId;
    private Long productId;
    private Double productSize;
    private Long price;
}