package com.shoekream.service;

import com.shoekream.common.exception.ErrorCode;
import com.shoekream.common.exception.ShoeKreamException;
import com.shoekream.domain.brand.Brand;
import com.shoekream.domain.brand.BrandRepository;
import com.shoekream.domain.brand.dto.*;
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

    // 브랜드 명 중복 확인
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

    public BrandUpdateResponse updateBrand(Long id, BrandUpdateRequest updatedBrand) {

        Brand savedBrand = brandRepository.findById(id).orElseThrow(() -> new ShoeKreamException(ErrorCode.BRAND_NOT_FOUND));

        checkDuplicatedUpdateBrandName(savedBrand,updatedBrand);

        savedBrand.update(updatedBrand);

        return savedBrand.toBrandUpdateResponse();
    }

    // 브랜드 수정 시 중복 확인 - 기존 브랜드 명 != 수정할 브랜드 명 일 경우 수정할 브랜드 명이 없어야 함
    private void checkDuplicatedUpdateBrandName(Brand savedBrand, BrandUpdateRequest updatedBrand) {
        if (!savedBrand.getName().equals(updatedBrand.getName()) && isExistBrand(updatedBrand) ) {
            throw new ShoeKreamException(ErrorCode.DUPLICATED_BRAND);
        }
    }
    private boolean isExistBrand(BrandUpdateRequest updatedBrand) {
        if (!brandRepository.existsByName(updatedBrand.getName())) {
            return false;
        }
        return true;
    }

}
