package com.shoekream.domain.brand.dto;


import com.shoekream.domain.brand.Brand;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BrandCreateResponse {

    private Long id;
    private String name;
    private String originImagePath;
    private String thumbnailImagePath;
}
