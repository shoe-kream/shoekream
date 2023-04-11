package com.shoekream.common.util;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.shoekream.common.exception.ErrorCode;
import com.shoekream.common.exception.ShoeKreamException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Component
@RequiredArgsConstructor
public class AwsS3Service {

    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String uploadProductOriginImage(MultipartFile file) {
        return upload(file, bucket, "product");
    }

    public String uploadBrandOriginImage(MultipartFile file) {
        return upload(file, bucket, "brand");
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

    public void deleteProductImage(String originImageUrl) {
        delete("product/" + originImageUrl);

    }

    public void deleteBrandImage(String originImageUrl) {
        delete("brand/" + originImageUrl);

    }

    public void delete(String filePath) {
        amazonS3Client.deleteObject(new DeleteObjectRequest(bucket, filePath));
    }



}
