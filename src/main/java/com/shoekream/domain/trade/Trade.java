package com.shoekream.domain.trade;

import com.shoekream.domain.BaseTimeEntity;
import com.shoekream.domain.address.Address;
import com.shoekream.domain.product.Product;
import com.shoekream.domain.trade.dto.TradeBidInfos;
import com.shoekream.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Trade extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SELLER_ID")
    private User seller;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BUYER_ID")
    private User buyer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRODUCT_ID")
    private Product product;

    @Enumerated(EnumType.STRING)
    private TradeStatus status;

    private Long price;

    private Double productSize;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SELLER_ADDRESS_ID")
    private Address sellerAddress;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BUYER_ADDRESS_ID")
    private Address buyerAddress;

    private String sellerToCompanyTrackingNumber;

    private String companyToBuyerTrackingNumber;

    private String companyToSellerTrackingNumber;

    private String cancelReason;

    public TradeBidInfos toTradeBidInfos() {
        return TradeBidInfos.builder()
                .tradeId(this.id)
                .productId(this.product.getId())
                .productSize(this.productSize)
                .price(this.price)
                .build();
    }
}
