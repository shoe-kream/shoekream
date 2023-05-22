package com.shoekream.domain.product.dto;

import com.shoekream.domain.trade.dto.TradeBidInfos;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class ProductInfoFromTrade {

    private Long id;
    private String name;
    private String modelNumber;
    private String color;
    private String brandName;
    private TradeBidInfos immediateSale;        // 즉시 판매가
    private TradeBidInfos immediatePurchase;    // 즉시 구매가
}