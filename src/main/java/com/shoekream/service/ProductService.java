package com.shoekream.service;

import com.shoekream.common.exception.ErrorCode;
import com.shoekream.common.exception.ShoeKreamException;
import com.shoekream.domain.product.Product;
import com.shoekream.domain.product.ProductRepository;
import com.shoekream.domain.product.dto.ProductCreateRequest;
import com.shoekream.domain.product.dto.ProductCreateResponse;
import com.shoekream.domain.product.dto.ProductInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;

    public ProductCreateResponse saveProduct(ProductCreateRequest requestDto) {

        if(productRepository.existsByNameAndModelNumber(requestDto.getName(), requestDto.getModelNumber())) {
            throw new ShoeKreamException(ErrorCode.DUPLICATED_PRODUCT);
        }

        Product savedProduct = productRepository.save(requestDto.toEntity());

        return savedProduct.toProductCreateResponse();
    }

    @Transactional(readOnly = true)
    public ProductInfo getProductInfo(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ShoeKreamException(ErrorCode.PRODUCT_NOT_FOUND))
                .toProductInfo();
    }
}
