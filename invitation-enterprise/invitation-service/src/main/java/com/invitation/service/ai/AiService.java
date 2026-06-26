package com.invitation.service.ai;

import com.invitation.common.model.R;
import com.invitation.model.entity.Invitation;

import java.util.List;
import java.util.Map;

/**
 * AI服务接口
 */
public interface AiService {

    /**
     * AI生成邀请函文案
     */
    R<String> generateContent(String eventType, String theme, String style);

    /**
     * AI优化邀请函文案
     */
    R<String> optimizeContent(String originalContent, String optimizationDirection);

    /**
     * AI生成祝福语
     */
    R<List<String>> generateBlessings(String eventType, String relationship, int count);

    /**
     * AI推荐模板
     */
    R<List<Long>> recommendTemplates(Long userId, String eventType);

    /**
     * AI智能排版
     */
    R<Map<String, Object>> smartLayout(Long invitationId);

    /**
     * AI生成邀请函封面描述
     */
    R<String> generateCoverDescription(String eventType, String theme);
}
