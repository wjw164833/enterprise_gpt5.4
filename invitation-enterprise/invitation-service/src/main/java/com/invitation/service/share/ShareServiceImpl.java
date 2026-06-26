package com.invitation.service.share;

import com.invitation.common.util.CanvasPosterUtil;
import com.invitation.common.util.OssUtil;
import com.invitation.common.util.ShortUrlUtil;
import com.invitation.common.util.WxMaUtil;
import com.invitation.infra.oss.OssService;
import com.invitation.model.dto.share.PosterGenerateDTO;
import com.invitation.model.dto.share.ShortLinkVO;
import com.invitation.model.entity.Invitation;
import com.invitation.model.entity.ShortLink;
import com.invitation.model.mapper.InvitationMapper;
import com.invitation.model.mapper.ShortLinkMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ShareServiceImpl implements ShareService {

    @Autowired
    private ShortLinkMapper shortLinkMapper;
    @Autowired
    private InvitationMapper invitationMapper;
    @Autowired
    private ShortUrlUtil shortUrlUtil;
    @Autowired
    private CanvasPosterUtil canvasPosterUtil;
    @Autowired
    private OssService ossService;
    @Autowired
    private OssUtil ossUtil;
    @Autowired
    private WxMaUtil wxMaUtil;

    @Override
    public ShortLinkVO generateShortLink(Long invitationId, String originalUrl) {
        String shortCode = shortUrlUtil.generateShortCode();
        ShortLink shortLink = new ShortLink();
        shortLink.setShortCode(shortCode);
        shortLink.setOriginalUrl(originalUrl);
        shortLink.setInvitationId(invitationId);
        shortLink.setClickCount(0);
        shortLinkMapper.insert(shortLink);

        ShortLinkVO vo = new ShortLinkVO();
        BeanUtils.copyProperties(shortLink, vo);
        vo.setShortUrl("/s/" + shortCode);
        return vo;
    }

    @Override
    public PosterGenerateDTO generatePoster(Long invitationId, int width, int height) {
        Invitation invitation = invitationMapper.selectById(invitationId);
        if (invitation == null) return null;

        byte[] posterBytes = canvasPosterUtil.generatePoster(
                invitation.getCoverImage(),
                invitation.getTitle(),
                invitation.getActivityDate() != null ? invitation.getActivityDate().toString() : "",
                invitation.getActivityAddress(),
                invitation.getQrcodeUrl(),
                width, height);

        if (posterBytes != null) {
            String objectKey = ossUtil.generateObjectKey("poster", "png");
            String posterUrl = ossService.uploadBytes(posterBytes, objectKey, "image/png");
            invitation.setPosterUrl(posterUrl);
            invitationMapper.updateById(invitation);
        }

        PosterGenerateDTO dto = new PosterGenerateDTO();
        dto.setInvitationId(invitationId);
        dto.setWidth(width);
        dto.setHeight(height);
        return dto;
    }

    @Override
    public byte[] generateQrcode(Long invitationId, String page) {
        String scene = "id=" + invitationId;
        return wxMaUtil.generateQrCode(scene, page, 430);
    }
}
