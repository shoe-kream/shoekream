package com.shoekream.domain.brand.dto;


import com.shoekream.domain.brand.Brand;
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

    public Brand toEntity() {
        return Brand.builder()
                .name(this.name)
                .originImagePath(this.originImagePath)
                .thumbnailImagePath(this.thumbnailImagePath)
                .build();
    }
}
