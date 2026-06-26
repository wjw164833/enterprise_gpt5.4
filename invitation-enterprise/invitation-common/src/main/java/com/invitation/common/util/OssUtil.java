package com.invitation.common.util;

import com.invitation.common.constant.BusinessConstant;
import com.invitation.common.enums.ResultCode;
import com.invitation.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;

/**
 * OSS上传工具 - 文件校验
 */
@Slf4j
@Component
public class OssUtil {

    /**
     * 校验图片文件
     */
    public void validateImage(MultipartFile file) {
        validateFile(file, BusinessConstant.ALLOWED_IMAGE_TYPES, BusinessConstant.IMAGE_MAX_SIZE, "图片");
    }

    /**
     * 校验视频文件
     */
    public void validateVideo(MultipartFile file) {
        validateFile(file, BusinessConstant.ALLOWED_VIDEO_TYPES, BusinessConstant.VIDEO_MAX_SIZE, "视频");
    }

    /**
     * 校验音频文件
     */
    public void validateAudio(MultipartFile file) {
        validateFile(file, BusinessConstant.ALLOWED_AUDIO_TYPES, BusinessConstant.AUDIO_MAX_SIZE, "音频");
    }

    /**
     * 校验文件类型和大小
     */
    private void validateFile(MultipartFile file, String[] allowedTypes, long maxSize, String typeName) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), typeName + "文件不能为空");
        }
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.contains(".")) {
            throw new BusinessException(ResultCode.FILE_TYPE_NOT_ALLOWED);
        }
        String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
        if (!Arrays.asList(allowedTypes).contains(extension)) {
            throw new BusinessException(ResultCode.FILE_TYPE_NOT_ALLOWED.getCode(),
                    typeName + "格式不支持，仅支持: " + String.join(",", allowedTypes));
        }
        if (file.getSize() > maxSize) {
            throw new BusinessException(ResultCode.FILE_SIZE_EXCEEDED.getCode(),
                    typeName + "大小不能超过" + (maxSize / 1024 / 1024) + "MB");
        }
    }

    /**
     * 获取文件扩展名
     */
    public String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }

    /**
     * 生成存储路径
     */
    public String generateObjectKey(String prefix, String extension) {
        return prefix + "/" + new java.text.SimpleDateFormat("yyyy/MM/dd").format(new java.util.Date())
                + "/" + java.util.UUID.randomUUID().toString().replace("-", "") + "." + extension;
    }
}
