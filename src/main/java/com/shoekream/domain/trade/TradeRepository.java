package com.shoekream.domain.trade;

import com.shoekream.domain.product.Product;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TradeRepository extends JpaRepository<Trade, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Trade> findPessimisticLockById(@Param("id") Long id);

    List<Trade> findTradeById(Long tradeId);

    List<Trade> findByProductAndProductSizeAndStatusAndSellerIsNull(Product product, Double productSize, TradeStatus status);
    List<Trade> findByProductAndProductSizeAndStatusAndBuyerIsNull(Product product, Double productSize, TradeStatus status);
}