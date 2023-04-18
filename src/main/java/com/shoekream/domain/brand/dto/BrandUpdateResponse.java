package com.shoekream.domain.brand.dto;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class BrandUpdateResponse {

    private String name;
    private String originImagePath;
    private String resizedImagePath;
}
