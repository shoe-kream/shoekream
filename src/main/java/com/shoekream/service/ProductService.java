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

import static com.shoekream.common.util.constants.AwsConstants.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ProductService {

    private final BrandRepository brandRepository;
    private final ProductRepository productRepository;
    private final AwsS3Service awsS3Service;

    public ProductCreateResponse saveProduct(ProductCreateRequest requestDto, MultipartFile image) {

        Brand savedBrand = validateBrandExists(requestDto);

        isExistsProduct(requestDto.getName(), requestDto.getModelNumber());

        String originImageUrl = awsS3Service.uploadProductOriginImage(image);

        // 원본 이미지와 리사이징 이미지는 파일 이름만 같고, 버킷과 폴더는 다르기에 db에는 변경된 url을 넣어주어야 함
        String bucketChangedImageUrl = FileUtil.convertBucket(originImageUrl, RESIZED_BUCKET_NAME);
        String resizedImageUrl = FileUtil.convertFolder(bucketChangedImageUrl, ORIGIN_PRODUCT_FOLDER, RESIZED_PRODUCT_FOLDER);

        requestDto.setOriginImagePath(originImageUrl,resizedImageUrl);

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

        // 상품 이미지 관련 url 전부 조회
        String originImagePath = product.getOriginImagePath();
        String resizedImagePath = product.getResizedImagePath();

        // 이미지 url에서 파일 이름만 추출
        String originFileName = FileUtil.extractFileName(originImagePath);
        String resizedFileName = FileUtil.extractFileName(resizedImagePath);

        // S3에서 상품 관련 이미지 전부 삭제
        awsS3Service.deleteProductImage(originFileName, resizedFileName);

        // 상품 삭제
        productRepository.delete(product);

        return product.toProductDeleteResponse();
    }

    @CacheEvict(value = "products", key = "#id")
    public ProductUpdateResponse updateProduct(Long id, ProductUpdateRequest updatedProduct, MultipartFile newImage) {

        brandRepository.findById(updatedProduct.getBrandId())
                .orElseThrow(() -> new ShoeKreamException(ErrorCode.BRAND_NOT_FOUND));

        Product savedProduct = validateProductExists(id);

        checkDuplicatedUpdateProduct(updatedProduct, savedProduct);

        // 요청에 새로운 이미지가 포함된 경우
        if(newImage != null) {
            // 기존 이미지 전부 삭제
            String originImagePath = savedProduct.getOriginImagePath();
            String resizedImagePath = savedProduct.getResizedImagePath();
            String originFileName = FileUtil.extractFileName(originImagePath);
            String resizedFileName = FileUtil.extractFileName(resizedImagePath);
            awsS3Service.deleteProductImage(originFileName,resizedFileName);

            //새로운 원본 이미지, 리사이징 이미지 등록
            String newImageUrl = awsS3Service.uploadProductOriginImage(newImage);
            String bucketChangedImageUrl = FileUtil.convertBucket(newImageUrl, RESIZED_BUCKET_NAME);
            String newResizedImageUrl = FileUtil.convertFolder(bucketChangedImageUrl, ORIGIN_PRODUCT_FOLDER, RESIZED_PRODUCT_FOLDER);

            updatedProduct.setOriginImagePath(newImageUrl, newResizedImageUrl);
        } else { // 요청에 이미지 포함되지 않은 경우
            updatedProduct.setOriginImagePath(savedProduct.getOriginImagePath(), savedProduct.getResizedImagePath());
        }

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
