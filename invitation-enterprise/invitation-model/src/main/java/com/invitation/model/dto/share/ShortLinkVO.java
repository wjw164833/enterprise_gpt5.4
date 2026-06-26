package com.invitation.model.dto.share;

import lombok.Data;

@Data
public class ShortLinkVO {
    private Long id;
    private String shortCode;
    private String shortUrl;
    private String originalUrl;
    private Integer clickCount;
}
