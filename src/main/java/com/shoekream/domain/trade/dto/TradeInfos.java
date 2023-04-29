package com.shoekream.domain.trade.dto;

import com.shoekream.domain.product.dto.ProductInfoFromTrade;
import com.shoekream.domain.user.dto.UserInfoForTrade;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class TradeInfos {

    private ProductInfoFromTrade productInfoFromTrade;
    private UserInfoForTrade userInfoForTrade;

    public static TradeInfos toTradeInfos(ProductInfoFromTrade productInfoFromTrade, UserInfoForTrade userInfoForTrade) {
        return TradeInfos.builder()
                .productInfoFromTrade(productInfoFromTrade)
                .userInfoForTrade(userInfoForTrade)
                .build();
    }
}