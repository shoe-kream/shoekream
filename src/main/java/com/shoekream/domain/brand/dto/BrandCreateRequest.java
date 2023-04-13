package com.shoekream.domain.brand.dto;

import com.shoekream.domain.brand.Brand;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BrandCreateRequest {

    @NotBlank(message = "브랜드 명을 입력하세요.")
    private String name;

    private String originImagePath;
    private String resizedImagePath;

    public Brand toEntity() {
        return Brand.builder()
                .name(this.name)
                .originImagePath(this.originImagePath)
                .resizedImagePath(this.resizedImagePath)
                .build();
    }

    public void setOriginImagePath(String originImageUrl, String resizedImageUrl) {
        this.originImagePath = originImageUrl;
        this.resizedImagePath = resizedImageUrl;
    }
}