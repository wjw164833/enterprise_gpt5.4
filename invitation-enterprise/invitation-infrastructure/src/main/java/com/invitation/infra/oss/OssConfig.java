package com.invitation.infra.oss;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * OSS配置属性
 */
@Data
@Component
@ConfigurationProperties(prefix = "oss")
public class OssConfig {

    /** 存储类型: local/aliyun/tencent */
    private String type = "local";

    /** 本地存储配置 */
    private LocalConfig local = new LocalConfig();

    /** 阿里云OSS配置 */
    private AliyunConfig aliyun = new AliyunConfig();

    /** 腾讯云COS配置 */
    private TencentConfig tencent = new TencentConfig();

    @Data
    public static class LocalConfig {
        private String basePath = "/tmp/invitation-upload";
        private String urlPrefix = "http://localhost:8080/upload/";
    }

    @Data
    public static class AliyunConfig {
        private String endpoint;
        private String accessKeyId;
        private String accessKeySecret;
        private String bucketName;
    }

    @Data
    public static class TencentConfig {
        private String region;
        private String secretId;
        private String secretKey;
        private String bucketName;
    }
}
