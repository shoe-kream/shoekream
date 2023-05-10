package com.shoekream.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "이미 존재하는 이메일입니다."),
    DUPLICATE_NICKNAME(HttpStatus.CONFLICT, "이미 존재하는 닉네임입니다."),
    DUPLICATED_PRODUCT(HttpStatus.CONFLICT, "이미 존재하는 상품입니다."),

    WRONG_PASSWORD(HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다."),

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "가입된 회원을 찾을 수 없습니다."),
    ADDRESS_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 주소를 찾을 수 없습니다."),
    BRAND_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 브랜드를 찾을 수 없습니다."),
    DUPLICATED_BRAND(HttpStatus.CONFLICT, "이미 등록되어 있는 브랜드입니다."),
    DUPLICATED_WISH_PRODUCT(HttpStatus.CONFLICT, "이미 장바구니에 등록되어 있는 상품입니다."),
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 상품을 찾을 수 없습니다."),
    CART_PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "장바구니에서 해당 상품을 찾을 수 없습니다."),
    TRADE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 입찰 내역을 찾을 수 없습니다."),
    PURCHASE_BID_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 사이즈 상품의 어떠한 구매 입찰도 존재하지 않습니다."),
    SALE_BID_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 사이즈 상품의 어떠한 판매 입찰도 존재하지 않습니다."),


    USER_NOT_MATCH(HttpStatus.UNAUTHORIZED,"본인만 요청할 수 있습니다."),
    VERIFY_NOT_ALLOWED(HttpStatus.UNAUTHORIZED,"인증에 실패했습니다. 다시 시도해주세요."),

    CHANGE_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "닉네임은 7일에 한번 변경할 수 있습니다."),
    WITHDRAWAL_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "잔여 포인트가 남아있어 탈퇴할 수 없습니다."),
    NOT_ALLOWED_WITHDRAWAL_POINT(HttpStatus.BAD_REQUEST, "보유하신 포인트보다 많이 출금할 수 없습니다."),
    NOT_ALLOWED_PRODUCT_SIZE(HttpStatus.BAD_REQUEST, "해당 상품에 존재하는 사이즈가 아닙니다."),
    NOT_ALLOWED_SALE_BID_PRICE(HttpStatus.BAD_REQUEST, "판매 입찰가는 구매 입찰의 최고가보다 낮을 수 없습니다."),
    NOT_ALLOWED_PURCHASE_BID_PRICE(HttpStatus.BAD_REQUEST, "구매 입찰가는 판매 입찰의 최저가보다 낮을 수 없습니다."),
    IS_NOT_PRE_WAREHOUSING(HttpStatus.BAD_REQUEST, "입고 대기 상태의 상품이 아닙니다."),
    IS_NOT_PRE_INSPECTION(HttpStatus.BAD_REQUEST, "검수 대기 상태의 상품이 아닙니다."),
    IS_NOT_PRE_SHIPMENT(HttpStatus.BAD_REQUEST, "구매자 발송 대기의 상품이 아닙니다."),

    WRONG_FILE_FORMAT(HttpStatus.BAD_REQUEST, "잘못된 형식의 파일입니다"),
    FILE_UPLOAD_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드에 실패했습니다.");

    private HttpStatus httpStatus;
    private String message;
}