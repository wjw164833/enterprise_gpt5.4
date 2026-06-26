package com.invitation.infra.oss;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * 阿里云OSS实现
 */
@Slf4j
@Service
@ConditionalOnProperty(name = "oss.type", havingValue = "aliyun")
public class AliyunOssImpl implements OssService {

    @Autowired
    private OssConfig ossConfig;

    @Override
    public String upload(MultipartFile file, String objectKey) {
        try {
            OSS ossClient = createClient();
            ossClient.putObject(ossConfig.getAliyun().getBucketName(), objectKey, file.getInputStream());
            ossClient.shutdown();
            return getUrl(objectKey);
        } catch (IOException e) {
            log.error("阿里云OSS上传失败: {}", e.getMessage(), e);
            throw new RuntimeException("文件上传失败", e);
        }
    }

    @Override
    public String uploadBytes(byte[] bytes, String objectKey, String contentType) {
        OSS ossClient = createClient();
        ossClient.putObject(ossConfig.getAliyun().getBucketName(), objectKey, new ByteArrayInputStream(bytes));
        ossClient.shutdown();
        return getUrl(objectKey);
    }

    @Override
    public void delete(String objectKey) {
        OSS ossClient = createClient();
        ossClient.deleteObject(ossConfig.getAliyun().getBucketName(), objectKey);
        ossClient.shutdown();
    }

    @Override
    public String getUrl(String objectKey) {
        return "https://" + ossConfig.getAliyun().getBucketName() + "." + ossConfig.getAliyun().getEndpoint() + "/" + objectKey;
    }

    private OSS createClient() {
        return new OSSClientBuilder().build(
                ossConfig.getAliyun().getEndpoint(),
                ossConfig.getAliyun().getAccessKeyId(),
                ossConfig.getAliyun().getAccessKeySecret()
        );
    }
}
