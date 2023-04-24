package com.shoekream.controller;

import com.shoekream.common.Response;
import com.shoekream.domain.trade.dto.*;
import com.shoekream.service.TradeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/trades")
@RequiredArgsConstructor
@Slf4j
public class TradeApiController {

    private final TradeService tradeService;

    @GetMapping("/{productId}")
    public ResponseEntity<Response<TradeInfos>> getTradeForBid(@PathVariable Long productId,
                                                               @RequestParam Double size,
                                                               Authentication authentication) {
        return ResponseEntity.ok(Response.success(tradeService.getTradeInfosForBid(productId, authentication.getName(), size)));
    }

    /**
     * 판매 입찰 생성
     * @param requestDto        입찰 DTO
     * @param authentication    유저 이메일
     * @param br                바인딩 체크
     */
    @PostMapping("/salesBid")
    public ResponseEntity<Response<String>> makeSaleBid(@Validated @RequestBody BidCreateRequest requestDto,
                                                         Authentication authentication,
                                                         BindingResult br) {
        tradeService.createSaleBid(authentication.getName(), requestDto);
        return ResponseEntity.ok(Response.success("ok"));
    }

    /**
     * 구매 입찰 생성
     * @param requestDto        입찰 DTO
     * @param authentication    유저 이메일
     * @param br                바인딩 체크
     */
    @PostMapping("/purchaseBid")
    public ResponseEntity<Response<String>> makePurchaseBid(@Validated @RequestBody BidCreateRequest requestDto,
                                                           Authentication authentication,
                                                           BindingResult br) {
        tradeService.createPurchaseBid(authentication.getName(), requestDto);
        return ResponseEntity.ok(Response.success("ok"));
    }

    /**
     * 즉시 구매 생성
     * @param requestDto        즉시 구매 DTO
     * @param authentication    유저 이메일
     * @param br                바인딩 체크
     */
    @PostMapping("/purchase")
    public ResponseEntity<Response<String>> purchase(@Validated @RequestBody ImmediatePurchaseRequest requestDto,
                                                     Authentication authentication,
                                                     BindingResult br) {
        tradeService.immediatePurchase(authentication.getName(), requestDto);
        return ResponseEntity.ok(Response.success("ok"));
    }

    /**
     * 즉시 판매 생성
     * @param requestDto        즉시 판매 DTO
     * @param authentication    유저 이메일
     * @param br                바인딩 체크
     */
    @PostMapping("/sale")
    public ResponseEntity<Response<String>> sale(@Validated @RequestBody ImmediateSaleRequest requestDto,
                                                 Authentication authentication,
                                                 BindingResult br) {
        tradeService.immediateSale(authentication.getName(), requestDto);
        return ResponseEntity.ok(Response.success("ok"));
    }

    /**
     * 입찰 취소
     * @param requestDto        입찰 취소 및 삭제 DTO
     * @param authentication    유저 이메일
     * @param br                바인딩 체크
     */
    @DeleteMapping("")
    public ResponseEntity<Response<TradeDeleteResponse>> deleteTrade(@Validated @RequestBody TradeDeleteRequest requestDto,
                                                        Authentication authentication,
                                                        BindingResult br) {
        return ResponseEntity.ok(Response.success(tradeService.deleteTrade(authentication.getName(), requestDto)));
    }

    /**
     * 입고 대기 요청
     * @param tradeId           입찰 id
     * @param requestDto        입고 대기 DTO
     * @param authentication    유저 이메일
     * @param br                바인딩 체크
     */
    @PatchMapping("/{tradeId}/sendingProduct")
    public ResponseEntity<Response<SendProductResponse>> sendProductToCompany(@PathVariable Long tradeId,
                                                                              @Validated @RequestBody SendingProductRequest requestDto,
                                                                              Authentication authentication,
                                                                              BindingResult br) {
        return ResponseEntity.ok(Response.success(tradeService.updateSellerToCompanyTrackingNumber(authentication.getName(),tradeId, requestDto)));
    }

    /**
     * @param tradeId           입찰 id
     * @param authentication    유저 이메일
     */
    @PatchMapping("/{tradeId}/wareHousing")
    public ResponseEntity<Response<String>> confirmWarehousing(@PathVariable Long tradeId,
                                                              Authentication authentication) {
        tradeService.confirmWarehousing(tradeId);
        return ResponseEntity.ok(Response.success("Confirm Warehousing clear"));
    }


}
