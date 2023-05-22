package com.shoekream.domain.trade.dto;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class ReceiveResponse {

    private Long buyerId;
    private String trackingNumber;
}
