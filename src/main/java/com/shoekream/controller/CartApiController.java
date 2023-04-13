package com.shoekream.controller;

import com.shoekream.common.Response;
import com.shoekream.domain.cart.dto.WishProductResponse;
import com.shoekream.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/carts")
public class CartApiController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<Response<Set<WishProductResponse>>> get(Authentication authentication){
        Set<WishProductResponse> response = cartService.getWishList(authentication.getName());
        return ResponseEntity.ok(Response.success(response));
    }
}
