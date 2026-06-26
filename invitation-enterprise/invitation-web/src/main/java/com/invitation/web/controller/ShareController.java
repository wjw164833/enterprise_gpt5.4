package com.invitation.web.controller;

import com.invitation.common.model.R;
import com.invitation.model.dto.share.PosterGenerateDTO;
import com.invitation.model.dto.share.ShortLinkVO;
import com.invitation.service.share.ShareService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 分享控制器
 */
@Tag(name = "分享管理")
@RestController
@RequestMapping("/api/v1/share")
@RequiredArgsConstructor
public class ShareController {

    private final ShareService shareService;

    @Operation(summary = "生成短链接")
    @PostMapping("/short-link/{invitationId}")
    public R<ShortLinkVO> generateShortLink(@PathVariable Long invitationId) {
        return shareService.generateShortLink(invitationId);
    }

    @Operation(summary = "生成海报")
    @PostMapping("/poster")
    public R<String> generatePoster(@RequestBody PosterGenerateDTO dto) {
        return shareService.generatePoster(dto);
    }

    @Operation(summary = "生成小程序码")
    @GetMapping("/wxacode/{invitationId}")
    public R<String> generateWxMiniQrCode(@PathVariable Long invitationId) {
        return shareService.generateWxMiniQrCode(invitationId);
    }
}
