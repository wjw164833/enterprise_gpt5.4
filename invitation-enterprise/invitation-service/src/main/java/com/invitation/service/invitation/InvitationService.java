package com.invitation.service.invitation;

import com.invitation.common.model.PageResult;
import com.invitation.common.model.R;
import com.invitation.model.dto.invitation.*;
import com.invitation.model.entity.InvitationVersion;

import java.util.List;

public interface InvitationService {
    InvitationDetailVO create(Long userId, InvitationCreateDTO dto);
    InvitationDetailVO update(Long userId, Long id, InvitationUpdateDTO dto);
    void delete(Long userId, Long id);
    InvitationDetailVO getDetail(Long id);
    InvitationDetailVO getByShortCode(String shortCode);
    default R<InvitationDetailVO> getInvitationDetail(Long id) {
        return R.ok(getDetail(id));
    }
    default R<InvitationDetailVO> getInvitationByShortCode(String shortCode) {
        return R.ok(getByShortCode(shortCode));
    }
    PageResult<InvitationListVO> listMine(Long userId, InvitationQueryDTO query);
    void publish(Long userId, Long id);
    void unpublish(Long userId, Long id);
    List<InvitationVersion> getVersions(Long invitationId);
    void rollbackVersion(Long userId, Long invitationId, Long versionId);
    void incrementView(Long invitationId, String visitorId, String ip, String ua, String referer, String source);
}
