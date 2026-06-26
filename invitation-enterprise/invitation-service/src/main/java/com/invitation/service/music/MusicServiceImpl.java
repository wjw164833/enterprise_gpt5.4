package com.invitation.service.music;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.invitation.common.enums.ResultCode;
import com.invitation.common.exception.BusinessException;
import com.invitation.common.model.PageResult;
import com.invitation.common.model.R;
import com.invitation.model.entity.MusicLibrary;
import com.invitation.model.mapper.MusicLibraryMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 音乐服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MusicServiceImpl implements MusicService {

    private final MusicLibraryMapper musicLibraryMapper;

    @Override
    public R<PageResult<MusicLibrary>> pageMusic(String keyword, String category, Integer page, Integer size) {
        Page<MusicLibrary> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<MusicLibrary> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(MusicLibrary::getName, keyword)
                              .or().like(MusicLibrary::getArtist, keyword));
        }
        if (category != null && !category.isEmpty()) {
            wrapper.eq(MusicLibrary::getCategory, category);
        }
        wrapper.eq(MusicLibrary::getStatus, 1)
               .orderByDesc(MusicLibrary::getCreatedAt);

        Page<MusicLibrary> result = musicLibraryMapper.selectPage(pageParam, wrapper);
        PageResult<MusicLibrary> pageResult = new PageResult<>();
        pageResult.setRecords(result.getRecords());
        pageResult.setTotal(result.getTotal());
        pageResult.setPage(page);
        pageResult.setSize(size);
        pageResult.setPages(result.getPages());
        return R.ok(pageResult);
    }

    @Override
    public R<MusicLibrary> getMusicDetail(Long musicId) {
        MusicLibrary music = musicLibraryMapper.selectById(musicId);
        if (music == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        return R.ok(music);
    }

    @Override
    public R<MusicLibrary> uploadMusic(String name, String artist, String category, String fileUrl, Long duration) {
        MusicLibrary music = new MusicLibrary();
        music.setName(name);
        music.setArtist(artist != null ? artist : "未知艺术家");
        music.setCategory(category != null ? category : "其他");
        music.setUrl(fileUrl);
        music.setDuration(duration != null ? duration.intValue() : 0);
        music.setStatus(1);
        musicLibraryMapper.insert(music);

        log.info("音乐上传成功: musicId={}, name={}", music.getId(), name);
        return R.ok(music);
    }

    @Override
    public R<Void> deleteMusic(Long musicId) {
        MusicLibrary music = musicLibraryMapper.selectById(musicId);
        if (music == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        musicLibraryMapper.deleteById(musicId);
        return R.ok();
    }

    @Override
    public R<PageResult<MusicLibrary>> recommendMusic(String eventType, Integer page, Integer size) {
        Page<MusicLibrary> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<MusicLibrary> wrapper = new LambdaQueryWrapper<>();

        if (eventType != null && !eventType.isEmpty()) {
            wrapper.eq(MusicLibrary::getCategory, eventType);
        }
        wrapper.eq(MusicLibrary::getStatus, 1)
               .orderByDesc(MusicLibrary::getCreatedAt);

        Page<MusicLibrary> result = musicLibraryMapper.selectPage(pageParam, wrapper);
        PageResult<MusicLibrary> pageResult = new PageResult<>();
        pageResult.setRecords(result.getRecords());
        pageResult.setTotal(result.getTotal());
        pageResult.setPage(page);
        pageResult.setSize(size);
        pageResult.setPages(result.getPages());
        return R.ok(pageResult);
    }
}
