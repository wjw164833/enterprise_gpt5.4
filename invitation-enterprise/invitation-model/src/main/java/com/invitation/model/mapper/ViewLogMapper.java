package com.invitation.model.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.invitation.model.entity.ViewLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ViewLogMapper extends BaseMapper<ViewLog> {
    int batchInsert(@Param("list") List<ViewLog> list);
    int countPvByInvitationAndDate(@Param("invitationId") Long invitationId, @Param("viewDate") String viewDate);
}
