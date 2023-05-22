package com.shoekream.domain.trade.dto;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class ReturnResponse {

    private Long sellerId;
    private String trackingNumber;
}
