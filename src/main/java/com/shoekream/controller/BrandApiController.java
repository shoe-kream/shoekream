package com.shoekream.controller;

import com.shoekream.common.Response;
import com.shoekream.domain.brand.dto.*;
import com.shoekream.service.BrandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/brands")
@RequiredArgsConstructor
@Slf4j
public class BrandApiController {

    private final BrandService brandService;

    /**
     * 브랜드 정보 단건 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<Response<BrandInfo>> getBrandInfo(@PathVariable Long id) {
        return ResponseEntity.ok().body(Response.success(brandService.getBrandInfo(id)));
    }

    /**
     * 브랜드 정보 리스트 조회
     */
    @GetMapping("")
    public ResponseEntity<Response<List<BrandInfo>>> getBrandInfos() {
        return ResponseEntity.ok().body(Response.success(brandService.getBrandInfos()));
    }

    /**
     * 브랜드 생성
     */
    @PostMapping("")
    public ResponseEntity<Response<BrandCreateResponse>> createBrand(@Valid @RequestPart BrandCreateRequest requestDto,
                                                                     @RequestPart MultipartFile multipartFile,
                                                                     BindingResult br,
                                                                     Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED).body(Response.success(brandService.saveBrand(requestDto, multipartFile)));
    }

    /**
     * 브랜드 삭제
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Response<BrandDeleteResponse>> deleteBrand(@PathVariable Long id, Authentication authentication) {
        return ResponseEntity.ok().body(Response.success(brandService.deleteBrand(id)));
    }

    /**
     * 브랜드 수정
     */
    @PatchMapping("/{id}")
    public ResponseEntity<Response<BrandUpdateResponse>> updateBrand(@PathVariable Long id,
                                                                     @Valid @RequestPart BrandUpdateRequest requestDto,
                                                                     @RequestPart(required = false) MultipartFile multipartFile,
                                                                     BindingResult br,
                                                                     Authentication authentication) {
        return ResponseEntity.ok().body(Response.success(brandService.updateBrand(id, requestDto, multipartFile)));
    }



}
