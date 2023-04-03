package com.shoekream.service;

import com.shoekream.common.exception.ErrorCode;
import com.shoekream.common.exception.ShoeKreamException;
import com.shoekream.domain.brand.Brand;
import com.shoekream.domain.brand.BrandRepository;
import com.shoekream.domain.product.Product;
import com.shoekream.domain.product.ProductRepository;
import com.shoekream.domain.product.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ProductService {

    private final BrandRepository brandRepository;
    private final ProductRepository productRepository;

    public ProductCreateResponse saveProduct(ProductCreateRequest requestDto) {

        Brand savedBrand = validateBrandExists(requestDto);

        isExistsProduct(requestDto.getName(), requestDto.getModelNumber());

        Product savedProduct = productRepository.save(Product.createProduct(requestDto,savedBrand));

        return savedProduct.toProductCreateResponse();
    }

    private Brand validateBrandExists(ProductCreateRequest requestDto) {
        return  brandRepository.findById(requestDto.getBrandId())
                .orElseThrow(() -> new ShoeKreamException(ErrorCode.BRAND_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public ProductInfo getProductInfo(Long id) {

        return productRepository.findById(id)
                .orElseThrow(() -> new ShoeKreamException(ErrorCode.PRODUCT_NOT_FOUND))
                .toProductInfo();
    }

    public ProductDeleteResponse deleteProduct(Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ShoeKreamException(ErrorCode.PRODUCT_NOT_FOUND));

        productRepository.delete(product);

        return product.toProductDeleteResponse();
    }

    public ProductUpdateResponse updateProduct(Long id, ProductUpdateRequest updatedProduct) {

        Product savedProduct = productRepository.findById(id)
                .orElseThrow(() -> new ShoeKreamException(ErrorCode.PRODUCT_NOT_FOUND));

        checkDuplicatedUpdateProduct(updatedProduct, savedProduct);

        savedProduct.update(updatedProduct);

        return savedProduct.toProductUpdateResponse();
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
