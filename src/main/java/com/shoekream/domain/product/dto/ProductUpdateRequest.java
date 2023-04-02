package com.shoekream.domain.product.dto;

import com.shoekream.domain.brand.dto.BrandInfo;
import com.shoekream.domain.product.Product;
import com.shoekream.domain.product.common.Currency;
import com.shoekream.domain.product.common.SizeClassification;
import com.shoekream.domain.product.common.SizeUnit;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ProductUpdateRequest {
    @NotBlank(message = "제품 명을 입력하세요.")
    private String name;

    @NotBlank(message = "모델 번호를 입력하세요.")
    private String modelNumber;

    private String color;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate releaseDate;

    @Positive(message = "올바른 출시가를 입력하세요.")
    private Long releasePrice;

    private Currency currency;

    private SizeClassification sizeClassification;

    private SizeUnit sizeUnit;

    @Positive(message = "올바른 최소 사이즈를 입력하세요.")
    private Double minSize;

    @Positive(message = "올바른 최대 사이즈를 입력하세요.")
    private Double maxSize;

    @Positive(message = "올바른 사이즈 간격을 입력해주세요.")
    private Double sizeGap;

    private BrandInfo brandInfo;

    private String originImagePath;

    private String thumbnailImagePath;

    private String resizedImagePath;

    public Product toEntity() {
        return Product.builder()
                .name(this.name)
                .modelNumber(this.modelNumber)
                .color(this.color)
                .releaseDate(this.releaseDate)
                .releasePrice(this.releasePrice)
                .currency(this.currency)
                .sizeClassification(this.sizeClassification)
                .sizeUnit(this.sizeUnit)
                .minSize(this.minSize)
                .maxSize(this.maxSize)
                .sizeGap(this.sizeGap)
                .brand(this.brandInfo.toEntity())
                .originImagePath(this.originImagePath)
                .thumbnailImagePath(this.thumbnailImagePath)
                .resizedImagePath(this.resizedImagePath)
                .build();
    }
}
