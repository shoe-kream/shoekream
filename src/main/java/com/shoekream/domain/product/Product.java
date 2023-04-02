package com.shoekream.domain.product;

import com.shoekream.domain.BaseTimeEntity;
import com.shoekream.domain.brand.Brand;
import com.shoekream.domain.product.common.Currency;
import com.shoekream.domain.product.common.SizeClassification;
import com.shoekream.domain.product.common.SizeUnit;
import com.shoekream.domain.product.dto.ProductCreateResponse;
import com.shoekream.domain.product.dto.ProductInfo;
import com.shoekream.domain.trade.Trade;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Product extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true)
    private String modelNumber;

    private String color;

    private LocalDate releaseDate;

    private Long releasePrice;

    @Enumerated(EnumType.STRING)
    private Currency currency;

    @Enumerated(EnumType.STRING)
    private SizeClassification sizeClassification;

    @Enumerated(EnumType.STRING)
    private SizeUnit sizeUnit;

    private Double minSize;

    private Double maxSize;

    private Double sizeGap;

    private String originImagePath;

    private String thumbnailImagePath;
    private String resizedImagePath;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BRAND_ID")
    private Brand brand;

    @OneToMany(mappedBy = "product", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Trade> trades = new ArrayList<>();

    public ProductCreateResponse toProductCreateResponse() {
        return ProductCreateResponse.builder()
                .name(this.name)
                .modelNumber(this.modelNumber)
                .minSize(this.minSize)
                .maxSize(this.maxSize)
                .brandName(this.brand.getName())
                .build();
    }

    public ProductInfo toProductInfo() {
        return ProductInfo.builder()
                .id(this.id)
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
                .brandInfo(this.brand.toBrandInfo())
                .originImagePath(this.originImagePath)
                .thumbnailImagePath(this.thumbnailImagePath)
                .resizedImagePath(this.resizedImagePath)
                .build();
    }
}
