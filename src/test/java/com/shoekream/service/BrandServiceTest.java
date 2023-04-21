package com.shoekream.service;

import com.shoekream.common.exception.ErrorCode;
import com.shoekream.common.exception.ShoeKreamException;
import com.shoekream.common.util.AwsS3Service;
import com.shoekream.common.util.FileUtil;
import com.shoekream.domain.brand.Brand;
import com.shoekream.domain.brand.BrandRepository;
import com.shoekream.domain.brand.dto.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BrandServiceTest {

    @Mock
    private BrandRepository brandRepository;

    @Mock
    AwsS3Service awsS3Service;

    @InjectMocks
    private BrandService brandService;

    private Brand mockBrand;

    private Brand brand;
    private String originFileName = "sample.png";
    private String resizedFileName = "sample.png";
    private String originImagePath = "https://shoekream.s3.ap-northeast-2.amazonaws.com/brand/sample.png";
    private String resizedImagePath = "https://shoekream-resized.s3.ap-northeast-2.amazonaws.com/brand-resized/sample.png";
    private String changedOriginImagePath = "https://shoekream.s3.ap-northeast-2.amazonaws.com/brand/newSample.png";
    private String changedResizedImagePath = "https://shoekream-resized.s3.ap-northeast-2.amazonaws.com/brand-resized/newSample.png";

    private MultipartFile createImageFile() {
        return new MockMultipartFile("sample", "sample.png", MediaType.IMAGE_PNG_VALUE,
                "sample".getBytes());
    }

    @BeforeEach
    void setup() {
        brand = Brand.builder().id(1L).name("name").originImagePath(originImagePath).resizedImagePath(resizedImagePath).build();
        mockBrand = mock(Brand.class);
    }

    @Nested
    @DisplayName("조회")
    class GetBrandInfo {

        @Test
        @DisplayName("브랜드 조회 성공")
        void getBrandInfoSuccess() {

            given(brandRepository.findById(anyLong())).willReturn(Optional.of(brand));

            BrandInfo brandInfo = brandService.getBrandInfo(brand.getId());

            assertThat(brandInfo.getId()).isEqualTo(1L);
            assertThat(brandInfo.getName()).isEqualTo("name");

            then(brandRepository).should(times(1)).findById(anyLong());
        }

        @Test
        @DisplayName("브랜드 조회 실패 - 일치하는 브랜드 없음")
        void getBrandInfoFail() {

            given(brandRepository.findById(anyLong())).willReturn(Optional.empty());

            ShoeKreamException shoeKreamException = assertThrows(ShoeKreamException.class,
                    () -> brandService.getBrandInfo(brand.getId()));

            assertThat(shoeKreamException.getErrorCode()).isEqualTo(ErrorCode.BRAND_NOT_FOUND);
            assertThat(shoeKreamException.getErrorCode().getMessage()).isEqualTo("해당 브랜드를 찾을 수 없습니다.");

            then(brandRepository).should(times(1)).findById(anyLong());
        }

        @Test
        @DisplayName("브랜드 리스트 조회 성공")
        void getBrandInfosSuccess() {

            Brand brand2 = Brand.builder().id(2L).name("name2").build();
            List<Brand> brandList = List.of(brand, brand2);

            given(brandRepository.findAll()).willReturn(brandList);

            List<BrandInfo> brandInfos = brandService.getBrandInfos();

            assertThat(brandInfos.size()).isEqualTo(brandList.size());

            then(brandRepository).should(times(1)).findAll();
        }
    }

    @Nested
    @DisplayName("등록")
    class CreateBrand {

        BrandCreateRequest brandCreateRequest = BrandCreateRequest.builder()
                .name("name")
                .originImagePath(originImagePath)
                .resizedImagePath(resizedImagePath)
                .build();

        @Test
        @DisplayName("브랜드 등록 성공")
        void saveBrandSuccess() {

            MockedStatic<FileUtil> fileUtilMockedStatic = mockStatic(FileUtil.class);
            MultipartFile image = createImageFile();

            given(brandRepository.existsByName(brandCreateRequest.getName())).willReturn(false);
            given(awsS3Service.uploadBrandOriginImage(image)).willReturn(originImagePath);
            given(FileUtil.convertFolder(anyString(), anyString(), anyString())).willReturn(resizedImagePath);
            given(brandRepository.save(any(Brand.class))).willReturn(brand);

            BrandCreateResponse brandCreateResponse = brandService.saveBrand(brandCreateRequest, image);
            assertThat(brandCreateResponse.getOriginImagePath()).isEqualTo(originImagePath);
            assertThat(brandCreateResponse.getResizedImagePath()).isEqualTo(resizedImagePath);

            fileUtilMockedStatic.close();

            then(brandRepository).should(times(1)).existsByName(brandCreateRequest.getName());
            then(awsS3Service).should(times(1)).uploadBrandOriginImage(image);
            then(brandRepository).should(times(1)).save(any(Brand.class));
        }

        @Test
        @DisplayName("브랜드 등록 실패 - 브랜드 명 중복")
        void saveBrandFail1() {

            MultipartFile image = createImageFile();

            given(brandRepository.existsByName(brandCreateRequest.getName())).willReturn(true);

            ShoeKreamException shoeKreamException = assertThrows(ShoeKreamException.class,
                    () -> brandService.saveBrand(brandCreateRequest,image));

            assertThat(shoeKreamException.getErrorCode()).isEqualTo(ErrorCode.DUPLICATED_BRAND);
            assertThat(shoeKreamException.getErrorCode().getMessage()).isEqualTo("이미 등록되어 있는 브랜드입니다.");

            then(brandRepository).should(times(1)).existsByName(brandCreateRequest.getName());
        }

//        @Test
//        @DisplayName("브랜드 등록 실패 - s3업로드 실패")
//        void saveBrandFail2() {
//
//            MultipartFile image = createImageFile();
//
//            given(brandRepository.existsByName(brandCreateRequest.getName())).willReturn(false);
//            given(awsS3Service.uploadBrandOriginImage(image)).willThrow(ShoeKreamException.class);
//
//            ShoeKreamException shoeKreamException = assertThrows(ShoeKreamException.class,
//                    () -> brandService.saveBrand(brandCreateRequest,image));
//
//            assertThat(brand.getOriginImagePath()).isNull();
//            assertThat(brand.getResizedImagePath()).isNull();
//
//            then(brandRepository).should(times(1)).existsByName(brandCreateRequest.getName());
//            then(awsS3Service).should(times(1)).uploadBrandOriginImage(image);
//        }
    }

    @Nested
    @DisplayName("삭제")
    class DeleteBrand {

        @Test
        @DisplayName("브랜드 삭제 성공")
        void deleteBrandSuccess() {

            MockedStatic<FileUtil> fileUtilMockedStatic = mockStatic(FileUtil.class);

            given(brandRepository.findById(anyLong())).willReturn(Optional.of(brand));
            given(FileUtil.extractFileName(anyString())).willReturn(originFileName);
            given(FileUtil.extractFileName(anyString())).willReturn(resizedFileName);
            lenient().doNothing().when(awsS3Service).deleteBrandImage(originFileName, resizedFileName);
            willDoNothing().given(brandRepository).deleteById(anyLong());

            BrandDeleteResponse brandDeleteResponse = brandService.deleteBrand(brand.getId());
            assertThat(brandDeleteResponse.getName()).isEqualTo(brand.getName());

            fileUtilMockedStatic.close();

            then(brandRepository).should(times(1)).findById(anyLong());
            then(awsS3Service).should(times(1)).deleteBrandImage(originFileName,resizedFileName);
            then(brandRepository).should(times(1)).deleteById(anyLong());
        }

        @Test
        @DisplayName("브랜드 삭제 실패(1) - 브랜드 존재하지 않음")
        void deleteBrandFail1() {

            given(brandRepository.findById(anyLong())).willReturn(Optional.empty());

            ShoeKreamException shoeKreamException = assertThrows(ShoeKreamException.class,
                    () -> brandService.deleteBrand(brand.getId()));

            assertThat(shoeKreamException.getErrorCode()).isEqualTo(ErrorCode.BRAND_NOT_FOUND);
            assertThat(shoeKreamException.getErrorCode().getMessage()).isEqualTo("해당 브랜드를 찾을 수 없습니다.");

            then(brandRepository).should(times(1)).findById(anyLong());
        }
    }


}