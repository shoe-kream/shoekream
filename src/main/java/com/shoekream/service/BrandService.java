package com.shoekream.service;

import com.shoekream.common.Response;
import com.shoekream.common.exception.ErrorCode;
import com.shoekream.common.exception.ShoeKreamException;
import com.shoekream.domain.brand.BrandRepository;
import com.shoekream.domain.brand.dto.BrandCreateResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class BrandService {

    private final BrandRepository brandRepository;

    public BrandCreateResponse getBrandInfo(Long id) {
        return brandRepository.findById(id)
                .orElseThrow(() -> new ShoeKreamException(ErrorCode.BRAND_NOT_FOUND))
                .toBrandInfo();
    }
}
