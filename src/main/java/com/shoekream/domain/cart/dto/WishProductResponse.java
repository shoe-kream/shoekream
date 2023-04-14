package com.shoekream.domain.cart.dto;

import com.shoekream.domain.brand.dto.BrandInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WishProductResponse {

    private Long id;
    private Long productId;
    private String productName;
    private BrandInfo brandInfo;

}
