package com.invitation.model.dto.invitation;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class InvitationUpdateDTO {
    private String title;
    private Integer activityType;
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
    private String ogTitle;
    private String ogDescription;
    private String ogImage;
}
