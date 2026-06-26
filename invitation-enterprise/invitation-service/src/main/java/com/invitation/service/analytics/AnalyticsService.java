package com.invitation.service.analytics;

import com.invitation.common.model.R;
import com.invitation.model.dto.analytics.AnalyticsEventDTO;
import com.invitation.model.dto.analytics.DashboardVO;

/**
 * 数据分析服务接口
 */
public interface AnalyticsService {

    /**
     * 记录事件
     */
    R<Void> trackEvent(AnalyticsEventDTO dto);

    /**
     * 获取仪表盘数据
     */
    R<DashboardVO> getDashboard(Long userId);

    /**
     * 获取邀请函统计数据
     */
    R<DashboardVO.InvitationStats> getInvitationStats(Long invitationId);

    /**
     * 获取趋势数据
     */
    R<DashboardVO.TrendData> getTrendData(Long invitationId, String period);

    /**
     * 获取访客来源分布
     */
    R<DashboardVO.SourceDistribution> getSourceDistribution(Long invitationId);
}
