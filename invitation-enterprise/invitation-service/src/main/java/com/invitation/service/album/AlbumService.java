package com.invitation.service.album;

import com.invitation.common.model.PageResult;
import com.invitation.common.model.R;
import com.invitation.model.entity.PhotoAlbum;

import java.util.List;

/**
 * 相册服务接口
 */
public interface AlbumService {

    /**
     * 上传照片
     */
    R<PhotoAlbum> uploadPhoto(Long invitationId, String photoUrl, String description, Integer sortOrder);

    /**
     * 批量上传照片
     */
    R<Void> batchUploadPhotos(Long invitationId, List<String> photoUrls);

    /**
     * 删除照片
     */
    R<Void> deletePhoto(Long photoId);

    /**
     * 更新照片描述
     */
    R<PhotoAlbum> updatePhotoDescription(Long photoId, String description);

    /**
     * 获取邀请函照片列表
     */
    R<List<PhotoAlbum>> listPhotos(Long invitationId);

    /**
     * 调整照片排序
     */
    R<Void> updateSortOrder(Long photoId, Integer sortOrder);
}
