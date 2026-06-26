package com.invitation.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.util.Date;

@Data
@TableName("user")
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String phone;
    private String nickname;
    private String avatar;
    private String password;
    private String wxOpenid;
    private String wxUnionid;
    private String wxMpOpenid;
    private Integer userType;
    private String enterpriseName;
    private String enterpriseLogo;
    private Integer status;
    private Date lastLoginTime;
    private String lastLoginIp;
    @TableField(fill = FieldFill.INSERT)
    private Date createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updatedAt;
    @TableLogic
    private Integer deleted;
}
