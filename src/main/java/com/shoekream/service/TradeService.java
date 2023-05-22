package com.shoekream.service;

import com.shoekream.common.exception.ErrorCode;
import com.shoekream.common.exception.ShoeKreamException;
import com.shoekream.domain.address.Address;
import com.shoekream.domain.point.Point;
import com.shoekream.domain.point.PointRepository;
import com.shoekream.domain.product.Product;
import com.shoekream.domain.product.ProductRepository;
import com.shoekream.domain.product.dto.ProductInfoFromTrade;
import com.shoekream.domain.trade.TradeStatus;
import com.shoekream.domain.trade.TradeValidator;
import com.shoekream.domain.trade.dto.*;
import com.shoekream.domain.trade.Trade;
import com.shoekream.domain.trade.TradeRepository;
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
    private final TradeValidator validator;

//    public TradeInfos getTradeInfosForBid(Long productId, String email, Double size) {
//
//        User user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new ShoeKreamException(ErrorCode.USER_NOT_FOUND));
//
//        Product product = productRepository.findById(productId)
//                .orElseThrow(() -> new ShoeKreamException(ErrorCode.PRODUCT_NOT_FOUND));
//
//        ProductInfoFromTrade productInfoFromTrade = product.getProductInfoFromTrade(size);
//        UserInfoForTrade userInfoForTrade = user.toUserInfoForTrade();
//
//        return TradeInfos.toTradeInfos(productInfoFromTrade, userInfoForTrade);
//    }

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
        Trade highestPurchaseBid = tradeRepository.findByProductAndProductSizeAndStatusAndSellerIsNull(product, requestDto.getProductSize(), TradeStatus.PRE_OFFER)
                .stream()
                .sorted(Comparator.comparing(Trade::getPrice).reversed())
                .findAny()
                .orElse(null);

        if (highestPurchaseBid != null) {
            if (requestDto.getPrice() < highestPurchaseBid.getPrice()) {
                throw new ShoeKreamException(ErrorCode.NOT_ALLOWED_SALE_BID_PRICE);
            }
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

        // 포인트 충분한지 확인
        user.checkEnoughPoint(requestDto.getPrice());

        // 입찰 등록하고자 하는 상품의 사이즈가 유효한지 확인
        checkExistProductSize(requestDto, product);

        // 구매 입찰은 판매 입찰의 최저가보다 높은 가격 불가
        Trade lowestSaleBid = tradeRepository.findByProductAndProductSizeAndStatusAndBuyerIsNull(product, requestDto.getProductSize(), TradeStatus.PRE_OFFER)
                .stream()
                .sorted(Comparator.comparing(Trade::getPrice))
                .findFirst()
                .orElse(null);

        if (lowestSaleBid != null) {
            if (requestDto.getPrice() > lowestSaleBid.getPrice()) {
                throw new ShoeKreamException(ErrorCode.NOT_ALLOWED_PURCHASE_BID_PRICE);
            }
        }

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
    public ImmediatePurchaseResponse immediatePurchase(String email, ImmediatePurchaseRequest requestDto) {

        User buyer = userRepository.findByEmail(email)
                .orElseThrow(() -> new ShoeKreamException(ErrorCode.USER_NOT_FOUND));

        Trade trade = tradeRepository.findPessimisticLockById(requestDto.getTradeId())
                .orElseThrow(() -> new ShoeKreamException(ErrorCode.TRADE_NOT_FOUND));

        ImmediatePurchaseResponse response = validator.purchaseValidate(trade);
        response.setTradeId(buyer.getId(), trade.getId());

        if(!response.isEligible()) {
            log.info(response.getRejectReason());
            return response;
        }

        // 요청 주소가 주소록에 있는지 확인
        Address buyerAddress = buyer.getAddressList().stream()
                .filter(address -> address.getId().equals(requestDto.getAddressId()))
                .findAny()
                .orElseThrow(() -> new ShoeKreamException(ErrorCode.ADDRESS_NOT_FOUND));

        // 구매자 포인트 충분한지 확인
        buyer.checkPointForPurchase(trade.getPrice());

        // 구매자 포인트 차감
        buyer.deductPoints(trade.getPrice());

        // 즉시 구매 진행 (판매자 발송 대기 상태로 변경)
        trade.registerImmediatePurchase(buyer, buyerAddress);

        // 구매자 포인트 차감 이력 생성 후 저장
        Point point = Point.registerPointDeductionHistory(buyer, trade.getPrice());
        pointRepository.save(point);

        return response;
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

    public TradeDeleteResponse deleteTrade(String email, TradeDeleteRequest requestDto) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ShoeKreamException(ErrorCode.USER_NOT_FOUND));

        Trade trade = tradeRepository.findById(requestDto.getTradeId())
                .orElseThrow(() -> new ShoeKreamException(ErrorCode.TRADE_NOT_FOUND));

        // PRE_OFFER vs PRE_SELLER_SHIPMENT 상태에서만 취소 가능
        if(isPermittedStatus(trade.getStatus())) {
            // 판매 입찰만 등록된 경우(아직 즉시 구매자는 없는 경우)
            if(trade.hasSeller() && !trade.hasBuyer()) {
                tradeRepository.delete(trade);
            } else { // 그 외엔 전부 구매자 있으므로 포인트 되돌리기
                trade.undoTrade(trade.getPrice());
                Point point = Point.returnPurchasePoint(trade.getBuyer(), trade.getPrice());
                pointRepository.save(point);
                tradeRepository.delete(trade);
            }
        }
        return trade.toTradeDeleteResponse();
    }

    private boolean isPermittedStatus(TradeStatus status) {
        if(status.equals(TradeStatus.PRE_OFFER) || status.equals(TradeStatus.PRE_SELLER_SHIPMENT)) {
            return true;
        } else {
            return false;
        }
    }

    public SendProductResponse updateSellerToCompanyTrackingNumber(String email, Long tradeId, SendingProductRequest requestDto) {

        User seller = userRepository.findByEmail(email)
                .orElseThrow(() -> new ShoeKreamException(ErrorCode.USER_NOT_FOUND));

        Trade trade = tradeRepository.findById(tradeId)
                .orElseThrow(() -> new ShoeKreamException(ErrorCode.TRADE_NOT_FOUND));

        // 판매자인지 확인
        if (!trade.getSeller().getEmail().equals(seller.getEmail())) {
            throw new ShoeKreamException(ErrorCode.USER_NOT_MATCH);
        }

        // 판매자 -> 회사 운송장번호 입력, status PRE_WAREHOUSING으로 변경
        trade.updateSellerToCompanyTrackingNumber(requestDto.getTrackingNumber());

        return trade.toSendProductResponse();
    }

    public void confirmWarehousing(Long tradeId) {

        Trade trade = tradeRepository.findById(tradeId)
                .orElseThrow(() -> new ShoeKreamException(ErrorCode.TRADE_NOT_FOUND));

        if(!trade.getStatus().equals(TradeStatus.PRE_WAREHOUSING)) {
            throw new ShoeKreamException(ErrorCode.IS_NOT_PRE_WAREHOUSING);
        }

        trade.updateStatus(TradeStatus.PRE_INSPECTION);
    }

    public void confirmInspection(Long tradeId) {

        Trade trade = tradeRepository.findById(tradeId)
                .orElseThrow(() -> new ShoeKreamException(ErrorCode.TRADE_NOT_FOUND));

        if(!trade.getStatus().equals(TradeStatus.PRE_INSPECTION)) {
            throw new ShoeKreamException(ErrorCode.IS_NOT_PRE_INSPECTION);
        }

        trade.updateStatus(TradeStatus.PRE_SHIPMENT);
    }

    public ReasonResponse inspectionFailed(Long tradeId, ReasonRequest requestDto) {

        Trade trade = tradeRepository.findById(tradeId)
                .orElseThrow(() -> new ShoeKreamException(ErrorCode.TRADE_NOT_FOUND));

        if(!trade.getStatus().equals(TradeStatus.PRE_INSPECTION)) {
            throw new ShoeKreamException(ErrorCode.IS_NOT_PRE_INSPECTION);
        }

        trade.cancelCausedByInspectionFailed(requestDto.getCancelReason());
        trade.updateStatus(TradeStatus.CANCEL);

        Point point = Point.returnPurchasePoint(trade.getBuyer(), trade.getPrice());
        pointRepository.save(point);

        return trade.toReasonResponse();
    }

    public ReturnResponse updateCompanyToSellerTrackingNumber(Long tradeId, SendingProductRequest requestDto) {

        Trade trade = tradeRepository.findById(tradeId)
                .orElseThrow(() -> new ShoeKreamException(ErrorCode.TRADE_NOT_FOUND));

        trade.updateCompanyToSellerTrackingNumber(requestDto.getTrackingNumber());

        return trade.toReturnResponse();
    }

    public ReceiveResponse updateCompanyToBuyerTrackingNumber(Long tradeId, SendingProductRequest requestDto) {

        Trade trade = tradeRepository.findById(tradeId)
                .orElseThrow(() -> new ShoeKreamException(ErrorCode.TRADE_NOT_FOUND));

        if(!trade.getStatus().equals(TradeStatus.PRE_SHIPMENT)) {
            throw new ShoeKreamException(ErrorCode.IS_NOT_PRE_SHIPMENT);
        }

        trade.updateCompanyToBuyerTrackingNumber(requestDto.getTrackingNumber());

        return trade.toReceiveResponse();
    }

    public void confirmPurchase(String buyerEmail, Long tradeId) {

        Trade trade = tradeRepository.findById(tradeId)
                .orElseThrow(() -> new ShoeKreamException(ErrorCode.TRADE_NOT_FOUND));

        if(!trade.getBuyer().getEmail().equals(buyerEmail)) {
            throw new ShoeKreamException(ErrorCode.USER_NOT_MATCH);
        }

        trade.finishTrade();

        Point point = Point.receivePurchasePoint(trade.getSeller(), trade.getPrice());
        pointRepository.save(point);

    }
}
