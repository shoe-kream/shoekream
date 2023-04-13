package com.shoekream.domain.cart.dto;

import com.shoekream.domain.brand.Brand;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WishProductResponse {

    private Long id;
    private Long productId;
    private String productName;
    private Brand brand;

}
