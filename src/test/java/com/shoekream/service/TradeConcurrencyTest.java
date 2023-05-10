package com.shoekream.service;

import com.shoekream.domain.trade.Trade;
import com.shoekream.domain.trade.TradeRepository;
import com.shoekream.domain.trade.dto.ImmediatePurchaseRequest;
import com.shoekream.domain.user.User;
import com.shoekream.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
class TradeConcurrencyTest {

    private final TradeService tradeService;
    private final TradeRepository tradeRepository;
    private final UserRepository userRepository;

    User buyer;
    final ImmediatePurchaseRequest immediatePurchaseRequest
            = ImmediatePurchaseRequest.builder()
            .tradeId(1L).productId(1L).addressId(3L).build();

    @BeforeEach
    void init() {
        buyer = userRepository.findById(3L).get();
    }

    // buyer - trade - product 1:N:1 관계
    // trade에 insert 되는 시점에 buyer
    @Test
    @DisplayName("판매 입찰 1개에, 동시에 10명이 즉시 구매하려는 상황")
    void immediatePurchase() throws InterruptedException {

        final int PURCHASE_PEOPLE = 10;
        final int SALES_BID = 1;

        CountDownLatch countDownLatch = new CountDownLatch(PURCHASE_PEOPLE);

        List<ImmediateBuyer> buyers = Stream
                .generate(() -> new ImmediateBuyer(buyer,countDownLatch))
                .limit(PURCHASE_PEOPLE)
                .collect(Collectors.toList());

        buyers.forEach(buyer -> new Thread(buyer).start());
        countDownLatch.await();

        List<Trade> trades = tradeRepository.findTradeById(immediatePurchaseRequest.getTradeId());
        long counts = trades.size();

        assertThat(counts).isEqualTo(SALES_BID);
    }

    private class ImmediateBuyer implements Runnable {
        private User buyer;
        private CountDownLatch countDownLatch;

        public ImmediateBuyer(User buyer, CountDownLatch countDownLatch) {
            this.buyer = buyer;
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void run() {
            tradeService.immediatePurchase(buyer.getEmail(), immediatePurchaseRequest);
            countDownLatch.countDown();
        }
    }
}
