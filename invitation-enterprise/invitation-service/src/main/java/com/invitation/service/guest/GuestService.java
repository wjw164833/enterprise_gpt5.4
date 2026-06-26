package com.invitation.service.guest;

import com.invitation.common.model.PageResult;
import com.invitation.common.model.R;
import com.invitation.model.dto.guest.GuestListVO;
import com.invitation.model.dto.guest.GuestReplyDTO;
import com.invitation.model.dto.guest.GuestReplyVO;
import com.invitation.model.entity.GuestReply;

public interface GuestService {
    GuestReplyVO reply(String shortCode, GuestReplyDTO dto, String ip, String ua);
    R<GuestReplyVO> replyByShortCode(String shortCode, GuestReplyDTO dto);
    PageResult<GuestReplyVO> listReplies(Long invitationId, int page, int size);
    R<PageResult<GuestListVO>> listGuests(Long invitationId, Integer page, Integer size);
    R<GuestReplyVO> getReplyDetail(Long replyId);
    void exportGuests(Long invitationId, javax.servlet.http.HttpServletResponse response);
}
