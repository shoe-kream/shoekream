package com.shoekream.controller;

import com.shoekream.common.Response;
import com.shoekream.domain.brand.dto.BrandCreateResponse;
import com.shoekream.service.BrandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/brands/")
@RequiredArgsConstructor
@Slf4j
public class BrandApiController {

    private final BrandService brandService;

    @GetMapping("/{id}")
    public ResponseEntity<Response<BrandCreateResponse>> getBrandInfo(@PathVariable Long id) {
        return ResponseEntity.ok().body(Response.success(brandService.getBrandInfo(id)));
    }

    @GetMapping()
    public ResponseEntity<Response<List<BrandCreateResponse>>> getBrandInfos() {
        return ResponseEntity.ok().body(Response.success(brandService.getBrandInfos()));
    }

}
