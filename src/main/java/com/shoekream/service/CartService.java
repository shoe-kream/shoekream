package com.shoekream.service;


import com.shoekream.common.exception.ShoeKreamException;
import com.shoekream.domain.cart.CartProduct;
import com.shoekream.domain.cart.CartProductRepository;
import com.shoekream.domain.cart.dto.CartProductRequest;
import com.shoekream.domain.cart.dto.WishProductResponse;
import com.shoekream.domain.product.Product;
import com.shoekream.domain.product.ProductRepository;
import com.shoekream.domain.user.User;
import com.shoekream.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static com.shoekream.common.exception.ErrorCode.*;

@Transactional
@Service
@RequiredArgsConstructor
public class CartService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartProductRepository cartProductRepository;

    @Transactional(readOnly = true)
    public Set<WishProductResponse> getWishList(String email) {
        User foundUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new ShoeKreamException(USER_NOT_FOUND));

        return foundUser.getWishList();
    }

    /**
     * 장바구니에 등록 요청시, productId
     */
    public WishProductResponse addWishProduct(String email, CartProductRequest request) {
        User foundUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new ShoeKreamException(USER_NOT_FOUND));

        Product foundProduct = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ShoeKreamException(PRODUCT_NOT_FOUND));

        CartProduct wishProduct = CartProduct.createCartProduct(foundUser.getCart(), foundProduct);

        foundUser.checkWishProductDuplicate(foundProduct);

        CartProduct saved = cartProductRepository.save(wishProduct);

        return saved.toWishProductResponse();
    }

    /**
     * 삭제 요청시, cartProductId
     */
    public WishProductResponse deleteWishProduct(CartProductRequest request) {
        CartProduct foundCartProduct = cartProductRepository.findById(request.getProductId())
                .orElseThrow(() -> new ShoeKreamException(CART_PRODUCT_NOT_FOUND));

        cartProductRepository.delete(foundCartProduct);

        return foundCartProduct.toWishProductResponse();
    }
}
