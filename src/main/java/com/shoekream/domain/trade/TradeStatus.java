package com.shoekream.domain.trade;

/**
 *     PRE_OFFER: 구매, 판매 입찰 신청
 *     PRE_SELLER_SHIPMENT: 판매자 발송 대기
 *     PRE_WAREHOUSING: 입고 대기(판매자 -> 회사)
 *     PRE_INSPECTION: 검수 대기
 *     PRE_SHIPMENT: 구매자 발송 대기
 *     SHIPPING: 배송중(회사 -> 구매자)
 *     TRADE_COMPLETE: 거래 완료
 *     CANCEL: 취소(판매자가 기간 내 상품 미발송 및 검수 탈락)
 */

public enum TradeStatus {
    PRE_OFFER,
    PRE_SELLER_SHIPMENT,
    PRE_WAREHOUSING,
    PRE_INSPECTION,
    PRE_SHIPMENT,
    SHIPPING,
    TRADE_COMPLETE,
    CANCEL
}