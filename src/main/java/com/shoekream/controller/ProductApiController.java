package com.shoekream.controller;

import com.shoekream.common.Response;
import com.shoekream.domain.product.dto.*;
import com.shoekream.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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

    @Tag(name = "Product", description = "상품 정보 관련 API")
    @Operation(summary = "상품 정보 등록", description = "JWT 토큰 필요(Authorization Header에 추가) | ADMIN 등급 계정만 가능 | 가입된 회원이 존재하지 않을 시 · 등록된 브랜드가 존재하지 않을 시 · 이미 같은 모델명으로 등록된 상품이 존재할 시 에러 발생")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"SUCCESS\",\"result\":{\"name\":\"name\",\"modelNumber\":\"modelNumber\",\"minSize\":200,\"maxSize\":300,\"brandName\":\"brandName\",\"originImagePath\":\"originImagePath\",\"resizedImagePath\":\"resizedImagePath\"}}")}, schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "ERROR (가입된 회원이 존재하지 않을 시 · 등록된 브랜드가 존재하지 않을 시)", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"ERROR\",\"result\":\"에러 메세지\"}")}, schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "409", description = "ERROR (이미 같은 모델명으로 등록된 상품이 존재할 시)", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"ERROR\",\"result\":\"에러 메세지\"}")}, schema = @Schema(implementation = Response.class)))

    })
    @PostMapping("")
    public ResponseEntity<Response<ProductCreateResponse>> createProduct(@Validated @RequestPart ProductCreateRequest requestDto,
                                                                         @RequestPart MultipartFile multipartFile,
                                                                         BindingResult br,
                                                                         Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED).body(Response.success(productService.saveProduct(requestDto, multipartFile)));
    }

    @Tag(name = "Product", description = "상품 정보 관련 API")
    @Operation(summary = "상품 정보 및 해당 상품 구매 입찰 · 판매 입찰 내역 조회", description = "가입된 회원이 존재하지 않을 시 · 등록된 상품이 존재하지 않을 시 에러 발생")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"SUCCESS\",\"result\":{\"id\":1,\"name\":\"name\",\"modelNumber\":\"modelNumber\",\"color\":\"color\",\"releaseDate\":\"releaseDate\",\"releasePrice\":\"releasePrice\",\"currency\":\"currency\"," +
                            "\"sizeClassification\":\"sizeClassification\",\"sizeUnit\":\"sizeUnit\",\"minSize\":200,\"maxSize\":300,\"sizeGap\":5," +
                            "\"brandInfo\":{\"id\":1,\"name\":\"name\",\"originImagePath\":\"originImagePath\",\"resizedImagePath\":\"resizedImagePath\"}," +
                            "\"originImagePath\":\"originImagePath\",\"resizedImagePath\":\"resizedImagePath\"," +
                            "\"salesBids\":[{\"tradeId\":1,\"productId\":1,\"productSize\":260,\"price\":200000}]," +
                            "\"purchaseBids\":[{\"tradeId\":2,\"productId\":1,\"productSize\":260,\"price\":250000}]}}")}, schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "ERROR (가입된 회원이 존재하지 않을 시 · 등록된 상품이 존재하지 않을 시)", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"ERROR\",\"result\":\"에러 메세지\"}")}, schema = @Schema(implementation = Response.class)))

    })
    @GetMapping("/{id}")
    public ResponseEntity<Response<ProductInfo>> getProductInfo(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(Response.success(productService.getProductInfo(id)));
    }

    @Tag(name = "Product", description = "상품 정보 관련 API")
    @Operation(summary = "상품 정보 삭제", description = "JWT 토큰 필요(Authorization Header에 추가) | ADMIN 등급 계정만 가능 | 가입된 회원이 존재하지 않을 시 · 등록된 상품이 존재하지 않을 시 에러 발생")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"SUCCESS\",\"result\":{\"name\":\"name\",\"modelNumber\":\"modelNumber\",\"brandName\":\"brandName\"}}")}, schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "ERROR (가입된 회원이 존재하지 않을 시 · 등록된 상품이 존재하지 않을 시)", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"ERROR\",\"result\":\"에러 메세지\"}")}, schema = @Schema(implementation = Response.class))),
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Response<ProductDeleteResponse>> deleteProduct(@PathVariable Long id, Authentication authentication) {
        return ResponseEntity.status(HttpStatus.OK).body(Response.success(productService.deleteProduct(id)));
    }

    @Tag(name = "Product", description = "상품 정보 관련 API")
    @Operation(summary = "상품 정보 수정", description = "JWT 토큰 필요(Authorization Header에 추가) | ADMIN 등급 계정만 가능 | 가입된 회원이 존재하지 않을 시 · 등록된 브랜드가 존재하지 않을 시 · 등록된 상품이 존재하지 않을 시 · 이미 같은 모델명으로 등록된 상품이 존재할 시 에러 발생")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"SUCCESS\",\"result\":{\"name\":\"name\",\"modelNumber\":\"modelNumber\",\"brandName\":\"brandName\"}}")}, schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "ERROR (가입된 회원이 존재하지 않을 시 · 등록된 브랜드가 존재하지 않을 시 · 등록된 상품이 존재하지 않을 시)", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"ERROR\",\"result\":\"에러 메세지\"}")}, schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "409", description = "ERROR (이미 같은 모델명으로 등록된 상품이 존재할 시)", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"ERROR\",\"result\":\"에러 메세지\"}")}, schema = @Schema(implementation = Response.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<Response<ProductUpdateResponse>> updateProduct(@PathVariable Long id,
                                                                         @Validated @RequestPart ProductUpdateRequest requestDto,
                                                                         @RequestPart(required = false) MultipartFile multipartFile,
                                                                         BindingResult br,
                                                                         Authentication authentication) {
        return ResponseEntity.status(HttpStatus.OK).body(Response.success(productService.updateProduct(id, requestDto, multipartFile)));
    }
}

