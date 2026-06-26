package com.invitation.web.controller;

import com.invitation.common.model.R;
import com.invitation.model.dto.analytics.AnalyticsEventDTO;
import com.invitation.model.dto.analytics.DashboardVO;
import com.invitation.service.analytics.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 数据分析控制器
 */
@Tag(name = "数据分析")
@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @Operation(summary = "记录事件")
    @PostMapping("/track")
    public R<Void> trackEvent(@RequestBody AnalyticsEventDTO dto) {
        return analyticsService.trackEvent(dto);
    }

    @Operation(summary = "获取仪表盘数据")
    @GetMapping("/dashboard")
    public R<DashboardVO> getDashboard() {
        return analyticsService.getDashboard(null);
    }

    @Operation(summary = "获取邀请函统计数据")
    @GetMapping("/stats/{invitationId}")
    public R<DashboardVO.InvitationStats> getInvitationStats(@PathVariable Long invitationId) {
        return analyticsService.getInvitationStats(invitationId);
    }

    @Operation(summary = "获取趋势数据")
    @GetMapping("/trend/{invitationId}")
    public R<DashboardVO.TrendData> getTrendData(@PathVariable Long invitationId,
                                                  @RequestParam(defaultValue = "week") String period) {
        return analyticsService.getTrendData(invitationId, period);
    }

    @Operation(summary = "获取访客来源分布")
    @GetMapping("/source/{invitationId}")
    public R<DashboardVO.SourceDistribution> getSourceDistribution(@PathVariable Long invitationId) {
        return analyticsService.getSourceDistribution(invitationId);
    }
}
