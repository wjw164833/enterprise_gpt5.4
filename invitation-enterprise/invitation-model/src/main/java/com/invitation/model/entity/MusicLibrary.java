package com.invitation.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.util.Date;

@Data
@TableName("music_library")
public class MusicLibrary {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String artist;
    private String url;
    private String coverUrl;
    private Integer duration;
    private String category;
    private Integer sortOrder;
    private Integer status;
    @TableField(fill = FieldFill.INSERT)
    private Date createdAt;
    @TableLogic
    private Integer deleted;
}
