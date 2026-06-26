package com.invitation.web.controller;

import com.invitation.common.model.R;
import com.invitation.service.ai.AiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * AI服务控制器
 */
@Tag(name = "AI服务")
@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;

    @Operation(summary = "AI生成邀请函文案")
    @PostMapping("/content/generate")
    public R<String> generateContent(@RequestParam String eventType,
                                      @RequestParam(required = false) String theme,
                                      @RequestParam(required = false) String style) {
        return aiService.generateContent(eventType, theme, style);
    }

    @Operation(summary = "AI优化邀请函文案")
    @PostMapping("/content/optimize")
    public R<String> optimizeContent(@RequestParam String originalContent,
                                      @RequestParam String optimizationDirection) {
        return aiService.optimizeContent(originalContent, optimizationDirection);
    }

    @Operation(summary = "AI生成祝福语")
    @GetMapping("/blessings")
    public R<List<String>> generateBlessings(@RequestParam String eventType,
                                              @RequestParam(required = false) String relationship,
                                              @RequestParam(defaultValue = "5") int count) {
        return aiService.generateBlessings(eventType, relationship, count);
    }

    @Operation(summary = "AI推荐模板")
    @GetMapping("/templates/recommend")
    public R<List<Long>> recommendTemplates(@RequestParam(required = false) String eventType) {
        return aiService.recommendTemplates(null, eventType);
    }

    @Operation(summary = "AI智能排版")
    @PostMapping("/layout/{invitationId}")
    public R<Map<String, Object>> smartLayout(@PathVariable Long invitationId) {
        return aiService.smartLayout(invitationId);
    }

    @Operation(summary = "AI生成封面描述")
    @PostMapping("/cover/description")
    public R<String> generateCoverDescription(@RequestParam String eventType,
                                               @RequestParam(required = false) String theme) {
        return aiService.generateCoverDescription(eventType, theme);
    }
}
