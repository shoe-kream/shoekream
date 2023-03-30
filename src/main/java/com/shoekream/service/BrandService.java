package com.shoekream.service;

import com.shoekream.common.exception.ErrorCode;
import com.shoekream.common.exception.ShoeKreamException;
import com.shoekream.domain.brand.Brand;
import com.shoekream.domain.brand.BrandRepository;
import com.shoekream.domain.brand.dto.BrandCreateRequest;
import com.shoekream.domain.brand.dto.BrandCreateResponse;
import com.shoekream.domain.brand.dto.BrandDeleteResponse;
import com.shoekream.domain.brand.dto.BrandInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class BrandService {

    private final BrandRepository brandRepository;

    public BrandInfo getBrandInfo(Long id) {
        return brandRepository.findById(id)
                .orElseThrow(() -> new ShoeKreamException(ErrorCode.BRAND_NOT_FOUND))
                .toBrandInfo();
    }

    public List<BrandInfo> getBrandInfos() {
        return brandRepository.findAll().stream()
                .map(Brand::toBrandInfo)
                .toList();
    }

    public BrandCreateResponse saveBrand(BrandCreateRequest requestDto) {

        checkDuplicatedBrandName(requestDto);

        Brand savedBrand = brandRepository.save(requestDto.toEntity());

        return savedBrand.toBrandCreateResponse();
    }

    private void checkDuplicatedBrandName(BrandCreateRequest requestDto) {
        if(brandRepository.existsByName(requestDto.getName())) {
            throw new ShoeKreamException(ErrorCode.DUPLICATED_BRAND);
        }
    }

    public BrandDeleteResponse deleteBrand(Long id) {

        Brand brand = brandRepository.findById(id).orElseThrow(() -> new ShoeKreamException(ErrorCode.BRAND_NOT_FOUND));

        brandRepository.deleteById(id);

        return brand.toBrandDeleteResponse();
    }
}
