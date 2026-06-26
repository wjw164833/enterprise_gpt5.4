package com.invitation.infra.oss;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 本地存储实现
 */
@Slf4j
@Service
@ConditionalOnProperty(name = "oss.type", havingValue = "local", matchIfMissing = true)
public class LocalStorageImpl implements OssService {

    @Autowired
    private OssConfig ossConfig;

    @Override
    public String upload(MultipartFile file, String objectKey) {
        try {
            Path filePath = Paths.get(ossConfig.getLocal().getBasePath(), objectKey);
            Files.createDirectories(filePath.getParent());
            file.transferTo(filePath.toFile());
            return getUrl(objectKey);
        } catch (IOException e) {
            log.error("本地上传失败: {}", e.getMessage(), e);
            throw new RuntimeException("文件上传失败", e);
        }
    }

    @Override
    public String uploadBytes(byte[] bytes, String objectKey, String contentType) {
        try {
            Path filePath = Paths.get(ossConfig.getLocal().getBasePath(), objectKey);
            Files.createDirectories(filePath.getParent());
            Files.write(filePath, bytes);
            return getUrl(objectKey);
        } catch (IOException e) {
            log.error("本地上传失败: {}", e.getMessage(), e);
            throw new RuntimeException("文件上传失败", e);
        }
    }

    @Override
    public void delete(String objectKey) {
        Path filePath = Paths.get(ossConfig.getLocal().getBasePath(), objectKey);
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            log.warn("本地文件删除失败: {}", e.getMessage());
        }
    }

    @Override
    public String getUrl(String objectKey) {
        return ossConfig.getLocal().getUrlPrefix() + objectKey;
    }
}
