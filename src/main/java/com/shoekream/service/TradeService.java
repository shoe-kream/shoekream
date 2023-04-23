package com.shoekream.service;

import com.shoekream.common.exception.ErrorCode;
import com.shoekream.common.exception.ShoeKreamException;
import com.shoekream.domain.address.Address;
import com.shoekream.domain.point.Point;
import com.shoekream.domain.point.PointRepository;
import com.shoekream.domain.product.Product;
import com.shoekream.domain.product.ProductRepository;
import com.shoekream.domain.product.dto.ProductInfoFromTrade;
import com.shoekream.domain.trade.dto.ImmediateSaleRequest;
import com.shoekream.domain.trade.Trade;
import com.shoekream.domain.trade.dto.BidCreateRequest;
import com.shoekream.domain.trade.TradeRepository;
import com.shoekream.domain.trade.dto.ImmediatePurchaseRequest;
import com.shoekream.domain.trade.dto.TradeInfos;
import com.shoekream.domain.user.User;
import com.shoekream.domain.user.dto.UserInfoForTrade;
import com.shoekream.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class TradeService {

    private final TradeRepository tradeRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    private final PointRepository pointRepository;

    public TradeInfos getTradeInfosForBid(Long productId, String email, Double size) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ShoeKreamException(ErrorCode.USER_NOT_FOUND));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ShoeKreamException(ErrorCode.PRODUCT_NOT_FOUND));

        ProductInfoFromTrade productInfoFromTrade = product.getProductInfoFromTrade(size);
        UserInfoForTrade userInfoForTrade = user.toUserInfoForTrade();

        return TradeInfos.toTradeInfos(productInfoFromTrade, userInfoForTrade);
    }

    @CacheEvict(value = "products",key = "#requestDto.productId")
    public void createSaleBid(String email, BidCreateRequest requestDto) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ShoeKreamException(ErrorCode.USER_NOT_FOUND));

        Product product = productRepository.findById(requestDto.getProductId())
                .orElseThrow(() -> new ShoeKreamException(ErrorCode.PRODUCT_NOT_FOUND));

        Address sellerAddress = user.getAddressList().stream()
                .filter(address -> address.getId().equals(requestDto.getAddressId()))
                .findAny()
                .orElseThrow(() -> new ShoeKreamException(ErrorCode.ADDRESS_NOT_FOUND));

        // 판매 입찰은 구매 입찰의 최고가보다 낮은 가격 불가
        Trade highestPurchaseBid = tradeRepository.findByProductAndProductSizeAndStatusPreOfferAndSellerIsNull(product, requestDto.getProductSize())
                .stream()
                .sorted(Comparator.comparing(Trade::getPrice).reversed())
                .findFirst()
                .orElseThrow(() -> new ShoeKreamException(ErrorCode.PURCHASE_BID_NOT_FOUND));

        if (requestDto.getPrice() < highestPurchaseBid.getPrice()) {
            throw new ShoeKreamException(ErrorCode.NOT_ALLOWED_SALE_BID_PRICE);
        }


        // 입찰 등록하고자 하는 상품의 사이즈가 유효한지 확인
        checkExistProductSize(requestDto, product);

        Trade trade = requestDto.toEntityForSeller(user, product, sellerAddress);

        tradeRepository.save(trade);
    }


    @CacheEvict(value = "products",key = "#requestDto.productId")
    public void createPurchaseBid(String email, BidCreateRequest requestDto) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ShoeKreamException(ErrorCode.USER_NOT_FOUND));

        Product product = productRepository.findById(requestDto.getProductId())
                .orElseThrow(() -> new ShoeKreamException(ErrorCode.PRODUCT_NOT_FOUND));

        // 요청 주소가 주소록에 있는지 확인
        Address buyerAddress = user.getAddressList().stream()
                .filter(address -> address.getId().equals(requestDto.getAddressId()))
                .findAny()
                .orElseThrow(() -> new ShoeKreamException(ErrorCode.ADDRESS_NOT_FOUND));

        // 구매 입찰은 판매 입찰의 최저가보다 높은 가격 불가
        Trade loewsSaleBid = tradeRepository.findByProductAndProductSizeAndStatusPreOfferAndBuyerIsNull(product, requestDto.getProductSize())
                .stream()
                .sorted(Comparator.comparing(Trade::getPrice))
                .findFirst()
                .orElseThrow(() -> new ShoeKreamException(ErrorCode.SALE_BID_NOT_FOUND));

        if (requestDto.getPrice() > loewsSaleBid.getPrice()) {
            throw new ShoeKreamException(ErrorCode.NOT_ALLOWED_PURCHASE_BID_PRICE);
        }

        // 입찰 등록하고자 하는 상품의 사이즈가 유효한지 확인
        checkExistProductSize(requestDto, product);

        // 포인트 충분한지 확인
        user.checkEnoughPoint(requestDto.getPrice());

        // 구매 입찰 생성
        Trade trade = requestDto.toEntityForBuyer(user, product, buyerAddress);

        // 포인트 차감
        user.deductPoints(requestDto.getPrice());

        tradeRepository.save(trade);

        // 포인트 차감 이력 생성 후 저장
        Point point = Point.registerPointDeductionHistory(user, trade.getPrice());
        pointRepository.save(point);

    }

    private void checkExistProductSize(BidCreateRequest requestDto, Product product) {
        if(requestDto.getProductSize() > product.getMaxSize() || requestDto.getProductSize() < product.getMinSize()) {
            throw new ShoeKreamException(ErrorCode.NOT_ALLOWED_PRODUCT_SIZE);
        }
    }

    @CacheEvict(value = "products",key = "#requestDto.productId")
    public void immediatePurchase(String email, ImmediatePurchaseRequest requestDto) {

        User buyer = userRepository.findByEmail(email)
                .orElseThrow(() -> new ShoeKreamException(ErrorCode.USER_NOT_FOUND));

        Trade trade = tradeRepository.findById(requestDto.getTradeId())
                .orElseThrow(() -> new ShoeKreamException(ErrorCode.TRADE_NOT_FOUND));

        // 요청 주소가 주소록에 있는지 확인
        Address buyerAddress = buyer.getAddressList().stream()
                .filter(address -> address.getId().equals(requestDto.getAddressId()))
                .findAny()
                .orElseThrow(() -> new ShoeKreamException(ErrorCode.ADDRESS_NOT_FOUND));

        // 구매자 포인트 충분한지 확인
        buyer.checkPointForPurchase(trade.getPrice());

        // 즉시 구매 진행 (판매자 발송 대기 상태로 변경)
        trade.registerImmediatePurchase(buyer, buyerAddress);

        // 구매자 포인트 차감
        buyer.deductPoints(trade.getPrice());

        // 구매자 포인트 차감 이력 생성 후 저장
        Point point = Point.registerPointDeductionHistory(buyer, trade.getPrice());
        pointRepository.save(point);
    }

    @CacheEvict(value = "products",key = "#requestDto.productId")
    public void immediateSale(String email, ImmediateSaleRequest requestDto) {

        User seller = userRepository.findByEmail(email)
                .orElseThrow(() -> new ShoeKreamException(ErrorCode.USER_NOT_FOUND));

        Trade trade = tradeRepository.findById(requestDto.getTradeId())
                .orElseThrow(() -> new ShoeKreamException(ErrorCode.TRADE_NOT_FOUND));

        // (판매자) 요청 주소가 주소록에 있는지 확인
        Address sellerAddress = seller.getAddressList().stream()
                .filter(address -> address.getId().equals(requestDto.getAddressId()))
                .findAny()
                .orElseThrow(() -> new ShoeKreamException(ErrorCode.ADDRESS_NOT_FOUND));

        // 즉시 판매 진행 (판매자 발송 대기 상태로 변경)
        trade.registerImmediateSale(seller, sellerAddress);
    }
}
