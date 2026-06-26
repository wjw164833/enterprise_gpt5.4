package com.invitation.service.album;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.invitation.common.enums.ResultCode;
import com.invitation.common.exception.BusinessException;
import com.invitation.common.model.LoginUser;
import com.invitation.common.model.R;
import com.invitation.model.entity.Invitation;
import com.invitation.model.entity.PhotoAlbum;
import com.invitation.model.mapper.InvitationMapper;
import com.invitation.model.mapper.PhotoAlbumMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 相册服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AlbumServiceImpl implements AlbumService {

    private final PhotoAlbumMapper photoAlbumMapper;
    private final InvitationMapper invitationMapper;

    @Override
    public R<PhotoAlbum> uploadPhoto(Long invitationId, String photoUrl, String description, Integer sortOrder) {
        Long userId = LoginUser.getUserId();
        Invitation invitation = invitationMapper.selectById(invitationId);
        if (invitation == null || !invitation.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.INVITATION_NOT_FOUND);
        }

        LambdaQueryWrapper<PhotoAlbum> countWrapper = new LambdaQueryWrapper<>();
        countWrapper.eq(PhotoAlbum::getInvitationId, invitationId);
        long count = photoAlbumMapper.selectCount(countWrapper);
        if (count >= 50) {
            throw new BusinessException(ResultCode.PHOTO_LIMIT_EXCEEDED);
        }

        PhotoAlbum photo = new PhotoAlbum();
        photo.setInvitationId(invitationId);
        photo.setPhotoUrl(photoUrl);
        photo.setDescription(description);
        photo.setSortOrder(sortOrder != null ? sortOrder : (int) count);
        photoAlbumMapper.insert(photo);

        log.info("照片上传成功: photoId={}, invitationId={}", photo.getId(), invitationId);
        return R.ok(photo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R<Void> batchUploadPhotos(Long invitationId, List<String> photoUrls) {
        Long userId = LoginUser.getUserId();
        Invitation invitation = invitationMapper.selectById(invitationId);
        if (invitation == null || !invitation.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.INVITATION_NOT_FOUND);
        }

        LambdaQueryWrapper<PhotoAlbum> countWrapper = new LambdaQueryWrapper<>();
        countWrapper.eq(PhotoAlbum::getInvitationId, invitationId);
        long existCount = photoAlbumMapper.selectCount(countWrapper);

        int sortOrder = (int) existCount;
        for (String photoUrl : photoUrls) {
            if (existCount + sortOrder - (int) existCount >= 50) {
                break;
            }
            PhotoAlbum photo = new PhotoAlbum();
            photo.setInvitationId(invitationId);
            photo.setPhotoUrl(photoUrl);
            photo.setSortOrder(sortOrder);
            photoAlbumMapper.insert(photo);
            sortOrder++;
        }

        log.info("批量上传照片: invitationId={}, count={}", invitationId, sortOrder - (int) existCount);
        return R.ok();
    }

    @Override
    public R<Void> deletePhoto(Long photoId) {
        Long userId = LoginUser.getUserId();
        PhotoAlbum photo = photoAlbumMapper.selectById(photoId);
        if (photo == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }

        Invitation invitation = invitationMapper.selectById(photo.getInvitationId());
        if (invitation == null || !invitation.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.PERMISSION_DENIED);
        }

        photoAlbumMapper.deleteById(photoId);
        return R.ok();
    }

    @Override
    public R<PhotoAlbum> updatePhotoDescription(Long photoId, String description) {
        Long userId = LoginUser.getUserId();
        PhotoAlbum photo = photoAlbumMapper.selectById(photoId);
        if (photo == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }

        Invitation invitation = invitationMapper.selectById(photo.getInvitationId());
        if (invitation == null || !invitation.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.PERMISSION_DENIED);
        }

        photo.setDescription(description);
        photoAlbumMapper.updateById(photo);
        return R.ok(photo);
    }

    @Override
    public R<List<PhotoAlbum>> listPhotos(Long invitationId) {
        LambdaQueryWrapper<PhotoAlbum> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PhotoAlbum::getInvitationId, invitationId)
               .orderByAsc(PhotoAlbum::getSortOrder);
        List<PhotoAlbum> photos = photoAlbumMapper.selectList(wrapper);
        return R.ok(photos);
    }

    @Override
    public R<Void> updateSortOrder(Long photoId, Integer sortOrder) {
        Long userId = LoginUser.getUserId();
        PhotoAlbum photo = photoAlbumMapper.selectById(photoId);
        if (photo == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }

        Invitation invitation = invitationMapper.selectById(photo.getInvitationId());
        if (invitation == null || !invitation.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.PERMISSION_DENIED);
        }

        photo.setSortOrder(sortOrder);
        photoAlbumMapper.updateById(photo);
        return R.ok();
    }
}
