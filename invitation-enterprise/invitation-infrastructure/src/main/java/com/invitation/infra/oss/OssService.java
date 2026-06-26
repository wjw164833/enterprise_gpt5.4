package com.invitation.infra.oss;

import org.springframework.web.multipart.MultipartFile;

/**
 * OSS操作服务接口
 */
public interface OssService {

    /**
     * 上传文件
     * @param file 文件
     * @param objectKey 对象键（路径）
     * @return 文件访问URL
     */
    String upload(MultipartFile file, String objectKey);

    /**
     * 上传字节数组
     * @param bytes 字节数组
     * @param objectKey 对象键
     * @return 文件访问URL
     */
    String uploadBytes(byte[] bytes, String objectKey, String contentType);

    /**
     * 删除文件
     * @param objectKey 对象键
     */
    void delete(String objectKey);

    /**
     * 获取文件URL
     * @param objectKey 对象键
     * @return 访问URL
     */
    String getUrl(String objectKey);
}
