package com.shoekream.controller;

import com.shoekream.common.Response;
import com.shoekream.domain.cart.dto.CartAddProductRequest;
import com.shoekream.domain.cart.dto.WishProductResponse;
import com.shoekream.service.CartService;
import lombok.RequiredArgsConstructor;
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

    @GetMapping
    public ResponseEntity<Response<Set<WishProductResponse>>> get(Authentication authentication) {
        Set<WishProductResponse> response = cartService.getWishList(authentication.getName());
        return ResponseEntity.ok(Response.success(response));
    }

    @PostMapping
    public ResponseEntity<Response<WishProductResponse>> addWishProduct(Authentication authentication, @Validated @RequestBody CartAddProductRequest request, BindingResult br) {
        WishProductResponse response = cartService.addWishProduct(authentication.getName(), request);
        return ResponseEntity.ok(Response.success(response));
    }
}