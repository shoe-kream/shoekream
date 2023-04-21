package com.shoekream.service;

import com.shoekream.common.exception.ShoeKreamException;
import com.shoekream.domain.brand.Brand;
import com.shoekream.domain.cart.Cart;
import com.shoekream.domain.cart.CartProduct;
import com.shoekream.domain.cart.CartProductRepository;
import com.shoekream.domain.cart.dto.CartProductRequest;
import com.shoekream.domain.product.Product;
import com.shoekream.domain.product.ProductRepository;
import com.shoekream.domain.user.User;
import com.shoekream.domain.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static com.shoekream.common.exception.ErrorCode.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CartProductRepository cartProductRepository;

    @InjectMocks
    private CartService cartService;

    @Mock
    private User mockUser;
    @Mock
    private Product mockProduct;
    @Mock
    private CartProduct mockCartProduct;
    @Mock
    private Brand mockBrand;
    @Mock
    private Cart mockCart;


    String email;
    Long productId;
    Long cartProductId;
    CartProductRequest cartProductRequest;

    @BeforeEach
    void setUp(){
        email = "email";
        productId = 1L;
        cartProductId = 1L;
        cartProductRequest = new CartProductRequest(productId);
    }


    @Nested
    @DisplayName("장바구니 조회")
    class GetWishList {

        @Test
        @DisplayName("장바구니 리스트 조회 성공")
        void success(){
            given(userRepository.findByEmail(email))
                    .willReturn(Optional.of(mockUser));

            assertDoesNotThrow(() -> cartService.getWishList(email));

            verify(userRepository, atLeastOnce()).findByEmail(email);
        }

        @Test
        @DisplayName("장바구니 리스트 조회 실패 (가입된 회원이 아닌 경우)")
        void error(){
            when(userRepository.findByEmail(email))
                    .thenReturn(Optional.empty());

            ShoeKreamException shoeKreamException = assertThrows(ShoeKreamException.class, () -> cartService.getWishList(email));

            assertThat(shoeKreamException.getErrorCode()).isEqualTo(USER_NOT_FOUND);

            verify(userRepository, atLeastOnce()).findByEmail(email);
        }
    }

    @Nested
    @DisplayName("장바구니 상품 등록")
    class AddWishProduct {

        @Test
        @DisplayName("장바구니 상품 등록 성공")
        void success(){
            given(userRepository.findByEmail(email))
                    .willReturn(Optional.of(mockUser));
            given(productRepository.findById(productId))
                    .willReturn(Optional.of(mockProduct));
            given(mockUser.getCart())
                    .willReturn(mockCart);
            given(cartProductRepository.save(any(CartProduct.class)))
                    .willReturn(mockCartProduct);

            assertDoesNotThrow(() -> cartService.addWishProduct(email, cartProductRequest));

            verify(userRepository, atLeastOnce()).findByEmail(email);
            verify(productRepository, atLeastOnce()).findById(productId);
            verify(cartProductRepository, atLeastOnce()).save(any(CartProduct.class));
        }

        @Test
        @DisplayName("장바구니 등록 실패 (가입된 회원이 아닌 경우)")
        void error(){
            when(userRepository.findByEmail(email))
                    .thenReturn(Optional.empty());

            ShoeKreamException shoeKreamException = assertThrows(ShoeKreamException.class, () -> cartService.addWishProduct(email, cartProductRequest));

            assertThat(shoeKreamException.getErrorCode()).isEqualTo(USER_NOT_FOUND);

            verify(userRepository, atLeastOnce()).findByEmail(email);
        }

        @Test
        @DisplayName("장바구니 등록 실패 (상품이 존재하지 않는 경우)")
        void error2(){
            given(userRepository.findByEmail(email))
                    .willReturn(Optional.of(mockUser));

            when(productRepository.findById(productId))
                    .thenReturn(Optional.empty());

            ShoeKreamException shoeKreamException = assertThrows(ShoeKreamException.class, () -> cartService.addWishProduct(email, cartProductRequest));

            assertThat(shoeKreamException.getErrorCode()).isEqualTo(PRODUCT_NOT_FOUND);

            verify(userRepository, atLeastOnce()).findByEmail(email);
            verify(productRepository, atLeastOnce()).findById(productId);
        }

        @Test
        @DisplayName("장바구니 등록 실패 (이미 장바구니에 존재하는 상품을 등록하는 경우)")
        void error3(){
            given(userRepository.findByEmail(email))
                    .willReturn(Optional.of(mockUser));
            given(productRepository.findById(productId))
                    .willReturn(Optional.of(mockProduct));
            given(mockUser.getCart())
                    .willReturn(mockCart);

            doThrow(new ShoeKreamException(DUPLICATED_WISH_PRODUCT))
                    .when(mockUser).checkWishProductDuplicate(any(Product.class));

            ShoeKreamException shoeKreamException = assertThrows(ShoeKreamException.class, () -> cartService.addWishProduct(email, cartProductRequest));

            assertThat(shoeKreamException.getErrorCode()).isEqualTo(DUPLICATED_WISH_PRODUCT);

            verify(userRepository, atLeastOnce()).findByEmail(email);
            verify(productRepository, atLeastOnce()).findById(productId);
        }
    }

    @Nested
    @DisplayName("장바구니 상품 삭제 등록")
    class DeleteWishProduct {

        @Test
        @DisplayName("장바구니 상품 삭제 성공")
        void success(){
            given(cartProductRepository.findById(cartProductId))
                    .willReturn(Optional.of(mockCartProduct));

            assertDoesNotThrow(() -> cartService.deleteWishProduct(cartProductRequest));

            verify(cartProductRepository, atLeastOnce()).findById(cartProductId);
        }

        @Test
        @DisplayName("장바구니 상품 삭제 실패 (장바구니에 상품이 존재하지 않는 경우)")
        void error(){
            when(cartProductRepository.findById(cartProductId))
                    .thenReturn(Optional.empty());

            ShoeKreamException shoeKreamException = assertThrows(ShoeKreamException.class, () -> cartService.deleteWishProduct(cartProductRequest));

            assertThat(shoeKreamException.getErrorCode()).isEqualTo(CART_PRODUCT_NOT_FOUND);

            verify(cartProductRepository, atLeastOnce()).findById(cartProductId);

        }
    }
}