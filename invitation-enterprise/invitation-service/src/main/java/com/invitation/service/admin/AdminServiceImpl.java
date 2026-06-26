package com.invitation.service.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.invitation.common.enums.InvitationStatus;
import com.invitation.common.enums.ResultCode;
import com.invitation.common.exception.BusinessException;
import com.invitation.common.model.PageResult;
import com.invitation.common.model.R;
import com.invitation.model.entity.BlessMessage;
import com.invitation.model.entity.GuestReply;
import com.invitation.model.entity.Invitation;
import com.invitation.model.entity.User;
import com.invitation.model.mapper.BlessMessageMapper;
import com.invitation.model.mapper.GuestReplyMapper;
import com.invitation.model.mapper.InvitationMapper;
import com.invitation.model.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 管理后台服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserMapper userMapper;
    private final InvitationMapper invitationMapper;
    private final GuestReplyMapper guestReplyMapper;
    private final BlessMessageMapper blessMessageMapper;

    @Override
    public R<PageResult<User>> pageUsers(String keyword, Integer userType, Integer status, Integer page, Integer size) {
        Page<User> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(User::getNickname, keyword)
                              .or().like(User::getPhone, keyword));
        }
        if (userType != null) {
            wrapper.eq(User::getUserType, userType);
        }
        if (status != null) {
            wrapper.eq(User::getStatus, status);
        }
        wrapper.orderByDesc(User::getCreatedAt);

        Page<User> result = userMapper.selectPage(pageParam, wrapper);
        PageResult<User> pageResult = new PageResult<>();
        pageResult.setRecords(result.getRecords());
        pageResult.setTotal(result.getTotal());
        pageResult.setPage(page);
        pageResult.setSize(size);
        pageResult.setPages(result.getPages());
        return R.ok(pageResult);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R<Void> updateUserStatus(Long userId, Integer status) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(User::getId, userId)
                     .set(User::getStatus, status);
        userMapper.update(null, updateWrapper);

        log.info("管理员更新用户状态: userId={}, status={}", userId, status);
        return R.ok();
    }

    @Override
    public R<PageResult<Invitation>> pageInvitations(String keyword, Integer status, Integer page, Integer size) {
        Page<Invitation> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Invitation> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(Invitation::getTitle, keyword);
        }
        if (status != null) {
            wrapper.eq(Invitation::getStatus, status);
        }
        wrapper.orderByDesc(Invitation::getCreatedAt);

        Page<Invitation> result = invitationMapper.selectPage(pageParam, wrapper);
        PageResult<Invitation> pageResult = new PageResult<>();
        pageResult.setRecords(result.getRecords());
        pageResult.setTotal(result.getTotal());
        pageResult.setPage(page);
        pageResult.setSize(size);
        pageResult.setPages(result.getPages());
        return R.ok(pageResult);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R<Void> unpublishInvitation(Long invitationId, String reason) {
        Invitation invitation = invitationMapper.selectById(invitationId);
        if (invitation == null) {
            throw new BusinessException(ResultCode.INVITATION_NOT_FOUND);
        }

        LambdaUpdateWrapper<Invitation> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Invitation::getId, invitationId)
                     .set(Invitation::getStatus, InvitationStatus.UNPUBLISHED.getCode());
        invitationMapper.update(null, updateWrapper);

        log.info("管理员下架邀请函: invitationId={}, reason={}", invitationId, reason);
        return R.ok();
    }

    @Override
    public R<Map<String, Object>> getSystemStats() {
        Map<String, Object> stats = new HashMap<>();

        Long totalUsers = userMapper.selectCount(new LambdaQueryWrapper<>());
        Long totalInvitations = invitationMapper.selectCount(new LambdaQueryWrapper<>());
        Long publishedInvitations = invitationMapper.selectCount(
                new LambdaQueryWrapper<Invitation>().eq(Invitation::getStatus, 1));
        Long totalReplies = guestReplyMapper.selectCount(new LambdaQueryWrapper<>());
        Long totalBlessings = blessMessageMapper.selectCount(new LambdaQueryWrapper<>());

        LambdaQueryWrapper<User> todayUserWrapper = new LambdaQueryWrapper<>();
        todayUserWrapper.ge(User::getCreatedAt, LocalDateTime.now().toLocalDate().atStartOfDay());
        Long todayNewUsers = userMapper.selectCount(todayUserWrapper);

        LambdaQueryWrapper<Invitation> todayInvWrapper = new LambdaQueryWrapper<>();
        todayInvWrapper.ge(Invitation::getCreatedAt, LocalDateTime.now().toLocalDate().atStartOfDay());
        Long todayNewInvitations = invitationMapper.selectCount(todayInvWrapper);

        stats.put("totalUsers", totalUsers);
        stats.put("totalInvitations", totalInvitations);
        stats.put("publishedInvitations", publishedInvitations);
        stats.put("totalReplies", totalReplies);
        stats.put("totalBlessings", totalBlessings);
        stats.put("todayNewUsers", todayNewUsers);
        stats.put("todayNewInvitations", todayNewInvitations);

        return R.ok(stats);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R<Void> reviewInvitation(Long invitationId, Integer auditStatus, String auditRemark) {
        Invitation invitation = invitationMapper.selectById(invitationId);
        if (invitation == null) {
            throw new BusinessException(ResultCode.INVITATION_NOT_FOUND);
        }

        LambdaUpdateWrapper<Invitation> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Invitation::getId, invitationId)
                     .set(Invitation::getStatus, auditStatus);
        invitationMapper.update(null, updateWrapper);

        log.info("管理员审核邀请函: invitationId={}, auditStatus={}", invitationId, auditStatus);
        return R.ok();
    }
}
