package com.invitation.service.bless;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.invitation.common.model.PageResult;
import com.invitation.model.entity.BlessMessage;
import com.invitation.model.mapper.BlessMessageMapper;
import com.invitation.model.mapper.InvitationMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class BlessMessageServiceImpl implements BlessMessageService {

    @Autowired
    private BlessMessageMapper blessMessageMapper;
    @Autowired
    private InvitationMapper invitationMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BlessMessage create(Long invitationId, String guestName, String guestAvatar, String content, String theme, String ip) {
        BlessMessage message = new BlessMessage();
        message.setInvitationId(invitationId);
        message.setGuestName(guestName);
        message.setGuestAvatar(guestAvatar);
        message.setContent(content);
        message.setTheme(theme);
        message.setIpAddress(ip);
        message.setStatus(1);
        blessMessageMapper.insert(message);

        invitationMapper.incrementBlessCount(invitationId);
        return message;
    }

    @Override
    public PageResult<BlessMessage> list(Long invitationId, int page, int size) {
        LambdaQueryWrapper<BlessMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BlessMessage::getInvitationId, invitationId)
               .eq(BlessMessage::getStatus, 1)
               .orderByDesc(BlessMessage::getCreatedAt);
        IPage<BlessMessage> pageResult = blessMessageMapper.selectPage(new Page<>(page, size), wrapper);
        return PageResult.of(pageResult.getTotal(), pageResult.getCurrent(), pageResult.getSize(), pageResult.getRecords());
    }

    @Override
    public void delete(Long userId, Long id) {
        BlessMessage msg = blessMessageMapper.selectById(id);
        if (msg != null) {
            msg.setStatus(0);
            blessMessageMapper.updateById(msg);
        }
    }
}
