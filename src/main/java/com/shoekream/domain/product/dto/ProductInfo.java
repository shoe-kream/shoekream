package com.shoekream.domain.product.dto;

import com.shoekream.domain.brand.dto.BrandInfo;
import com.shoekream.domain.product.common.Currency;
import com.shoekream.domain.product.common.SizeClassification;
import com.shoekream.domain.product.common.SizeUnit;
import lombok.*;

import java.time.LocalDate;

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
    private String thumbnailImagePath;
    private String resizedImagePath;
}
