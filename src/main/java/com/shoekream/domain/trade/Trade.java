package com.shoekream.domain.trade;

import com.shoekream.domain.BaseTimeEntity;
import com.shoekream.domain.address.Address;
import com.shoekream.domain.product.Product;
import com.shoekream.domain.trade.dto.*;
import com.shoekream.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
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

    public void registerImmediatePurchase(User buyer, Address buyerAddress) {
        this.buyer = buyer;
        this.buyerAddress = buyerAddress;
        this.status = TradeStatus.PRE_SELLER_SHIPMENT;
    }

    public void registerImmediateSale(User seller, Address sellerAddress) {
        this.seller = seller;
        this.sellerAddress = sellerAddress;
        this.status = TradeStatus.PRE_SELLER_SHIPMENT;
    }

    public boolean hasBuyer() {
        return this.buyer != null;
    }

    public boolean hasSeller() {
        return this.seller != null;
    }

    public void undoTrade(Long price) {
        buyer.returnPoint(price);

    }

    public TradeDeleteResponse toTradeDeleteResponse() {
        return TradeDeleteResponse.builder()
                .productId(this.product.getId())
                .productName(this.product.getName())
                .productSize(this.productSize)
                .price(this.price)
                .build();
    }

    public void updateSellerToCompanyTrackingNumber(String trackingNumber) {
        this.companyToSellerTrackingNumber = trackingNumber;
        this.status = TradeStatus.PRE_WAREHOUSING;
    }

    public SendProductResponse toSendProductResponse() {
        return SendProductResponse.builder()
                .sellerId(this.seller.getId())
                .trackingNumber(this.companyToSellerTrackingNumber)
                .build();
    }

    public void updateStatus(TradeStatus status) {
        this.status = status;
    }

    public void cancelCausedByInspectionFailed(String cancelReason) {
        this.cancelReason = cancelReason;
        this.buyer.returnPoint(this.price);
    }

    public ReasonResponse toReasonResponse() {
        return ReasonResponse.builder()
                .tradeId(this.id)
                .cancelReason(this.cancelReason)
                .build();
    }

    public void updateCompanyToSellerTrackingNumber(String trackingNumber) {
        this.companyToSellerTrackingNumber = trackingNumber;
    }

    public ReturnResponse toReturnResponse() {
        return ReturnResponse.builder()
                .sellerId(this.seller.getId())
                .trackingNumber(this.companyToSellerTrackingNumber)
                .build();
    }

    public void updateCompanyToBuyerTrackingNumber(String trackingNumber) {
        this.companyToBuyerTrackingNumber = trackingNumber;
        this.status = TradeStatus.SHIPPING;
    }

    public ReceiveResponse toReceiveResponse() {
        return ReceiveResponse.builder()
                .buyerId(this.buyer.getId())
                .trackingNumber(this.companyToBuyerTrackingNumber)
                .build();
    }

    public void finishTrade() {
        this.status = TradeStatus.TRADE_COMPLETE;
        this.seller.returnPoint(this.price);
    }
}

