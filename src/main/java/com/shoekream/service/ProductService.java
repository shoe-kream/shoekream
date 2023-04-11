package com.shoekream.service;

import com.shoekream.common.exception.ErrorCode;
import com.shoekream.common.exception.ShoeKreamException;
import com.shoekream.common.util.AwsS3Service;
import com.shoekream.common.util.FileUtil;
import com.shoekream.domain.brand.Brand;
import com.shoekream.domain.brand.BrandRepository;
import com.shoekream.domain.product.Product;
import com.shoekream.domain.product.ProductRepository;
import com.shoekream.domain.product.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ProductService {

    private final BrandRepository brandRepository;
    private final ProductRepository productRepository;
    private final AwsS3Service awsS3Service;

    public ProductCreateResponse saveProduct(ProductCreateRequest requestDto, MultipartFile file) {

        Brand savedBrand = validateBrandExists(requestDto);

        isExistsProduct(requestDto.getName(), requestDto.getModelNumber());

        String originImageUrl = awsS3Service.uploadProductOriginImage(file);
        requestDto.setOriginImagePath(originImageUrl);

        Product savedProduct = productRepository.save(Product.createProduct(requestDto,savedBrand));

        return savedProduct.toProductCreateResponse();
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "products", key ="#id")
    public ProductInfo getProductInfo(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ShoeKreamException(ErrorCode.PRODUCT_NOT_FOUND))
                .toProductInfo();
    }

    @CacheEvict(value = "products", key = "#id")
    public ProductDeleteResponse deleteProduct(Long id) {

        Product product = validateProductExists(id);

        // 상품 이미지 전체 url 조회
        String originImagePath = product.getOriginImagePath();

        // 이미지 url에서 파일 이름만 추출
        String fileName = FileUtil.getFileName(originImagePath);

        // S3에서 상품 이미지 삭제
        awsS3Service.deleteProductImage(fileName);

        // 상품 삭제
        productRepository.delete(product);

        return product.toProductDeleteResponse();
    }

    //3. 기존 상품 이미지 유지 or 기존 상품 이미지 변경하는 경우
    //    → 기존 상품 이미지 삭제하고, 업데이트 될 이미지 url 초기화
    //4. 새로운 상품 이미지 등록
    //   4-1. 원본 이미지 저장(s3)
    //   4-2. 썸네일용, 리사이즈용 이미지 url로 변경하고 저장

    @CacheEvict(value = "products", key = "#id")
    public ProductUpdateResponse updateProduct(Long id, ProductUpdateRequest updatedProduct) {

        brandRepository.findById(updatedProduct.getBrandId())
                .orElseThrow(() -> new ShoeKreamException(ErrorCode.BRAND_NOT_FOUND));

        Product savedProduct = validateProductExists(id);

        checkDuplicatedUpdateProduct(updatedProduct, savedProduct);

        savedProduct.update(updatedProduct);

        return savedProduct.toProductUpdateResponse();
    }

    private Brand validateBrandExists(ProductCreateRequest requestDto) {
        return brandRepository.findById(requestDto.getBrandId())
                .orElseThrow(() -> new ShoeKreamException(ErrorCode.BRAND_NOT_FOUND));
    }

    private Product validateProductExists(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ShoeKreamException(ErrorCode.PRODUCT_NOT_FOUND));
    }

    private void checkDuplicatedUpdateProduct(ProductUpdateRequest updateProduct, Product savedProduct) {
        if(!updateProduct.getName().equals(savedProduct.getName()) && !updateProduct.getModelNumber().equals(savedProduct.getModelNumber())) {
            isExistsProduct(updateProduct.getName(), updateProduct.getModelNumber());
        }
    }

    private void isExistsProduct(String name, String modelNumber) {
        if(productRepository.existsByNameAndModelNumber(name, modelNumber)) {
            throw new ShoeKreamException(ErrorCode.DUPLICATED_PRODUCT);
        }
    }
}
