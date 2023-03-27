package com.shoekream.domain.point;


/*
 * @ 포인트 구분
 * POINT_CHARGE: 충전(+)
 * POINT_WITHDRAW: 출금(-)
 * PURCHASE_DEDUCTION: 구매 대금(-)
 * PURCHASE_RETURN: 구매 대금 반환(+)
 * POINT_REVENUE: 판매 대금(+)
 */

public enum PointDivision {
    POINT_CHARGE, POINT_WITHDRAW, PURCHASE_DEDUCTION, PURCHASE_RETURN, POINT_REVENUE;
}
