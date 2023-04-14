package com.shoekream.service;

import com.shoekream.common.exception.ErrorCode;
import com.shoekream.common.exception.ShoeKreamException;
import com.shoekream.common.util.AwsS3Service;
import com.shoekream.common.util.FileUtil;
import com.shoekream.domain.brand.Brand;
import com.shoekream.domain.brand.BrandRepository;
import com.shoekream.domain.brand.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.shoekream.common.util.constants.AwsConstants.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class BrandService {

    private final BrandRepository brandRepository;
    private final AwsS3Service awsS3Service;

    public BrandInfo getBrandInfo(Long id) {
        return brandRepository.findById(id)
                .orElseThrow(() -> new ShoeKreamException(ErrorCode.BRAND_NOT_FOUND))
                .toBrandInfo();
    }

    @Cacheable(value = "brands")
    public List<BrandInfo> getBrandInfos() {
        return brandRepository.findAll().stream()
                .map(Brand::toBrandInfo)
                .toList();
    }

    public BrandCreateResponse saveBrand(BrandCreateRequest requestDto, MultipartFile file) {

        checkDuplicatedBrandName(requestDto);

        String originImageUrl = awsS3Service.uploadBrandOriginImage(file);

        // 원본 이미지와 리사이징 이미지는 파일 이름만 같고, 버킷과 폴더는 다르기에 db에는 변경된 url을 넣어주어야 함
        String bucketChangedImageUrl = FileUtil.convertBucket(originImageUrl, RESIZED_BUCKET_NAME);
        String resizedImageUrl = FileUtil.convertFolder(bucketChangedImageUrl, ORIGIN_BRAND_FOLDER, RESIZED_BRAND_FOLDER);

        requestDto.setOriginImagePath(originImageUrl, resizedImageUrl);

        Brand savedBrand = brandRepository.save(requestDto.toEntity());

        return savedBrand.toBrandCreateResponse();
    }

    // 브랜드 명 중복 확인
    private void checkDuplicatedBrandName(BrandCreateRequest requestDto) {
        if(brandRepository.existsByName(requestDto.getName())) {
            throw new ShoeKreamException(ErrorCode.DUPLICATED_BRAND);
        }
    }

    @CacheEvict(value = "brands", allEntries = true)
    public BrandDeleteResponse deleteBrand(Long id) {

        Brand brand = brandRepository.findById(id).orElseThrow(() -> new ShoeKreamException(ErrorCode.BRAND_NOT_FOUND));

        // 이미지 url에서 파일 이름만 추출
        String originFileName = FileUtil.extractFileName(brand.getOriginImagePath());
        String resizedFileName = FileUtil.extractFileName(brand.getResizedImagePath());

        // S3에서 브랜드 이미지 삭제
        awsS3Service.deleteBrandImage(originFileName, resizedFileName);

        // 브랜드 삭제
        brandRepository.deleteById(id);

        return brand.toBrandDeleteResponse();
    }

    @CacheEvict(value = "brands", allEntries = true)
    public BrandUpdateResponse updateBrand(Long id, BrandUpdateRequest updatedBrand, MultipartFile newImage) {

        Brand savedBrand = brandRepository.findById(id).orElseThrow(() -> new ShoeKreamException(ErrorCode.BRAND_NOT_FOUND));

        checkDuplicatedUpdateBrandName(savedBrand,updatedBrand);

        // 요청에 새로운 이미지가 포함된 경우
        if(newImage != null) {
            // 기존 이미지 전부 삭제
            String originFileName = FileUtil.extractFileName(savedBrand.getOriginImagePath());
            String resizedFileName = FileUtil.extractFileName(savedBrand.getResizedImagePath());
            awsS3Service.deleteBrandImage(originFileName, resizedFileName);

            //새로운 원본 이미지, 리사이징 이미지 등록
            String newImageUrl = awsS3Service.uploadBrandOriginImage(newImage);
            String bucketChangedImageUrl = FileUtil.convertBucket(newImageUrl, RESIZED_BUCKET_NAME);
            String newResizedImageUrl = FileUtil.convertFolder(bucketChangedImageUrl, ORIGIN_BRAND_FOLDER, RESIZED_BRAND_FOLDER);

            updatedBrand.setOriginImagePath(newImageUrl, newResizedImageUrl);
        } else { // 요청에 이미지 포함되지 않은 경우
            updatedBrand.setOriginImagePath(savedBrand.getOriginImagePath(), savedBrand.getResizedImagePath());
        }

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

    public void checkProductBrandExists(BrandInfo brand) {
        brandRepository.findById(brand.getId())
                .orElseThrow(() -> new ShoeKreamException(ErrorCode.BRAND_NOT_FOUND));
    }

}
