package com.invitation.model.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.invitation.model.entity.Subscription;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SubscriptionMapper extends BaseMapper<Subscription> {
    Subscription selectActiveByUserId(@Param("userId") Long userId);
}
