package com.shoekream.domain.brand.dto;


import com.shoekream.domain.brand.Brand;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class BrandInfo {

    private Long id;
    private String name;
    private String originImagePath;
    private String resizedImagePath;

    public Brand toEntity() {
        return Brand.builder()
                .id(this.id)
                .name(this.name)
                .originImagePath(this.originImagePath)
                .resizedImagePath(this.resizedImagePath)
                .build();
    }
}
