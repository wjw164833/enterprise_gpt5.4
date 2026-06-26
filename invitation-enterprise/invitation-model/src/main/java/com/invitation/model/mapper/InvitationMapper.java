package com.invitation.model.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.invitation.model.entity.Invitation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface InvitationMapper extends BaseMapper<Invitation> {
    Invitation selectByShortCode(@Param("shortCode") String shortCode);
    List<Invitation> selectByUserId(@Param("userId") Long userId);
    int incrementPv(@Param("id") Long id);
    int incrementUv(@Param("id") Long id);
    int incrementReplyCount(@Param("id") Long id);
    int incrementAttendCount(@Param("id") Long id);
    int incrementBlessCount(@Param("id") Long id);
}
