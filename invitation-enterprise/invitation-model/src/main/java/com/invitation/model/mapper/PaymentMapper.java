package com.invitation.model.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.invitation.model.entity.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PaymentMapper extends BaseMapper<Payment> {
    default Payment selectByOrderNo(@Param("orderNo") String orderNo) {
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Payment> wrapper =
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        wrapper.eq(Payment::getOrderNo, orderNo).last("LIMIT 1");
        return selectOne(wrapper);
    }
}
