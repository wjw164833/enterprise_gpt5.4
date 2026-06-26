package com.invitation.service.guest;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.invitation.common.enums.ResultCode;
import com.invitation.common.exception.BusinessException;
import com.invitation.common.model.LoginUser;
import com.invitation.common.model.PageResult;
import com.invitation.common.model.R;
import com.invitation.model.dto.guest.GuestListVO;
import com.invitation.model.dto.guest.GuestReplyDTO;
import com.invitation.model.dto.guest.GuestReplyVO;
import com.invitation.model.entity.GuestReply;
import com.invitation.model.entity.Invitation;
import com.invitation.model.mapper.GuestReplyMapper;
import com.invitation.model.mapper.InvitationMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class GuestServiceImpl implements GuestService {

    @Autowired
    private GuestReplyMapper guestReplyMapper;
    @Autowired
    private InvitationMapper invitationMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public GuestReplyVO reply(String shortCode, GuestReplyDTO dto, String ip, String ua) {
        Invitation invitation = invitationMapper.selectByShortCode(shortCode);
        if (invitation == null) {
            throw new BusinessException(ResultCode.INVITATION_NOT_FOUND);
        }

        GuestReply reply = new GuestReply();
        reply.setInvitationId(invitation.getId());
        reply.setGuestName(dto.getGuestName());
        reply.setGuestPhone(dto.getGuestPhone());
        reply.setReplyStatus(dto.getReplyStatus());
        reply.setGuestCount(dto.getGuestCount() != null ? dto.getGuestCount() : 1);
        reply.setRemark(dto.getRemark());
        reply.setIpAddress(ip);
        reply.setUserAgent(ua);
        guestReplyMapper.insert(reply);

        // 更新邀请函统计
        invitationMapper.incrementReplyCount(invitation.getId());
        if (dto.getReplyStatus() == 1) {
            invitationMapper.incrementAttendCount(invitation.getId());
        }

        GuestReplyVO vo = new GuestReplyVO();
        BeanUtils.copyProperties(reply, vo);
        return vo;
    }

    /**
     * 通过短码回复（GuestController调用的方法）
     */
    @Transactional(rollbackFor = Exception.class)
    public R<GuestReplyVO> replyByShortCode(String shortCode, GuestReplyDTO dto) {
        Invitation invitation = invitationMapper.selectByShortCode(shortCode);
        if (invitation == null) {
            throw new BusinessException(ResultCode.INVITATION_NOT_FOUND);
        }

        GuestReply reply = new GuestReply();
        reply.setInvitationId(invitation.getId());
        reply.setGuestName(dto.getGuestName());
        reply.setGuestPhone(dto.getGuestPhone());
        reply.setReplyStatus(dto.getReplyStatus());
        reply.setGuestCount(dto.getGuestCount() != null ? dto.getGuestCount() : 1);
        reply.setRemark(dto.getRemark());
        reply.setIpAddress(null);
        reply.setUserAgent(null);
        guestReplyMapper.insert(reply);

        // 更新邀请函统计
        invitationMapper.incrementReplyCount(invitation.getId());
        if (dto.getReplyStatus() == 1) {
            invitationMapper.incrementAttendCount(invitation.getId());
        }

        GuestReplyVO vo = new GuestReplyVO();
        BeanUtils.copyProperties(reply, vo);
        return R.ok(vo);
    }

    @Override
    public PageResult<GuestReplyVO> listReplies(Long invitationId, int page, int size) {
        LambdaQueryWrapper<GuestReply> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GuestReply::getInvitationId, invitationId)
               .orderByDesc(GuestReply::getCreatedAt);
        IPage<GuestReply> pageResult = guestReplyMapper.selectPage(new Page<>(page, size), wrapper);

        List<GuestReplyVO> records = pageResult.getRecords().stream().map(r -> {
            GuestReplyVO vo = new GuestReplyVO();
            BeanUtils.copyProperties(r, vo);
            return vo;
        }).collect(Collectors.toList());

        return PageResult.of(pageResult.getTotal(), pageResult.getCurrent(), pageResult.getSize(), records);
    }

    /**
     * 获取嘉宾列表（分页）
     */
    public R<PageResult<GuestListVO>> listGuests(Long invitationId, Integer page, Integer size) {
        LambdaQueryWrapper<GuestReply> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GuestReply::getInvitationId, invitationId)
               .orderByDesc(GuestReply::getCreatedAt);
        IPage<GuestReply> pageResult = guestReplyMapper.selectPage(new Page<>(page, size), wrapper);

        List<GuestListVO> records = pageResult.getRecords().stream().map(r -> {
            GuestListVO vo = new GuestListVO();
            BeanUtils.copyProperties(r, vo);
            return vo;
        }).collect(Collectors.toList());

        PageResult<GuestListVO> result = PageResult.of(pageResult.getTotal(), pageResult.getCurrent(), pageResult.getSize(), records);
        return R.ok(result);
    }

    /**
     * 获取嘉宾回复详情
     */
    public R<GuestReplyVO> getReplyDetail(Long replyId) {
        GuestReply reply = guestReplyMapper.selectById(replyId);
        if (reply == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        GuestReplyVO vo = new GuestReplyVO();
        BeanUtils.copyProperties(reply, vo);
        return R.ok(vo);
    }

    @Override
    public void exportGuests(Long invitationId, HttpServletResponse response) {
        // P0-12: 添加邀请函所属用户校验，非创建者抛出权限异常
        Long currentUserId = LoginUser.getUserId();
        Invitation invitation = invitationMapper.selectById(invitationId);
        if (invitation == null) {
            throw new BusinessException(ResultCode.INVITATION_NOT_FOUND);
        }
        if (!invitation.getUserId().equals(currentUserId)) {
            throw new BusinessException(ResultCode.ACCESS_DENIED);
        }

        List<GuestReply> replies = guestReplyMapper.selectByInvitationId(invitationId);
        com.invitation.common.util.ExcelUtil excelUtil = new com.invitation.common.util.ExcelUtil();
        excelUtil.export(response, "宾客列表", "宾客", GuestReply.class, replies);
    }
}
