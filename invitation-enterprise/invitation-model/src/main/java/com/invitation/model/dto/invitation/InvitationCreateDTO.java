package com.invitation.model.dto.invitation;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class InvitationCreateDTO {
    @NotBlank(message = "标题不能为空")
    private String title;
    @NotNull(message = "活动类型不能为空")
    private Integer activityType;
    @NotNull(message = "模板ID不能为空")
    private Long templateId;
    private String coverImage;
    private String content;
    private Date activityDate;
    private String activityAddress;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Long musicId;
    private String customMusicUrl;
    private String videoUrl;
    private String aiGreeting;
    private String extraData;
}
