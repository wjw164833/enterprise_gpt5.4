package com.invitation.model.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.invitation.model.entity.BlessMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface BlessMessageMapper extends BaseMapper<BlessMessage> {
    List<BlessMessage> selectByInvitationId(@Param("invitationId") Long invitationId);
    int countByInvitationId(@Param("invitationId") Long invitationId);
}
