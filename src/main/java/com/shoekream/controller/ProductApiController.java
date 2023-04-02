package com.shoekream.controller;

import com.shoekream.common.Response;
import com.shoekream.domain.product.dto.ProductCreateRequest;
import com.shoekream.domain.product.dto.ProductCreateResponse;
import com.shoekream.service.BrandService;
import com.shoekream.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Slf4j
public class ProductApiController {

    private final ProductService productService;

    private final BrandService brandService;

    @PostMapping("")
    public ResponseEntity<Response<ProductCreateResponse>> createProduct(@Valid @RequestBody ProductCreateRequest requestDto) {
        brandService.checkProductBrandExists(requestDto.getBrandInfo());
        return ResponseEntity.status(HttpStatus.CREATED).body(Response.success(productService.saveProduct(requestDto)));
    }

}
