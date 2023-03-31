package com.shoekream.domain.brand.dto;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class BrandDeleteResponse {

    private String name;
}
