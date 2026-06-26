package com.invitation.infra.oss;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.region.Region;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * 腾讯云COS实现
 */
@Slf4j
@Service
@ConditionalOnProperty(name = "oss.type", havingValue = "tencent")
public class TencentCosImpl implements OssService {

    @Autowired
    private OssConfig ossConfig;

    @Override
    public String upload(MultipartFile file, String objectKey) {
        try {
            COSClient cosClient = createClient();
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());
            PutObjectRequest request = new PutObjectRequest(
                    ossConfig.getTencent().getBucketName(), objectKey, file.getInputStream(), metadata);
            cosClient.putObject(request);
            cosClient.shutdown();
            return getUrl(objectKey);
        } catch (IOException e) {
            log.error("腾讯云COS上传失败: {}", e.getMessage(), e);
            throw new RuntimeException("文件上传失败", e);
        }
    }

    @Override
    public String uploadBytes(byte[] bytes, String objectKey, String contentType) {
        COSClient cosClient = createClient();
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(bytes.length);
        metadata.setContentType(contentType);
        PutObjectRequest request = new PutObjectRequest(
                ossConfig.getTencent().getBucketName(), objectKey,
                new ByteArrayInputStream(bytes), metadata);
        cosClient.putObject(request);
        cosClient.shutdown();
        return getUrl(objectKey);
    }

    @Override
    public void delete(String objectKey) {
        COSClient cosClient = createClient();
        cosClient.deleteObject(ossConfig.getTencent().getBucketName(), objectKey);
        cosClient.shutdown();
    }

    @Override
    public String getUrl(String objectKey) {
        return "https://" + ossConfig.getTencent().getBucketName() + ".cos." + ossConfig.getTencent().getRegion() + ".myqcloud.com/" + objectKey;
    }

    private COSClient createClient() {
        COSCredentials credentials = new BasicCOSCredentials(
                ossConfig.getTencent().getSecretId(),
                ossConfig.getTencent().getSecretKey());
        ClientConfig clientConfig = new ClientConfig(new Region(ossConfig.getTencent().getRegion()));
        return new COSClient(credentials, clientConfig);
    }
}
