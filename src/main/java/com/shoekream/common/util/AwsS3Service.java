package com.shoekream.common.util;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.shoekream.common.exception.ErrorCode;
import com.shoekream.common.exception.ShoeKreamException;
import com.shoekream.domain.brand.Brand;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

import static com.shoekream.common.util.constants.AwsConstants.*;

@Component
@RequiredArgsConstructor
public class AwsS3Service {

    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.s3.resizedBucket}")
    private String resizedBucket;

    public String uploadProductOriginImage(MultipartFile file) {
        return upload(file, bucket, ORIGIN_PRODUCT_FOLDER);
    }

    public String uploadBrandOriginImage(MultipartFile file) {
        return upload(file, bucket, ORIGIN_BRAND_FOLDER);
    }

    public String upload(MultipartFile file, String bucket, String folder) {

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(file.getContentType());
        objectMetadata.setContentLength(file.getSize());

        String originalFileName = file.getOriginalFilename();

        // 파일 형식 체크
        FileUtil.checkFileFormat(originalFileName);

        // 파일 생성
        String key = FileUtil.makeFileName(originalFileName,folder);

        try (InputStream inputStream = file.getInputStream()) {
            amazonS3Client.putObject(new PutObjectRequest(bucket, key, inputStream, objectMetadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
        } catch (IOException e) {
            throw new ShoeKreamException(ErrorCode.FILE_UPLOAD_ERROR);
        }

        String storedFileUrl = amazonS3Client.getUrl(bucket, key).toString();

        return storedFileUrl;
    }

    public void deleteProductImage(String originFileName, String resizedFileName) {
        delete(ORIGIN_PRODUCT_FOLDER + "/" + originFileName, bucket);
        delete(RESIZED_PRODUCT_FOLDER + "/" + resizedFileName, resizedBucket);

    }

    public void deleteBrandImage(String originFileName, String resizedFileName) {
        delete(ORIGIN_BRAND_FOLDER + "/" + originFileName, bucket);
        delete(RESIZED_BRAND_FOLDER + "/" + resizedFileName, resizedBucket);

    }

    public void delete(String filePath, String bucket) {
        amazonS3Client.deleteObject(new DeleteObjectRequest(bucket, filePath));
    }



}
