package com.invitation.service.bless;

import com.invitation.common.model.PageResult;
import com.invitation.common.model.R;
import com.invitation.model.entity.BlessMessage;

public interface BlessMessageService {
    BlessMessage create(Long invitationId, String guestName, String guestAvatar, String content, String theme, String ip);
    PageResult<BlessMessage> list(Long invitationId, int page, int size);
    void delete(Long userId, Long id);

    default R<BlessMessage> createBless(Long invitationId, String content, String guestName, String guestAvatar) {
        return R.ok(create(invitationId, guestName, guestAvatar, content, null, null));
    }

    default R<PageResult<BlessMessage>> listBless(Long invitationId, Integer page, Integer size) {
        return R.ok(list(invitationId, page, size));
    }

    default R<Void> deleteBless(Long blessId) {
        delete(null, blessId);
        return R.ok();
    }
}
