package com.invitation.web.controller;

import com.invitation.common.model.R;
import com.invitation.model.entity.PhotoAlbum;
import com.invitation.service.album.AlbumService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 相册控制器
 */
@Tag(name = "相册管理")
@RestController
@RequestMapping("/api/v1/album")
@RequiredArgsConstructor
public class AlbumController {

    private final AlbumService albumService;

    @Operation(summary = "上传照片")
    @PostMapping("/upload/{invitationId}")
    public R<PhotoAlbum> uploadPhoto(@PathVariable Long invitationId,
                                      @RequestParam String photoUrl,
                                      @RequestParam(required = false) String description,
                                      @RequestParam(required = false) Integer sortOrder) {
        return albumService.uploadPhoto(invitationId, photoUrl, description, sortOrder);
    }

    @Operation(summary = "删除照片")
    @DeleteMapping("/{photoId}")
    public R<Void> deletePhoto(@PathVariable Long photoId) {
        return albumService.deletePhoto(photoId);
    }

    @Operation(summary = "更新照片描述")
    @PutMapping("/{photoId}/description")
    public R<PhotoAlbum> updateDescription(@PathVariable Long photoId,
                                            @RequestParam String description) {
        return albumService.updatePhotoDescription(photoId, description);
    }

    @Operation(summary = "获取邀请函照片列表")
    @GetMapping("/list/{invitationId}")
    public R<List<PhotoAlbum>> listPhotos(@PathVariable Long invitationId) {
        return albumService.listPhotos(invitationId);
    }

    @Operation(summary = "调整照片排序")
    @PutMapping("/{photoId}/sort")
    public R<Void> updateSortOrder(@PathVariable Long photoId, @RequestParam Integer sortOrder) {
        return albumService.updateSortOrder(photoId, sortOrder);
    }
}
