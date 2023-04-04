package com.shoekream.domain.product.dto;

import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ProductDeleteResponse {

    private String name;
    private String modelNumber;
    private String brandName;
}
