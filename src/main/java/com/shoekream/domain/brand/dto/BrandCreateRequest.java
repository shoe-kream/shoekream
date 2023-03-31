package com.shoekream.domain.brand.dto;

import com.shoekream.domain.brand.Brand;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BrandCreateRequest {

    @NotEmpty(message = "브랜드 명을 입력하세요.")
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