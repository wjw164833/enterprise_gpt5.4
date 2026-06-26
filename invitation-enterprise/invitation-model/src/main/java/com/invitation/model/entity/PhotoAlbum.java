package com.invitation.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.util.Date;

@Data
@TableName("photo_album")
public class PhotoAlbum {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long invitationId;
    private String imageUrl;
    private String thumbnailUrl;
    private Integer sortOrder;
    @TableField(exist = false)
    private String description;
    @TableField(fill = FieldFill.INSERT)
    private Date createdAt;
    @TableLogic
    private Integer deleted;

    public String getPhotoUrl() {
        return imageUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.imageUrl = photoUrl;
    }
}
