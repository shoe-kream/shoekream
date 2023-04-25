package com.shoekream.domain.product.dto;

import com.shoekream.domain.brand.dto.BrandInfo;
import com.shoekream.domain.product.common.Currency;
import com.shoekream.domain.product.common.SizeClassification;
import com.shoekream.domain.product.common.SizeUnit;
import com.shoekream.domain.trade.dto.TradeBidInfos;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class ProductInfo {

    private Long id;
    private String name;
    private String modelNumber;
    private String color;
    private LocalDate releaseDate;
    private Long releasePrice;
    private Currency currency;
    private SizeClassification sizeClassification;
    private SizeUnit sizeUnit;
    private Double minSize;
    private Double maxSize;
    private Double sizeGap;
    private BrandInfo brandInfo;
    private String originImagePath;
    private String resizedImagePath;
    private List<TradeBidInfos> salesBids = new ArrayList<>();
    private List<TradeBidInfos> purchaseBids = new ArrayList<>();
}
