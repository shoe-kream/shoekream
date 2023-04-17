package com.shoekream.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shoekream.common.aop.BindingCheck;
import com.shoekream.common.config.SecurityConfig;
import com.shoekream.common.exception.ShoeKreamException;
import com.shoekream.common.util.JwtUtil;
import com.shoekream.domain.brand.dto.BrandInfo;
import com.shoekream.domain.cart.dto.CartProductRequest;
import com.shoekream.domain.cart.dto.WishProductResponse;
import com.shoekream.service.CartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Set;

import static com.shoekream.common.exception.ErrorCode.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(value = CartApiController.class)
@EnableAspectJAutoProxy
@Import({SecurityConfig.class, BindingCheck.class})
class CartApiControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    WebApplicationContext wac;

    @Value("${jwt.secret}")
    String secretKey;

    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    private CartService cartService;

    @BeforeEach
    public void setUpMockMvc() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(wac)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Nested
    @DisplayName("장바구니 아이탬 조회")
    class GetWishList {
        String email = "email";

        String token = JwtUtil.createToken(email, "ROLE_USER", secretKey, 1000L * 60 * 60);
        BrandInfo brandInfo = BrandInfo.builder()
                .id(1L)
                .name("brand")
                .originImagePath("originImagePath")
                .resizedImagePath("thumbnailImagePath")
                .build();
        WishProductResponse response = WishProductResponse.builder()
                .id(1L)
                .productId(1L)
                .brandInfo(brandInfo)
                .productName("productName")
                .build();

        @Test
        @DisplayName("장바구니 조회 성공 테스트")
        void success() throws Exception {

            given(cartService.getWishList(email))
                    .willReturn(Set.of(response));

            mockMvc.perform(get("/api/v1/carts")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andDo(print())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("SUCCESS"))
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result[0].id").value(1))
                    .andExpect(jsonPath("$.result[0].productId").value(1))
                    .andExpect(jsonPath("$.result[0].productName").value("productName"))
                    .andExpect(jsonPath("$.result[0].brandInfo").exists())
                    .andExpect(jsonPath("$.result[0].brandInfo.id").value(1))
                    .andExpect(jsonPath("$.result[0].brandInfo.name").value("brand"))
                    .andExpect(jsonPath("$.result[0].brandInfo.originImagePath").value("originImagePath"))
                    .andExpect(jsonPath("$.result[0].brandInfo.thumbnailImagePath").value("thumbnailImagePath"));

        }

        @Test
        @DisplayName("장바구니 조회 실패 테스트 (가입된 회원을 찾을 수 없는 경우)")
        void error() throws Exception {

            when(cartService.getWishList(email))
                    .thenThrow(new ShoeKreamException(USER_NOT_FOUND));

            mockMvc.perform(get("/api/v1/carts")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andDo(print())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("ERROR"))
                    .andExpect(jsonPath("$.result").exists());
        }
    }

    @Nested
    @DisplayName("장바구니 아이탬 등록")
    class AddWishProduct {
        String email = "email";
        Long productId = 1L;

        String token = JwtUtil.createToken(email, "ROLE_USER", secretKey, 1000L * 60 * 60);
        BrandInfo brandInfo = BrandInfo.builder()
                .id(1L)
                .name("brand")
                .originImagePath("originImagePath")
                .resizedImagePath("thumbnailImagePath")
                .build();
        WishProductResponse response = WishProductResponse.builder()
                .id(productId)
                .productId(1L)
                .brandInfo(brandInfo)
                .productName("productName")
                .build();

        CartProductRequest request = new CartProductRequest(productId);

        @Test
        @DisplayName("장바구니 상품 등록 성공 테스트")
        void success() throws Exception {

            given(cartService.addWishProduct(email,request))
                    .willReturn(response);

            mockMvc.perform(post("/api/v1/carts")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("SUCCESS"))
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result.id").value(1))
                    .andExpect(jsonPath("$.result.productId").value(productId))
                    .andExpect(jsonPath("$.result.productName").value("productName"))
                    .andExpect(jsonPath("$.result.brandInfo").exists())
                    .andExpect(jsonPath("$.result.brandInfo.id").value(1))
                    .andExpect(jsonPath("$.result.brandInfo.name").value("brand"))
                    .andExpect(jsonPath("$.result.brandInfo.originImagePath").value("originImagePath"))
                    .andExpect(jsonPath("$.result.brandInfo.thumbnailImagePath").value("thumbnailImagePath"));

        }

        @Test
        @DisplayName("장바구니 상품 등록 실패 테스트 (가입된 회원을 찾을 수 없는 경우)")
        void error() throws Exception {

            when(cartService.addWishProduct(email,request))
                    .thenThrow(new ShoeKreamException(USER_NOT_FOUND));

            mockMvc.perform(post("/api/v1/carts")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("ERROR"))
                    .andExpect(jsonPath("$.result").exists());
        }

        @Test
        @DisplayName("장바구니 상품 등록 실패 테스트 (상품을 찾을 수 없는 경우)")
        void error2() throws Exception {

            when(cartService.addWishProduct(email,request))
                    .thenThrow(new ShoeKreamException(PRODUCT_NOT_FOUND));

            mockMvc.perform(post("/api/v1/carts")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("ERROR"))
                    .andExpect(jsonPath("$.result").exists());
        }

        @Test
        @DisplayName("장바구니 상품 등록 실패 테스트 (이미 장바구니에 추가한 상품인 경우)")
        void error3() throws Exception {

            when(cartService.addWishProduct(email,request))
                    .thenThrow(new ShoeKreamException(DUPLICATED_WISH_PRODUCT));

            mockMvc.perform(post("/api/v1/carts")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("ERROR"))
                    .andExpect(jsonPath("$.result").exists());
        }
    }

    @Nested
    @DisplayName("장바구니 아이탬 삭제")
    class DeleteWishProduct {
        String email = "email";
        Long productId = 1L;

        String token = JwtUtil.createToken(email, "ROLE_USER", secretKey, 1000L * 60 * 60);
        BrandInfo brandInfo = BrandInfo.builder()
                .id(1L)
                .name("brand")
                .originImagePath("originImagePath")
                .resizedImagePath("thumbnailImagePath")
                .build();
        WishProductResponse response = WishProductResponse.builder()
                .id(productId)
                .productId(1L)
                .brandInfo(brandInfo)
                .productName("productName")
                .build();

        CartProductRequest request = new CartProductRequest(productId);

        @Test
        @DisplayName("장바구니 상품 삭제 성공 테스트")
        void success() throws Exception {

            given(cartService.deleteWishProduct(request))
                    .willReturn(response);

            mockMvc.perform(delete("/api/v1/carts")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("SUCCESS"))
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result.id").value(1))
                    .andExpect(jsonPath("$.result.productId").value(productId))
                    .andExpect(jsonPath("$.result.productName").value("productName"))
                    .andExpect(jsonPath("$.result.brandInfo").exists())
                    .andExpect(jsonPath("$.result.brandInfo.id").value(1))
                    .andExpect(jsonPath("$.result.brandInfo.name").value("brand"))
                    .andExpect(jsonPath("$.result.brandInfo.originImagePath").value("originImagePath"))
                    .andExpect(jsonPath("$.result.brandInfo.thumbnailImagePath").value("thumbnailImagePath"));

        }

        @Test
        @DisplayName("장바구니 상품 삭제 실패 테스트 (가입된 회원을 찾을 수 없는 경우)")
        void error() throws Exception {

            when(cartService.deleteWishProduct(request))
                    .thenThrow(new ShoeKreamException(USER_NOT_FOUND));

            mockMvc.perform(delete("/api/v1/carts")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("ERROR"))
                    .andExpect(jsonPath("$.result").exists());
        }

        @Test
        @DisplayName("장바구니 상품 삭제 실패 테스트 (상품을 찾을 수 없는 경우)")
        void error2() throws Exception {

            when(cartService.deleteWishProduct(request))
                    .thenThrow(new ShoeKreamException(PRODUCT_NOT_FOUND));

            mockMvc.perform(delete("/api/v1/carts")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("ERROR"))
                    .andExpect(jsonPath("$.result").exists());
        }

    }
}