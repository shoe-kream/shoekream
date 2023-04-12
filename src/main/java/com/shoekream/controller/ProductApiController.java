package com.shoekream.controller;

import com.shoekream.common.Response;
import com.shoekream.domain.product.dto.*;
import com.shoekream.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Slf4j
public class ProductApiController {

    private final ProductService productService;

    @PostMapping("")
    public ResponseEntity<Response<ProductCreateResponse>> createProduct(@Validated @RequestPart ProductCreateRequest requestDto,
                                                                         @RequestPart MultipartFile multipartFile,
                                                                         BindingResult br) {
        return ResponseEntity.status(HttpStatus.CREATED).body(Response.success(productService.saveProduct(requestDto, multipartFile)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response<ProductInfo>> getProductInfo(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(Response.success(productService.getProductInfo(id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Response<ProductDeleteResponse>> deleteProduct(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(Response.success(productService.deleteProduct(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Response<ProductUpdateResponse>> updateProduct(@PathVariable Long id,
                                                                         @Validated @RequestPart ProductUpdateRequest requestDto,
                                                                         @RequestPart(required = false) MultipartFile multipartFile,
                                                                         BindingResult br) {
        return ResponseEntity.status(HttpStatus.OK).body(Response.success(productService.updateProduct(id, requestDto, multipartFile)));
    }
}

