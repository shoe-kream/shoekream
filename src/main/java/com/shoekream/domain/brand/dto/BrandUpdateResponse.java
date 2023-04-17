package com.shoekream.domain.brand.dto;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BrandUpdateResponse {

    private String name;
    private String originImagePath;
    private String resizedImagePath;
}
