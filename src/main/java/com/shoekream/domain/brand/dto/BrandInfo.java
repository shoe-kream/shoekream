package com.shoekream.domain.brand.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BrandInfo {

    private Long id;
    private String name;
    private String originImagePath;
    private String thumbnailImagePath;
}
