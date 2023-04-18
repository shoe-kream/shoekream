package com.shoekream.service;

import com.shoekream.common.exception.ErrorCode;
import com.shoekream.common.exception.ShoeKreamException;
import com.shoekream.common.util.AwsS3Service;
import com.shoekream.common.util.FileUtil;
import com.shoekream.domain.brand.Brand;
import com.shoekream.domain.brand.BrandRepository;
import com.shoekream.domain.product.Product;
import com.shoekream.domain.product.ProductRepository;
import com.shoekream.domain.product.dto.ProductCreateRequest;
import com.shoekream.domain.product.dto.ProductCreateResponse;
import com.shoekream.domain.product.dto.ProductDeleteResponse;
import com.shoekream.domain.product.dto.ProductInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private BrandRepository brandRepository;

    @Mock
    AwsS3Service awsS3Service;

    @InjectMocks
    private ProductService productService;

    private Product product;
    private Brand brand;

    private String originFileName = "sample.png";
    private String resizedFileName = "sample.png";
    private String originImagePath = "https://shoekream.s3.ap-northeast-2.amazonaws.com/product/sample.png";
    private String resizedImagePath = "https://shoekream-resized.s3.ap-northeast-2.amazonaws.com/product-resized/sample.png";
    private String changedOriginImagePath = "https://shoekream.s3.ap-northeast-2.amazonaws.com/product/sample.png";
    private String changedResizedImagePath = "https://shoekream-resized.s3.ap-northeast-2.amazonaws.com/product-resized/sample.png";


    private MultipartFile createImageFile() {
        return new MockMultipartFile("sample", "sample.png", MediaType.IMAGE_PNG_VALUE,
                "sample".getBytes());
    }

    @BeforeEach
    void setup() {
        brand = Brand.builder().id(1L).name("name").originImagePath(originImagePath)
                .resizedImagePath(resizedImagePath).build();
        product = Product.builder().id(1L).name("name").modelNumber("modelNumber").originImagePath(originImagePath)
                .resizedImagePath(resizedImagePath).brand(brand).build();
    }

    @Nested
    @DisplayName("조회")
    class GetProductInfo {

        @Test
        @DisplayName("상품 조회 성공")
        void getProductInfoSuccess() {

            given(productRepository.findById(anyLong())).willReturn(Optional.of(product));

            ProductInfo productInfo = productService.getProductInfo(product.getId());

            assertThat(productInfo.getId()).isEqualTo(1L);
            assertThat(productInfo.getName()).isEqualTo("name");

            then(productRepository).should(times(1)).findById(anyLong());
        }

        @Test
        @DisplayName("상품 조회 실패 - 일치하는 상품 없음")
        void getProductInfoFail() {

            given(productRepository.findById(anyLong())).willReturn(Optional.empty());

            ShoeKreamException shoeKreamException = assertThrows(ShoeKreamException.class,
                    () -> productService.getProductInfo(product.getId()));

            assertThat(shoeKreamException.getErrorCode()).isEqualTo(ErrorCode.PRODUCT_NOT_FOUND);
            assertThat(shoeKreamException.getErrorCode().getMessage()).isEqualTo("해당 상품을 찾을 수 없습니다.");

            then(productRepository).should(times(1)).findById(anyLong());
        }
    }

    @Nested
    @DisplayName("등록")
    class CreateProduct {

        ProductCreateRequest productCreateRequest = ProductCreateRequest.builder()
                .brandId(1L)
                .name("name")
                .originImagePath(originImagePath)
                .resizedImagePath(resizedImagePath)
                .build();

        @Test
        @DisplayName("상품 등록 성공")
        void saveProductSuccess() {

            MockedStatic<FileUtil> fileUtilMockedStatic = mockStatic(FileUtil.class);
            MultipartFile image = createImageFile();

            given(brandRepository.findById(anyLong())).willReturn(Optional.of(brand));
            given(productRepository.existsByNameAndModelNumber(productCreateRequest.getName(),productCreateRequest.getModelNumber())).willReturn(false);
            given(awsS3Service.uploadProductOriginImage(image)).willReturn(originImagePath);
            given(FileUtil.convertFolder(anyString(), anyString(), anyString())).willReturn(resizedImagePath);
            given(productRepository.save(any(Product.class))).willReturn(product);

            ProductCreateResponse productCreateResponse = productService.saveProduct(productCreateRequest, image);
            assertThat(productCreateResponse.getOriginImagePath()).isEqualTo(originImagePath);
            assertThat(productCreateResponse.getResizedImagePath()).isEqualTo(resizedImagePath);

            fileUtilMockedStatic.close();

            then(brandRepository).should(times(1)).findById(anyLong());
            then(productRepository).should(times(1)).existsByNameAndModelNumber(productCreateRequest.getName(),productCreateRequest.getModelNumber());
            then(awsS3Service).should(times(1)).uploadProductOriginImage(image);
            then(productRepository).should(times(1)).save(any(Product.class));
        }

        @Test
        @DisplayName("상품 등록 실패 - 상품이 속한 브랜드 없음")
        void saveProductFail1() {

            MultipartFile image = createImageFile();

            given(brandRepository.findById(anyLong())).willReturn(Optional.empty());

            ShoeKreamException shoeKreamException = assertThrows(ShoeKreamException.class,
                    () -> productService.saveProduct(productCreateRequest, image));

            assertThat(shoeKreamException.getErrorCode()).isEqualTo(ErrorCode.BRAND_NOT_FOUND);
            assertThat(shoeKreamException.getErrorCode().getMessage()).isEqualTo("해당 브랜드를 찾을 수 없습니다.");

            then(brandRepository).should(times(1)).findById(anyLong());
        }

        @Test
        @DisplayName("상품 등록 실패 - 존재하는 상품 없음")
       void saveProductFail2() {

            MultipartFile image = createImageFile();

            given(brandRepository.findById(anyLong())).willReturn(Optional.of(brand));
            given(productRepository.existsByNameAndModelNumber(productCreateRequest.getName(),productCreateRequest.getModelNumber())).willReturn(true);

            ShoeKreamException shoeKreamException = assertThrows(ShoeKreamException.class,
                    () -> productService.saveProduct(productCreateRequest, image));

            assertThat(shoeKreamException.getErrorCode()).isEqualTo(ErrorCode.DUPLICATED_PRODUCT);
            assertThat(shoeKreamException.getErrorCode().getMessage()).isEqualTo("이미 존재하는 상품입니다.");

            then(brandRepository).should(times(1)).findById(anyLong());
            then(productRepository).should(times(1)).existsByNameAndModelNumber(productCreateRequest.getName(), productCreateRequest.getModelNumber());
        }

    }

    @Nested
    @DisplayName("삭제")
    class DeleteProduct {

        @Test
        @DisplayName("상품 삭제 성공")
        void deleteProductSuccess() {

            MockedStatic<FileUtil> fileUtilMockedStatic = mockStatic(FileUtil.class);

            given(productRepository.findById(anyLong())).willReturn(Optional.of(product));
            given(FileUtil.extractFileName(anyString())).willReturn(originFileName);
            given(FileUtil.extractFileName(anyString())).willReturn(resizedFileName);
            lenient().doNothing().when(awsS3Service).deleteProductImage(originFileName, resizedFileName);
            willDoNothing().given(productRepository).delete(any(Product.class));

            ProductDeleteResponse productDeleteResponse = productService.deleteProduct(product.getId());
            assertThat(productDeleteResponse.getName()).isEqualTo(product.getName());

            fileUtilMockedStatic.close();

            then(productRepository).should(times(1)).findById(anyLong());
            then(awsS3Service).should(times(1)).deleteProductImage(originFileName, resizedFileName);
            then(productRepository).should(times(1)).delete(any(Product.class));
        }

        @Test
        @DisplayName("상품 삭제 실패(1) - 상품 존재하지 않음")
        void deleteProductFail1() {

            given(productRepository.findById(anyLong())).willReturn(Optional.empty());

            ShoeKreamException shoeKreamException = assertThrows(ShoeKreamException.class,
                    () -> productService.deleteProduct(product.getId()));

            assertThat(shoeKreamException.getErrorCode()).isEqualTo(ErrorCode.PRODUCT_NOT_FOUND);
            assertThat(shoeKreamException.getErrorCode().getMessage()).isEqualTo("해당 상품을 찾을 수 없습니다.");

            then(productRepository).should(times(1)).findById(anyLong());
        }
    }

}