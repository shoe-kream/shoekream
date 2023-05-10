package com.shoekream.domain.trade.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ImmediatePurchaseResponse {

    private boolean eligible;
    private String rejectReason;
    private Long tradeId;
    private Long buyerId;

    public void setTradeId(Long buyerId, Long tradeId) {
        this.tradeId = tradeId;
        this.buyerId = buyerId;
    }

    public ImmediatePurchaseResponse(boolean eligible, String rejectReason) {
        this.eligible = eligible;
        this.rejectReason = rejectReason;
    }

    public static ImmediatePurchaseResponse of(boolean eligible, String rejectReason) {
        return new ImmediatePurchaseResponse(eligible, rejectReason);
    }
}