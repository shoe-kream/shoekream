package com.shoekream.service;

import com.shoekream.common.exception.ErrorCode;
import com.shoekream.common.exception.ShoeKreamException;
import com.shoekream.domain.address.Address;
import com.shoekream.domain.address.AddressRepository;
import com.shoekream.domain.product.Product;
import com.shoekream.domain.product.ProductRepository;
import com.shoekream.domain.product.dto.ProductInfoFromTrade;
import com.shoekream.domain.trade.Trade;
import com.shoekream.domain.trade.BidCreateRequest;
import com.shoekream.domain.trade.TradeRepository;
import com.shoekream.domain.trade.dto.TradeInfos;
import com.shoekream.domain.user.User;
import com.shoekream.domain.user.dto.UserInfoForTrade;
import com.shoekream.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class TradeService {

    private final TradeRepository tradeRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public TradeInfos getTradeInfosForBid(Long productId, String email, Double size) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ShoeKreamException(ErrorCode.USER_NOT_FOUND));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ShoeKreamException(ErrorCode.PRODUCT_NOT_FOUND));

        ProductInfoFromTrade productInfoFromTrade = product.getProductInfoFromTrade(size);
        UserInfoForTrade userInfoForTrade = user.toUserInfoForTrade();

        return TradeInfos.toTradeInfos(productInfoFromTrade, userInfoForTrade);
    }

    public void createSaleBid(String email, BidCreateRequest requestDto) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ShoeKreamException(ErrorCode.USER_NOT_FOUND));

        Product product = productRepository.findById(requestDto.getProductId())
                .orElseThrow(() -> new ShoeKreamException(ErrorCode.PRODUCT_NOT_FOUND));

        Address sellerAddress = user.getAddressList().stream()
                .filter(address -> address.getId().equals(requestDto.getAddressId()))
                .findAny()
                .orElseThrow(() -> new ShoeKreamException(ErrorCode.ADDRESS_NOT_FOUND));

        Trade trade = requestDto.toEntityForSeller(user, product, sellerAddress);

        tradeRepository.save(trade);
    }
}
