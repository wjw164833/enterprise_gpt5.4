package com.invitation.service.music;

import com.invitation.common.model.PageResult;
import com.invitation.common.model.R;
import com.invitation.model.entity.MusicLibrary;

/**
 * 音乐服务接口
 */
public interface MusicService {

    /**
     * 分页查询音乐库
     */
    R<PageResult<MusicLibrary>> pageMusic(String keyword, String category, Integer page, Integer size);

    /**
     * 获取音乐详情
     */
    R<MusicLibrary> getMusicDetail(Long musicId);

    /**
     * 上传音乐
     */
    R<MusicLibrary> uploadMusic(String name, String artist, String category, String fileUrl, Long duration);

    /**
     * 删除音乐
     */
    R<Void> deleteMusic(Long musicId);

    /**
     * 获取推荐音乐
     */
    R<PageResult<MusicLibrary>> recommendMusic(String eventType, Integer page, Integer size);
}
