package com.shoekream.controller;

import com.shoekream.common.Response;
import com.shoekream.domain.brand.dto.*;
import com.shoekream.service.BrandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/brands")
@RequiredArgsConstructor
@Slf4j
public class BrandApiController {

    private final BrandService brandService;

    @GetMapping("/{id}")
    public ResponseEntity<Response<BrandInfo>> getBrandInfo(@PathVariable Long id) {
        return ResponseEntity.ok().body(Response.success(brandService.getBrandInfo(id)));
    }

    @GetMapping("")
    public ResponseEntity<Response<List<BrandInfo>>> getBrandInfos() {
        return ResponseEntity.ok().body(Response.success(brandService.getBrandInfos()));
    }

    @PostMapping("")
    public ResponseEntity<Response<BrandCreateResponse>> createBrand(@Valid @RequestBody BrandCreateRequest requestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(Response.success(brandService.saveBrand(requestDto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Response<BrandDeleteResponse>> deleteBrand(@PathVariable Long id) {
        return ResponseEntity.ok().body(Response.success(brandService.deleteBrand(id)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Response<BrandUpdateResponse>> updateBrand(@PathVariable Long id, @Valid @RequestBody BrandUpdateRequest requestDto) {
        return ResponseEntity.ok().body(Response.success(brandService.updateBrand(id, requestDto)));
    }



}
