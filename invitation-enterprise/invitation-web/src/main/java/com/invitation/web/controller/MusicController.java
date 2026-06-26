package com.invitation.web.controller;

import com.invitation.common.model.PageResult;
import com.invitation.common.model.R;
import com.invitation.model.entity.MusicLibrary;
import com.invitation.service.music.MusicService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 音乐控制器
 */
@Tag(name = "音乐管理")
@RestController
@RequestMapping("/api/v1/music")
@RequiredArgsConstructor
public class MusicController {

    private final MusicService musicService;

    @Operation(summary = "分页查询音乐列表")
    @GetMapping
    public R<PageResult<MusicLibrary>> pageMusic(@RequestParam(required = false) String keyword,
                                                  @RequestParam(required = false) String category,
                                                  @RequestParam(defaultValue = "1") Integer page,
                                                  @RequestParam(defaultValue = "20") Integer size) {
        return musicService.pageMusic(keyword, category, page, size);
    }

    @Operation(summary = "获取音乐详情")
    @GetMapping("/{musicId}")
    public R<MusicLibrary> getMusicDetail(@PathVariable Long musicId) {
        return musicService.getMusicDetail(musicId);
    }

    @Operation(summary = "推荐音乐")
    @GetMapping("/recommend")
    public R<PageResult<MusicLibrary>> recommendMusic(@RequestParam(required = false) String eventType,
                                                       @RequestParam(defaultValue = "1") Integer page,
                                                       @RequestParam(defaultValue = "20") Integer size) {
        return musicService.recommendMusic(eventType, page, size);
    }
}
