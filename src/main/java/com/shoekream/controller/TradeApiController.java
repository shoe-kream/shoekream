package com.shoekream.controller;

import com.shoekream.common.Response;
import com.shoekream.domain.trade.dto.*;
import com.shoekream.service.TradeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
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

//    @GetMapping("/{productId}")
//    public ResponseEntity<Response<TradeInfos>> getTradeForBid(@PathVariable Long productId,
//                                                               @RequestParam Double size,
//                                                               Authentication authentication) {
//        return ResponseEntity.ok(Response.success(tradeService.getTradeInfosForBid(productId, authentication.getName(), size)));
//    }

    /**
     * 판매 입찰 생성
     * @param requestDto        입찰 DTO - price, productSize, productId, addressId
     * @param authentication    판매 입찰자 이메일
     * @param br                바인딩 체크
     */
    @Tag(name = "Trade", description = "입찰 정보 관련 API")
    @Operation(summary = "판매 입찰 등록", description = "JWT 토큰 필요(Authorization Header에 추가) | 등록하는 판매 입찰가가 구매 입찰의 최고가 보다 낮을 시 · 해당 상품에 존재하는 사이즈가 아닐 시 · 가입된 회원이 존재하지 않을 시 · 등록된 상품이 존재하지 않을 시 · 등록된 주소가 존재하지 않을 시 에러 발생")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"SUCCESS\",\"result\":\"ok\"}")}, schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "400", description = "ERROR (등록하는 판매 입찰가가 구매 입찰의 최고가 보다 낮을 시 · 해당 상품에 존재하는 사이즈가 아닐 시)", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"ERROR\",\"result\":\"에러 메세지\"}")}, schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "ERROR (가입된 회원이 존재하지 않을 시 · 등록된 상품이 존재하지 않을 시 · 등록된 주소가 존재하지 않을 시 )", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"ERROR\",\"result\":\"에러 메세지\"}")}, schema = @Schema(implementation = Response.class)))

    })
    @PostMapping("/salesBid")
    public ResponseEntity<Response<String>> makeSaleBid(@Validated @RequestBody BidCreateRequest requestDto,
                                                        Authentication authentication,
                                                        BindingResult br) {
        tradeService.createSaleBid(authentication.getName(), requestDto);
        return ResponseEntity.ok(Response.success("ok"));
    }

    /**
     * 구매 입찰 생성
     * @param requestDto        입찰 DTO - price, productSize, productId, addressId
     * @param authentication    구매 입찰자 이메일
     * @param br                바인딩 체크
     */
    @Tag(name = "Trade", description = "입찰 정보 관련 API")
    @Operation(summary = "구매 입찰 등록", description = "JWT 토큰 필요(Authorization Header에 추가) | 등록하는 판매 입찰가가 구매 입찰의 최고가 보다 낮을 시 · 해당 상품에 존재하는 사이즈가 아닐 시 · 보유한 포인트보다 비싼 상품 구매 입찰 등록시 · 가입된 회원이 존재하지 않을 시 · 등록된 상품이 존재하지 않을 시 · 등록된 주소가 존재하지 않을 시 에러 발생")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"SUCCESS\",\"result\":\"ok\"}")}, schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "400", description = "ERROR (등록하는 판매 입찰가가 구매 입찰의 최고가 보다 낮을 시 · 해당 상품에 존재하는 사이즈가 아닐 시 · 보유한 포인트보다 비싼 상품 구매 입찰 등록시)", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"ERROR\",\"result\":\"에러 메세지\"}")}, schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "ERROR (가입된 회원이 존재하지 않을 시 · 등록된 상품이 존재하지 않을 시 · 등록된 주소가 존재하지 않을 시 )", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"ERROR\",\"result\":\"에러 메세지\"}")}, schema = @Schema(implementation = Response.class)))

    })
    @PostMapping("/purchaseBid")
    public ResponseEntity<Response<String>> makePurchaseBid(@Validated @RequestBody BidCreateRequest requestDto,
                                                            Authentication authentication,
                                                            BindingResult br) {
        tradeService.createPurchaseBid(authentication.getName(), requestDto);
        return ResponseEntity.ok(Response.success("ok"));
    }

    /**
     * 즉시 구매 생성
     * @param requestDto        즉시 구매 DTO - tradeId, productId, addressId
     * @param authentication    즉시 구매자 이메일
     * @param br                바인딩 체크
     */
    @Tag(name = "Trade", description = "입찰 정보 관련 API")
    @Operation(summary = "즉시 구매", description = "JWT 토큰 필요(Authorization Header에 추가) | 보유한 포인트보다 비싼 상품 즉시 구매 요청 시 · 가입된 회원이 존재하지 않을 시 · 등록된 상품이 존재하지 않을 시 · 등록된 주소가 존재하지 않을 시 에러 발생")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"SUCCESS\",\"result\":\"ok\"}")}, schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "400", description = "ERROR (보유한 포인트보다 비싼 상품 구매 입찰 등록시)", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"ERROR\",\"result\":\"에러 메세지\"}")}, schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "ERROR (가입된 회원이 존재하지 않을 시 · 등록된 상품이 존재하지 않을 시 · 등록된 주소가 존재하지 않을 시 )", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"ERROR\",\"result\":\"에러 메세지\"}")}, schema = @Schema(implementation = Response.class)))

    })
    @PostMapping("/purchase")
    public ResponseEntity<Response<String>> purchase(@Validated @RequestBody ImmediatePurchaseRequest requestDto,
                                                     Authentication authentication,
                                                     BindingResult br) {
        tradeService.immediatePurchase(authentication.getName(), requestDto);
        return ResponseEntity.ok(Response.success("ok"));
    }

    /**
     * 즉시 판매 생성
     * @param requestDto        즉시 판매 DTO - tradeId, productId, addressId
     * @param authentication    즉시 판매자 이메일
     * @param br                바인딩 체크
     */
    @Tag(name = "Trade", description = "입찰 정보 관련 API")
    @Operation(summary = "즉시 판매", description = "JWT 토큰 필요(Authorization Header에 추가) | 가입된 회원이 존재하지 않을 시 · 등록된 상품이 존재하지 않을 시 · 등록된 주소가 존재하지 않을 시 에러 발생")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"SUCCESS\",\"result\":\"ok\"}")}, schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "ERROR (가입된 회원이 존재하지 않을 시 · 등록된 상품이 존재하지 않을 시 · 등록된 주소가 존재하지 않을 시 )", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"ERROR\",\"result\":\"에러 메세지\"}")}, schema = @Schema(implementation = Response.class)))

    })
    @PostMapping("/sale")
    public ResponseEntity<Response<String>> sale(@Validated @RequestBody ImmediateSaleRequest requestDto,
                                                 Authentication authentication,
                                                 BindingResult br) {
        tradeService.immediateSale(authentication.getName(), requestDto);
        return ResponseEntity.ok(Response.success("ok"));
    }

    /**
     * 입찰 취소
     * @param requestDto        입찰 취소 및 삭제 DTO - tradeId, price
     * @param authentication    입찰자(구매/판매) 이메일
     * @param br                바인딩 체크
     */
    @Tag(name = "Trade", description = "입찰 정보 관련 API")
    @Operation(summary = "입찰 취소", description = "JWT 토큰 필요(Authorization Header에 추가) | 가입된 회원이 존재하지 않을 시 · 등록된 입찰 내역이 존재하지 않을 시")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"SUCCESS\",\"result\":{\"productId\":1,\"productName\":\"productName\",\"productSize\":200,\"price\":20000}}")}, schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "ERROR (가입된 회원이 존재하지 않을 시 · 등록된 입찰 내역이 존재하지 않을 시)", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"ERROR\",\"result\":\"에러 메세지\"}")}, schema = @Schema(implementation = Response.class)))

    })
    @DeleteMapping("")
    public ResponseEntity<Response<TradeDeleteResponse>> deleteTrade(@Validated @RequestBody TradeDeleteRequest requestDto,
                                                        Authentication authentication,
                                                        BindingResult br) {
        return ResponseEntity.ok(Response.success(tradeService.deleteTrade(authentication.getName(), requestDto)));
    }

    /**
     * 입고 대기 요청
     * @param tradeId           입찰 id
     * @param requestDto        입고 대기 DTO - trackingNumber(판매자 -> 회사)
     * @param authentication    구매자 이메일
     * @param br                바인딩 체크
     */
    @Tag(name = "Trade", description = "입찰 정보 관련 API")
    @Operation(summary = "입고 대기 요청", description = "JWT 토큰 필요(Authorization Header에 추가) | 가입된 회원이 존재하지 않을 시 · 등록된 입찰 내역이 존재하지 않을 시")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"SUCCESS\",\"result\":{\"sellerId\":1,\"trackingNumber\":\"trackingNumber\"}}")}, schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "401", description = "ERROR (판매자 본인이 요청하지 않는 경우)", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"ERROR\",\"result\":\"에러 메세지\"}")}, schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "ERROR (가입된 회원이 존재하지 않을 시 · 등록된 입찰 내역이 존재하지 않을 시)", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"ERROR\",\"result\":\"에러 메세지\"}")}, schema = @Schema(implementation = Response.class)))

    })
    @PatchMapping("/{tradeId}/sendingProduct")
    public ResponseEntity<Response<SendProductResponse>> sendProductToCompany(@PathVariable Long tradeId,
                                                                              @Validated @RequestBody SendingProductRequest requestDto,
                                                                              Authentication authentication,
                                                                              BindingResult br) {
        return ResponseEntity.ok(Response.success(tradeService.updateSellerToCompanyTrackingNumber(authentication.getName(),tradeId, requestDto)));
    }

    /**
     * 입고 확인 요청
     * @param tradeId           입찰 id
     * @param authentication    관리자 이메일
     */
    @Tag(name = "Trade", description = "입찰 정보 관련 API")
    @Operation(summary = "입고 확인 요청", description = "JWT 토큰 필요(Authorization Header에 추가) | 입고 대기 상태의 상품이 아닌 경우 · 가입된 회원이 존재하지 않을 시 · 등록된 입찰 내역이 존재하지 않을 시")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"SUCCESS\",\"result\":\"Warehousing confirm clear\"}")}, schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "400", description = "ERROR (입고 대기 상태의 상품이 아닌 경우)", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"ERROR\",\"result\":\"에러 메세지\"}")}, schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "ERROR (가입된 회원이 존재하지 않을 시 · 등록된 입찰 내역이 존재하지 않을 시)", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"ERROR\",\"result\":\"에러 메세지\"}")}, schema = @Schema(implementation = Response.class)))

    })
    @PatchMapping("/{tradeId}/wareHousing")
    public ResponseEntity<Response<String>> confirmWarehousing(@PathVariable Long tradeId,
                                                              Authentication authentication) {
        tradeService.confirmWarehousing(tradeId);
        return ResponseEntity.ok(Response.success("Warehousing confirm clear"));
    }

    /**
     * 검수 확인 요청
     * @param tradeId           입찰 id
     * @param authentication    관리자 이메일
     */
    @Tag(name = "Trade", description = "입찰 정보 관련 API")
    @Operation(summary = "검수 확인 요청", description = "JWT 토큰 필요(Authorization Header에 추가) | 검수 대기 상태의 상품이 아닌 경우 · 가입된 회원이 존재하지 않을 시 · 등록된 입찰 내역이 존재하지 않을 시")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"SUCCESS\",\"result\":\"Inspection Successfully done\"}")}, schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "400", description = "ERROR (검수 대기 상태의 상품이 아닌 경우)", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"ERROR\",\"result\":\"에러 메세지\"}")}, schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "ERROR (가입된 회원이 존재하지 않을 시 · 등록된 입찰 내역이 존재하지 않을 시)", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"ERROR\",\"result\":\"에러 메세지\"}")}, schema = @Schema(implementation = Response.class)))

    })
    @PatchMapping("/{tradeId}/inspection")
    public ResponseEntity<Response<String>> confirmInspection(@PathVariable Long tradeId,
                                                              Authentication authentication) {
        tradeService.confirmInspection(tradeId);
        return ResponseEntity.ok(Response.success("Inspection Successfully done"));
    }

    /**
     * 검수 실패 요청
     * @param tradeId           입찰 id
     * @param requestDto        검수 실패 요청 DTO - cancelReason
     * @param authentication    관리자 이메일
     */
    @Tag(name = "Trade", description = "입찰 정보 관련 API")
    @Operation(summary = "검수 실패 요청", description = "JWT 토큰 필요(Authorization Header에 추가) | 검수 대기 상태의 상품이 아닌 경우 · 가입된 회원이 존재하지 않을 시 · 등록된 입찰 내역이 존재하지 않을 시")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"SUCCESS\",\"result\":{\"tradeId\":1,\"cancelReason\":\"cancelReason\"}}")}, schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "400", description = "ERROR (검수 대기 상태의 상품이 아닌 경우)", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"ERROR\",\"result\":\"에러 메세지\"}")}, schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "ERROR (가입된 회원이 존재하지 않을 시 · 등록된 입찰 내역이 존재하지 않을 시)", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"ERROR\",\"result\":\"에러 메세지\"}")}, schema = @Schema(implementation = Response.class)))

    })
    @PatchMapping("/{tradeId}/inspectionFail")
    public ResponseEntity<Response<ReasonResponse>> failInspection(@PathVariable Long tradeId,
                                                     @RequestBody ReasonRequest requestDto,
                                                     Authentication authentication) {
        return ResponseEntity.ok(Response.success(tradeService.inspectionFailed(tradeId, requestDto)));
    }

    /**
     * 반송 요청
     * @param tradeId           입찰 id
     * @param requestDto        반송 요청 DTO - trackingNumber(회사 -> 판매자)
     * @param authentication    관리자 이메일
     */
    @Tag(name = "Trade", description = "입찰 정보 관련 API")
    @Operation(summary = "반송 요청", description = "JWT 토큰 필요(Authorization Header에 추가) | 가입된 회원이 존재하지 않을 시 · 등록된 입찰 내역이 존재하지 않을 시")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"SUCCESS\",\"result\":{\"sellerId\":1,\"trackingNumber\":\"trackingNumber\"}}")}, schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "ERROR (가입된 회원이 존재하지 않을 시 · 등록된 입찰 내역이 존재하지 않을 시)", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"ERROR\",\"result\":\"에러 메세지\"}")}, schema = @Schema(implementation = Response.class)))

    })
    @PatchMapping("/{tradeId}/returnProduct")
    public ResponseEntity<Response<ReturnResponse>> sendProductToSeller(@PathVariable Long tradeId,
                                                                        @RequestBody SendingProductRequest requestDto,
                                                                        Authentication authentication) {
        return ResponseEntity.ok(Response.success(tradeService.updateCompanyToSellerTrackingNumber(tradeId, requestDto)));
    }

    /**
     * 배송 요청
     * @param tradeId           입찰 id
     * @param requestDto        배송 요청 DTO - trackingNumber(회사 -> 구매자)
     * @param authentication    관리자 이메일
     */
    @Tag(name = "Trade", description = "입찰 정보 관련 API")
    @Operation(summary = "배송 요청", description = "JWT 토큰 필요(Authorization Header에 추가) | 구매자 발송 대기 상태의 상품이 아닌 경우 · 가입된 회원이 존재하지 않을 시 · 등록된 입찰 내역이 존재하지 않을 시")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"SUCCESS\",\"result\":{\"buyerId\":1,\"trackingNumber\":\"trackingNumber\"}}")}, schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "400", description = "ERROR (구매자 발송 대기 상태의 상품이 아닌 경우)", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"ERROR\",\"result\":\"에러 메세지\"}")}, schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "ERROR (가입된 회원이 존재하지 않을 시 · 등록된 입찰 내역이 존재하지 않을 시)", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"ERROR\",\"result\":\"에러 메세지\"}")}, schema = @Schema(implementation = Response.class)))

    })
    @PatchMapping("/{tradeId}/receivingProduct")
    public ResponseEntity<Response<ReceiveResponse>> sendProductToBuyer(@PathVariable Long tradeId,
                                                                        @RequestBody SendingProductRequest requestDto,
                                                                        Authentication authentication) {
        return ResponseEntity.ok(Response.success(tradeService.updateCompanyToBuyerTrackingNumber(tradeId, requestDto)));
    }

    /**
     * 구매 최종 확인
     * @param tradeId           입찰 id
     * @param authentication    구매자 이메일
     */
    @Tag(name = "Trade", description = "입찰 정보 관련 API")
    @Operation(summary = "배송 요청", description = "JWT 토큰 필요(Authorization Header에 추가) | 구매자 본인이 요청하지 않은 경우 · 가입된 회원이 존재하지 않을 시 · 등록된 입찰 내역이 존재하지 않을 시")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"SUCCESS\",\"result\":\"Trade successfully done\"}")}, schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "400", description = "ERROR (구매자 본인이 요청하지 않은 경우)", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"ERROR\",\"result\":\"에러 메세지\"}")}, schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "ERROR (가입된 회원이 존재하지 않을 시 · 등록된 입찰 내역이 존재하지 않을 시)", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"ERROR\",\"result\":\"에러 메세지\"}")}, schema = @Schema(implementation = Response.class)))

    })
    @PatchMapping("/{tradeId}/confirmPurchase")
    public ResponseEntity<Response<String>> confirmPurchase(@PathVariable Long tradeId,
                                                            Authentication authentication) {
        tradeService.confirmPurchase(authentication.getName(), tradeId);
        return ResponseEntity.ok(Response.success("Trade successfully done"));
    }


}
