package com.invitation.service.admin;

import com.invitation.common.model.PageResult;
import com.invitation.common.model.R;
import com.invitation.model.entity.Invitation;
import com.invitation.model.entity.User;

/**
 * 管理后台服务接口
 */
public interface AdminService {

    /**
     * 分页查询用户列表
     */
    R<PageResult<User>> pageUsers(String keyword, Integer userType, Integer status, Integer page, Integer size);

    /**
     * 禁用/启用用户
     */
    R<Void> updateUserStatus(Long userId, Integer status);

    /**
     * 分页查询所有邀请函
     */
    R<PageResult<Invitation>> pageInvitations(String keyword, Integer status, Integer page, Integer size);

    /**
     * 下架邀请函
     */
    R<Void> unpublishInvitation(Long invitationId, String reason);

    /**
     * 获取系统统计
     */
    R<java.util.Map<String, Object>> getSystemStats();

    /**
     * 审核邀请函
     */
    R<Void> reviewInvitation(Long invitationId, Integer auditStatus, String auditRemark);
}
