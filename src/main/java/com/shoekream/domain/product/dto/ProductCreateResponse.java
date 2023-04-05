package com.shoekream.domain.product.dto;

import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ProductCreateResponse {

    private String name;
    private String modelNumber;
    private Double minSize;
    private Double maxSize;
    private String brandName;
}
