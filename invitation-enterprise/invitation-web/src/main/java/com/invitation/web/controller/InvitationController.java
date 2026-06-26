package com.invitation.web.controller;

import com.invitation.common.annotation.OperationLog;
import com.invitation.common.annotation.RateLimit;
import com.invitation.common.model.LoginUser;
import com.invitation.common.model.PageResult;
import com.invitation.common.model.R;
import com.invitation.model.dto.invitation.InvitationCreateDTO;
import com.invitation.model.dto.invitation.InvitationDetailVO;
import com.invitation.model.dto.invitation.InvitationListVO;
import com.invitation.model.dto.invitation.InvitationQueryDTO;
import com.invitation.model.dto.invitation.InvitationUpdateDTO;
import com.invitation.service.invitation.InvitationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 邀请函管理控制器
 */
@Tag(name = "邀请函管理")
@RestController
@RequestMapping("/api/v1/invitations")
@RequiredArgsConstructor
public class InvitationController {

    private final InvitationService invitationService;

    @Operation(summary = "创建邀请函")
    @PostMapping
    @OperationLog(module = "邀请函", action = "创建", description = "创建新邀请函")
    public R<InvitationDetailVO> create(@Validated @RequestBody InvitationCreateDTO dto) {
        Long userId = LoginUser.getUserId();
        return R.ok(invitationService.create(userId, dto));
    }

    @Operation(summary = "更新邀请函")
    @PutMapping("/{id}")
    @OperationLog(module = "邀请函", action = "更新", description = "更新邀请函内容")
    public R<InvitationDetailVO> update(@PathVariable Long id, @Validated @RequestBody InvitationUpdateDTO dto) {
        Long userId = LoginUser.getUserId();
        return R.ok(invitationService.update(userId, id, dto));
    }

    @Operation(summary = "获取邀请函详情")
    @GetMapping("/{id}")
    public R<InvitationDetailVO> detail(@PathVariable Long id) {
        return R.ok(invitationService.getDetail(id));
    }

    @Operation(summary = "分页查询邀请函列表")
    @GetMapping
    public R<PageResult<InvitationListVO>> page(InvitationQueryDTO queryDTO) {
        Long userId = LoginUser.getUserId();
        return R.ok(invitationService.listMine(userId, queryDTO));
    }

    // P0-07: 添加 /mine 端点，查询当前用户的邀请函列表
    @Operation(summary = "查询我的邀请函列表")
    @GetMapping("/mine")
    public R<PageResult<InvitationListVO>> mine(InvitationQueryDTO queryDTO) {
        Long userId = LoginUser.getUserId();
        return R.ok(invitationService.listMine(userId, queryDTO));
    }

    @Operation(summary = "发布邀请函")
    @PostMapping("/{id}/publish")
    @OperationLog(module = "邀请函", action = "发布", description = "发布邀请函")
    public R<Void> publish(@PathVariable Long id) {
        Long userId = LoginUser.getUserId();
        invitationService.publish(userId, id);
        return R.ok();
    }

    @Operation(summary = "取消发布邀请函")
    @PostMapping("/{id}/unpublish")
    @OperationLog(module = "邀请函", action = "取消发布", description = "取消发布邀请函")
    public R<Void> unpublish(@PathVariable Long id) {
        Long userId = LoginUser.getUserId();
        invitationService.unpublish(userId, id);
        return R.ok();
    }

    @Operation(summary = "删除邀请函")
    @DeleteMapping("/{id}")
    @OperationLog(module = "邀请函", action = "删除", description = "删除邀请函")
    public R<Void> delete(@PathVariable Long id) {
        Long userId = LoginUser.getUserId();
        invitationService.delete(userId, id);
        return R.ok();
    }

    @Operation(summary = "复制邀请函")
    @PostMapping("/{id}/copy")
    @OperationLog(module = "邀请函", action = "复制", description = "复制邀请函")
    public R<InvitationDetailVO> copy(@PathVariable Long id) {
        return R.ok();
    }
}
