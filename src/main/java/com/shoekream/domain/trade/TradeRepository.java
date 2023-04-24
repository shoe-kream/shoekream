package com.shoekream.domain.trade;

import com.shoekream.domain.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TradeRepository extends JpaRepository<Trade, Long> {

    List<Trade> findByProductAndProductSizeAndStatusAndSellerIsNull(Product product, Double productSize, TradeStatus status);
    List<Trade> findByProductAndProductSizeAndStatusAndBuyerIsNull(Product product, Double productSize, TradeStatus status);
}