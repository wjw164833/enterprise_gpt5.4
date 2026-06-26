package com.invitation.web.controller;

import com.invitation.common.annotation.OperationLog;
import com.invitation.common.model.PageResult;
import com.invitation.common.model.R;
import com.invitation.model.entity.Invitation;
import com.invitation.model.entity.User;
import com.invitation.service.admin.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 管理后台控制器
 */
@Tag(name = "管理后台")
@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @Operation(summary = "分页查询用户列表")
    @GetMapping("/users")
    public R<PageResult<User>> pageUsers(@RequestParam(required = false) String keyword,
                                          @RequestParam(required = false) Integer userType,
                                          @RequestParam(required = false) Integer status,
                                          @RequestParam(defaultValue = "1") Integer page,
                                          @RequestParam(defaultValue = "20") Integer size) {
        return adminService.pageUsers(keyword, userType, status, page, size);
    }

    @Operation(summary = "禁用/启用用户")
    @PutMapping("/users/{userId}/status")
    @OperationLog(module = "用户管理", action = "更新状态", description = "禁用或启用用户")
    public R<Void> updateUserStatus(@PathVariable Long userId, @RequestParam Integer status) {
        return adminService.updateUserStatus(userId, status);
    }

    @Operation(summary = "分页查询所有邀请函")
    @GetMapping("/invitations")
    public R<PageResult<Invitation>> pageInvitations(@RequestParam(required = false) String keyword,
                                                      @RequestParam(required = false) Integer status,
                                                      @RequestParam(defaultValue = "1") Integer page,
                                                      @RequestParam(defaultValue = "20") Integer size) {
        return adminService.pageInvitations(keyword, status, page, size);
    }

    @Operation(summary = "下架邀请函")
    @PutMapping("/invitations/{invitationId}/unpublish")
    @OperationLog(module = "邀请函管理", action = "下架", description = "下架邀请函")
    public R<Void> unpublishInvitation(@PathVariable Long invitationId, @RequestParam String reason) {
        return adminService.unpublishInvitation(invitationId, reason);
    }

    @Operation(summary = "审核邀请函")
    @PutMapping("/invitations/{invitationId}/review")
    @OperationLog(module = "邀请函管理", action = "审核", description = "审核邀请函")
    public R<Void> reviewInvitation(@PathVariable Long invitationId,
                                     @RequestParam Integer auditStatus,
                                     @RequestParam(required = false) String auditRemark) {
        return adminService.reviewInvitation(invitationId, auditStatus, auditRemark);
    }

    @Operation(summary = "获取系统统计")
    @GetMapping("/stats")
    public R<Map<String, Object>> getSystemStats() {
        return adminService.getSystemStats();
    }
}
