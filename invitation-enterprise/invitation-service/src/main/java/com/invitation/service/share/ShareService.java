package com.invitation.service.share;

import com.invitation.common.model.R;
import com.invitation.model.dto.share.PosterGenerateDTO;
import com.invitation.model.dto.share.ShortLinkVO;

public interface ShareService {
    ShortLinkVO generateShortLink(Long invitationId, String originalUrl);
    PosterGenerateDTO generatePoster(Long invitationId, int width, int height);
    byte[] generateQrcode(Long invitationId, String page);

    default R<ShortLinkVO> generateShortLink(Long invitationId) {
        return R.ok(generateShortLink(invitationId, "/i/" + invitationId));
    }

    default R<String> generatePoster(PosterGenerateDTO dto) {
        PosterGenerateDTO result = generatePoster(dto.getInvitationId(), dto.getWidth(), dto.getHeight());
        return R.ok(result != null ? String.valueOf(result.getInvitationId()) : "");
    }

    default R<String> generateWxMiniQrCode(Long invitationId) {
        byte[] bytes = generateQrcode(invitationId, "pages/invitation/detail");
        return R.ok(bytes != null ? java.util.Base64.getEncoder().encodeToString(bytes) : "");
    }
}
