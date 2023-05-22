package com.shoekream.domain.trade.dto;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class TradeDeleteResponse {

    private Long productId;
    private String productName;
    private Double productSize;
    private Long price;
}
