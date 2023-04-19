package com.shoekream.controller;

import com.shoekream.common.Response;
import com.shoekream.domain.trade.dto.TradeInfos;
import com.shoekream.service.TradeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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

}
