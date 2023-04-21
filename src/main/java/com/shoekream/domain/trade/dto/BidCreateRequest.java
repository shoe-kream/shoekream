package com.shoekream.domain.trade.dto;

import com.shoekream.domain.address.Address;
import com.shoekream.domain.product.Product;
import com.shoekream.domain.trade.Trade;
import com.shoekream.domain.trade.TradeStatus;
import com.shoekream.domain.user.User;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class BidCreateRequest {

    @Positive(message = "올바른 입찰가를 입력해야 합니다.")
    @NotNull(message = "입찰가는 반드시 입력해야 합니다.")
    private Long price;

    @Positive(message = "올바른 사이즈를 입력해야 합니다.")
    @NotNull(message = "사이즈는 반드시 입력해야 합니다.")
    private Double productSize;

    @NotNull(message = "상품의 사이즈를 입력해야 합니다.")
    private Long productId;

    @NotNull(message = "주소는 반드시 입력해야 합니다.")
    private Long addressId;

    public Trade toEntityForSeller(User user, Product product, Address sellerAddress) {
        return Trade.builder()
                .seller(user)
                .product(product)
                .status(TradeStatus.PRE_OFFER)
                .price(this.price)
                .productSize(this.productSize)
                .sellerAddress(sellerAddress)
                .build();
    }

    public Trade toEntityForBuyer(User user, Product product, Address buyerAddress) {
        return Trade.builder()
                .buyer(user)
                .product(product)
                .status(TradeStatus.PRE_OFFER)
                .price(this.price)
                .productSize(this.productSize)
                .buyerAddress(buyerAddress)
                .build();
    }
}
