package com.shoekream.controller;

import com.shoekream.common.Response;
import com.shoekream.domain.cart.dto.CartProductRequest;
import com.shoekream.domain.cart.dto.WishProductResponse;
import com.shoekream.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/carts")
public class CartApiController {

    private final CartService cartService;

    @Tag(name = "Cart", description = "장바구니 정보 관련 API")
    @Operation(summary = "장바구니 정보 조회", description = "JWT 토큰 필요(Authorization Header에 추가) | 가입된 회원이 존재하지 않을 시 에러 발생")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"SUCCESS\",\"result\":[{\"id\":1,\"productId\":\"1\",\"productName\":\"productName\",\"brandInfo\":{\"id\":1,\"name\":\"name\",\"originImagePath\":\"originImagePath\",\"resizedImagePath\":\"resizedImagePath\"}}]}")}, schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "ERROR (가입된 회원이 존재하지 않을 시)", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"ERROR\",\"result\":\"에러 메세지\"}")}, schema = @Schema(implementation = Response.class)))
    })
    @GetMapping
    public ResponseEntity<Response<Set<WishProductResponse>>> get(Authentication authentication) {
        Set<WishProductResponse> response = cartService.getWishList(authentication.getName());
        return ResponseEntity.ok(Response.success(response));
    }
    @Tag(name = "Cart", description = "장바구니 정보 관련 API")
    @Operation(summary = "장바구니 상품 등록", description = "JWT 토큰 필요(Authorization Header에 추가) | 가입된 회원이 존재하지 않을 시 · 등록된 상품이 존재하지 않을 시 · 장바구니 상품 정보 중복시 에러 발생")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"SUCCESS\",\"result\":{\"id\":1,\"productId\":1,\"productName\":\"productName\",\"brandInfo\":{\"id\":1,\"name\":\"name\",\"originImagePath\":\"originImagePath\",\"resizedImagePath\":\"resizedImagePath\"}}}")}, schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "ERROR (가입된 회원이 존재하지 않을 시 · 등록된 상품이 존재하지 않을 시)", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"ERROR\",\"result\":\"에러 메세지\"}")}, schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "409", description = "ERROR (장바구니 상품 정보 중복시)", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"ERROR\",\"result\":\"에러 메세지\"}")}, schema = @Schema(implementation = Response.class)))
    })
    @PostMapping
    public ResponseEntity<Response<WishProductResponse>> addWishProduct(Authentication authentication, @Validated @RequestBody CartProductRequest request, BindingResult br) {
        WishProductResponse response = cartService.addWishProduct(authentication.getName(), request);
        return ResponseEntity.ok(Response.success(response));
    }

    @Tag(name = "Cart", description = "장바구니 정보 관련 API")
    @Operation(summary = "장바구니 상품 삭제", description = "JWT 토큰 필요(Authorization Header에 추가) | 등록된 상품이 존재하지 않을 시 에러 발생")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"SUCCESS\",\"result\":{\"id\":1,\"productId\":1,\"productName\":\"productName\",\"brandInfo\":{\"id\":1,\"name\":\"name\",\"originImagePath\":\"originImagePath\",\"resizedImagePath\":\"resizedImagePath\"}}}")}, schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "ERROR (등록된 상품이 존재하지 않을 시)", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"ERROR\",\"result\":\"에러 메세지\"}")}, schema = @Schema(implementation = Response.class))),
    })
    @DeleteMapping
    public ResponseEntity<Response<WishProductResponse>> deleteWishProduct(Authentication authentication, @Validated @RequestBody CartProductRequest request, BindingResult br) {
        WishProductResponse response = cartService.deleteWishProduct(request);
        return ResponseEntity.ok(Response.success(response));
    }
}
