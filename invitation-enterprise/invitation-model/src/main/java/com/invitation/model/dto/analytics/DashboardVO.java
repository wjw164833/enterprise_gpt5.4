package com.invitation.model.dto.analytics;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class DashboardVO {
    private Long totalInvitations;
    private Long publishedInvitations;
    private Long totalReplies;
    private Long attendCount;
    private Long totalBlessings;
    private Long totalViews;
    private BigDecimal totalGiftAmount;

    @Data
    public static class InvitationStats {
        private Long invitationId;
        private String title;
        private Integer pvCount;
        private Integer uvCount;
        private Integer replyCount;
        private Integer attendCount;
        private Integer blessCount;
        private Long uncertainCount;
        private Long notAttendCount;
    }

    @Data
    public static class TrendData {
        private List<String> dates;
        private List<Long> viewCounts;
        private List<Long> replyCounts;
    }

    @Data
    public static class SourceDistribution {
        private Map<String, Long> sources;
    }
}
