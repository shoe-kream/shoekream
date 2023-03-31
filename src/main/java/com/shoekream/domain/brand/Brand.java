package com.shoekream.domain.brand;

import com.shoekream.domain.BaseTimeEntity;
import com.shoekream.domain.brand.dto.*;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Brand extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    private String originImagePath;

    private String thumbnailImagePath;

    public BrandInfo toBrandInfo() {
        return BrandInfo.builder()
                .id(this.id)
                .name(this.name)
                .originImagePath(this.originImagePath)
                .thumbnailImagePath(this.thumbnailImagePath)
                .build();
    }

    public BrandCreateResponse toBrandCreateResponse() {
        return BrandCreateResponse.builder()
                .name(this.name)
                .build();
    }

    public BrandDeleteResponse toBrandDeleteResponse() {
        return BrandDeleteResponse.builder()
                .name(this.name)
                .build();
    }

    public BrandUpdateResponse toBrandUpdateResponse() {
        return BrandUpdateResponse.builder()
                .name(this.name)
                .originImagePath(this.originImagePath)
                .thumbnailImagePath(this.thumbnailImagePath)
                .build();
    }

    public void update(BrandUpdateRequest updatedBrand) {
        this.name = updatedBrand.getName();
        this.originImagePath = updatedBrand.getOriginImagePath();
        this.thumbnailImagePath = updatedBrand.getThumbnailImagePath();
    }
}
