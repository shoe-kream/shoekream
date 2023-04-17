package com.shoekream.domain.cart.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@EqualsAndHashCode
public class CartProductRequest {
    @NotNull(message = "상품 번호는 필수 입력 항목입니다.")
    @Positive(message = "유효하지 않은 번호입니다.")
    private Long productId;
}
