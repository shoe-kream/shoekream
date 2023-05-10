package com.shoekream.controller;

import com.shoekream.common.Response;
import com.shoekream.domain.brand.dto.*;
import com.shoekream.service.BrandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
    @Tag(name = "Brand", description = "브랜드 정보 관련 API")
    @Operation(summary = "브랜드 정보 단건 조회", description = "JWT 토큰 필요(Authorization Header에 추가) | ADMIN 등급 계정만 가능 | 등록된 브랜드가 존재하지 않을 시 에러 발생")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"SUCCESS\",\"result\":{\"id\":1,\"name\":\"name\",\"originImagePath\":\"originImagePath\",\"resizedImagePath\":\"resizedImagePath\"}}")}, schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "ERROR (등록된 브랜드가 존재하지 않을 시)", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"ERROR\",\"result\":\"에러 메세지\"}")}, schema = @Schema(implementation = Response.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<Response<BrandInfo>> getBrandInfo(@PathVariable Long id) {
        return ResponseEntity.ok().body(Response.success(brandService.getBrandInfo(id)));
    }

    /**
     * 브랜드 정보 리스트 조회
     */
    @Tag(name = "Brand", description = "브랜드 정보 관련 API")
    @Operation(summary = "브랜드 정보 리스트 조회", description = "JWT 토큰 필요(Authorization Header에 추가) | ADMIN 등급 계정만 가능 | 등록된 브랜드가 존재하지 않을 시 에러 발생")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"SUCCESS\",\"result\":[{\"id\":1,\"name\":\"name\",\"originImagePath\":\"originImagePath\",\"resizedImagePath\":\"resizedImagePath\"},{\"id\":2,\"name\":\"name\",\"originImagePath\":\"originImagePath\",\"resizedImagePath\":\"resizedImagePath\"}]}")}, schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "ERROR (등록된 브랜드가 존재하지 않을 시)", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"ERROR\",\"result\":\"에러 메세지\"}")}, schema = @Schema(implementation = Response.class)))
    })
    @GetMapping("")
    public ResponseEntity<Response<List<BrandInfo>>> getBrandInfos() {
        return ResponseEntity.ok().body(Response.success(brandService.getBrandInfos()));
    }

    /**
     * 브랜드 생성
     */
    @Tag(name = "Brand", description = "브랜드 정보 관련 API")
    @Operation(summary = "브랜드 정보 등록", description = "JWT 토큰 필요(Authorization Header에 추가) | ADMIN 등급 계정만 가능 | 등록된 동명의 브랜드가 이미 존재할 시 에러 발생")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"SUCCESS\",\"result\":{\"name\":\"name\",\"originImagePath\":\"originImagePath\",\"resizedImagePath\":\"resizedImagePath\"}}")}, schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "409", description = "ERROR (등록된 동명의 브랜드가 이미 존재할 시)", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"ERROR\",\"result\":\"에러 메세지\"}")}, schema = @Schema(implementation = Response.class)))
    })
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
    @Tag(name = "Brand", description = "브랜드 정보 관련 API")
    @Operation(summary = "브랜드 정보 삭제", description = "JWT 토큰 필요(Authorization Header에 추가) | ADMIN 등급 계정만 가능 | 등록된 동명의 브랜드가 이미 존재할 시 에러 발생")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"SUCCESS\",\"result\":{\"name\":\"name\"}}")}, schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "ERROR (등록된 브랜드가 존재하지 않을 시)", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"ERROR\",\"result\":\"에러 메세지\"}")}, schema = @Schema(implementation = Response.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Response<BrandDeleteResponse>> deleteBrand(@PathVariable Long id, Authentication authentication) {
        return ResponseEntity.ok().body(Response.success(brandService.deleteBrand(id)));
    }

    /**
     * 브랜드 수정
     */
    @Tag(name = "Brand", description = "브랜드 정보 관련 API")
    @Operation(summary = "브랜드 정보 수정", description = "JWT 토큰 필요(Authorization Header에 추가) | ADMIN 등급 계정만 가능 | 등록된 브랜드가 존재하지 않을 시 · 변경될 브랜드 이름이 변경전과 같거나 이미 존재할 시 에러 발생")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"SUCCESS\",\"result\":{\"name\":\"name\",\"originImagePath\":\"originImagePath\",\"resizedImagePath\":\"resizedImagePath\"}}")}, schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "ERROR (등록된 브랜드가 존재하지 않을 시)", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"ERROR\",\"result\":\"에러 메세지\"}")}, schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "409", description = "ERROR (변경될 브랜드 이름이 변경전과 같거나 이미 존재할 시)", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"ERROR\",\"result\":\"에러 메세지\"}")}, schema = @Schema(implementation = Response.class)))
    })
    @PatchMapping("/{id}")
    public ResponseEntity<Response<BrandUpdateResponse>> updateBrand(@PathVariable Long id,
                                                                     @Valid @RequestPart BrandUpdateRequest requestDto,
                                                                     @RequestPart(required = false) MultipartFile multipartFile,
                                                                     BindingResult br,
                                                                     Authentication authentication) {
        return ResponseEntity.ok().body(Response.success(brandService.updateBrand(id, requestDto, multipartFile)));
    }


}
