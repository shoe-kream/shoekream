package com.shoekream.domain.trade;

import com.shoekream.domain.trade.dto.ImmediatePurchaseResponse;
import org.springframework.stereotype.Component;

@Component
public class TradeValidator {

    public ImmediatePurchaseResponse purchaseValidate(Trade trade) {
        if(!trade.getStatus().equals(TradeStatus.PRE_OFFER) || trade.getBuyer() != null) {
            return ImmediatePurchaseResponse.of(false, "이미 낙찰된 거래입니다.");
        }

        return ImmediatePurchaseResponse.of(true, null);
    }
}

