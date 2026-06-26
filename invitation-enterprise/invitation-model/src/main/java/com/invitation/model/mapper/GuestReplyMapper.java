package com.invitation.model.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.invitation.model.entity.GuestReply;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface GuestReplyMapper extends BaseMapper<GuestReply> {
    List<GuestReply> selectByInvitationId(@Param("invitationId") Long invitationId);
    int countByInvitationId(@Param("invitationId") Long invitationId);
    int countAttendByInvitationId(@Param("invitationId") Long invitationId);
}
