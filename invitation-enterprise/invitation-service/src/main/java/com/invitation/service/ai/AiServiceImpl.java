package com.invitation.service.ai;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.invitation.common.constant.RedisKeyConstant;
import com.invitation.common.enums.ResultCode;
import com.invitation.common.exception.BusinessException;
import com.invitation.common.model.LoginUser;
import com.invitation.common.model.R;
import com.invitation.infra.redis.RedisService;
import com.invitation.model.entity.Invitation;
import com.invitation.model.mapper.InvitationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * AI服务实现
 * 基于模板+规则的AI文案生成，可对接大模型API升级
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiServiceImpl implements AiService {

    private final RedisService redisService;
    private final InvitationMapper invitationMapper;

    @Value("${ai.enabled:false}")
    private boolean aiEnabled;

    @Value("${ai.api-key:}")
    private String apiKey;

    @Value("${ai.api-url:}")
    private String apiUrl;

    @Override
    public R<String> generateContent(String eventType, String theme, String style) {
        Long userId = LoginUser.getUserId();
        checkAiQuota(userId);

        String content = buildContentByTemplate(eventType, theme, style);

        consumeAiQuota(userId);
        log.info("AI生成文案: userId={}, eventType={}", userId, eventType);
        return R.ok(content);
    }

    @Override
    public R<String> optimizeContent(String originalContent, String optimizationDirection) {
        Long userId = LoginUser.getUserId();
        checkAiQuota(userId);

        String optimized = originalContent;

        switch (optimizationDirection) {
            case "formal":
                optimized = optimizeToFormal(originalContent);
                break;
            case "warm":
                optimized = optimizeToWarm(originalContent);
                break;
            case "creative":
                optimized = optimizeToCreative(originalContent);
                break;
            case "concise":
                optimized = optimizeToConcise(originalContent);
                break;
            default:
                optimized = originalContent;
        }

        consumeAiQuota(userId);
        log.info("AI优化文案: userId={}, direction={}", userId, optimizationDirection);
        return R.ok(optimized);
    }

    @Override
    public R<List<String>> generateBlessings(String eventType, String relationship, int count) {
        Long userId = LoginUser.getUserId();
        checkAiQuota(userId);

        List<String> blessings = new ArrayList<>();
        String[] templates = getBlessingTemplates(eventType, relationship);
        Random random = new Random();

        int genCount = Math.min(count, templates.length);
        Set<Integer> usedIndices = new HashSet<>();
        while (blessings.size() < genCount) {
            int idx = random.nextInt(templates.length);
            if (!usedIndices.contains(idx)) {
                usedIndices.add(idx);
                blessings.add(templates[idx]);
            }
        }

        consumeAiQuota(userId);
        log.info("AI生成祝福语: userId={}, count={}", userId, blessings.size());
        return R.ok(blessings);
    }

    @Override
    public R<List<Long>> recommendTemplates(Long userId, String eventType) {
        LambdaQueryWrapper<Invitation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Invitation::getUserId, userId)
               .orderByDesc(Invitation::getCreatedAt)
               .last("LIMIT 10");
        List<Invitation> recentInvitations = invitationMapper.selectList(wrapper);

        Set<Long> templateIds = new LinkedHashSet<>();
        for (Invitation inv : recentInvitations) {
            if (inv.getTemplateId() != null) {
                templateIds.add(inv.getTemplateId());
            }
        }

        templateIds.add(1L);
        templateIds.add(2L);
        templateIds.add(3L);

        log.info("AI推荐模板: userId={}, templates={}", userId, templateIds);
        return R.ok(new ArrayList<>(templateIds));
    }

    @Override
    public R<Map<String, Object>> smartLayout(Long invitationId) {
        Long userId = LoginUser.getUserId();
        checkAiQuota(userId);

        Invitation invitation = invitationMapper.selectById(invitationId);
        if (invitation == null || !invitation.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.INVITATION_NOT_FOUND);
        }

        Map<String, Object> layout = new HashMap<>();
        layout.put("titlePosition", "center");
        layout.put("titleSize", "large");
        layout.put("datePosition", "center");
        layout.put("dateSize", "medium");
        layout.put("addressPosition", "center");
        layout.put("addressSize", "small");
        layout.put("coverStyle", "full");
        layout.put("backgroundColor", "#ffffff");
        layout.put("textColor", "#333333");
        layout.put("accentColor", "#e74c3c");
        layout.put("fontFamily", "serif");

        if (invitation.getActivityType() != null) {
            switch (invitation.getActivityType()) {
                case 1:
                    layout.put("accentColor", "#e74c3c");
                    layout.put("fontFamily", "serif");
                    layout.put("coverStyle", "romantic");
                    break;
                case 2:
                    layout.put("accentColor", "#f39c12");
                    layout.put("fontFamily", "sans-serif");
                    layout.put("coverStyle", "cheerful");
                    break;
                case 3:
                    layout.put("accentColor", "#2980b9");
                    layout.put("fontFamily", "sans-serif");
                    layout.put("coverStyle", "professional");
                    break;
                default:
                    break;
            }
        }

        consumeAiQuota(userId);
        log.info("AI智能排版: userId={}, invitationId={}", userId, invitationId);
        return R.ok(layout);
    }

    @Override
    public R<String> generateCoverDescription(String eventType, String theme) {
        Long userId = LoginUser.getUserId();
        checkAiQuota(userId);

        String description;
        switch (eventType != null ? eventType : "other") {
            case "1":
            case "wedding":
                description = "浪漫温馨的婚礼场景，鲜花环绕，柔和光线，" + (theme != null ? theme : "经典白色") + "主题";
                break;
            case "2":
            case "birthday":
                description = "欢快活泼的生日派对氛围，彩色气球与彩带，" + (theme != null ? theme : "缤纷") + "主题";
                break;
            case "3":
            case "business":
                description = "商务大气的会议场景，简洁现代设计，" + (theme != null ? theme : "蓝色商务") + "主题";
                break;
            default:
                description = "优雅大方的邀请函封面，精致设计，" + (theme != null ? theme : "简约") + "主题";
        }

        consumeAiQuota(userId);
        return R.ok(description);
    }

    private void checkAiQuota(Long userId) {
        String quotaKey = RedisKeyConstant.AI_QUOTA + userId;
        Integer used = redisService.get(quotaKey, Integer.class);
        int maxQuota = 10;
        if (used != null && used >= maxQuota) {
            throw new BusinessException(ResultCode.AI_QUOTA_EXCEEDED);
        }
    }

    private void consumeAiQuota(Long userId) {
        String quotaKey = RedisKeyConstant.AI_QUOTA + userId;
        redisService.increment(quotaKey);
        redisService.expire(quotaKey, 1, TimeUnit.DAYS);
    }

    private String buildContentByTemplate(String eventType, String theme, String style) {
        StringBuilder sb = new StringBuilder();

        switch (eventType != null ? eventType : "other") {
            case "1":
            case "wedding":
                sb.append("我们即将步入婚姻的殿堂，诚挚地邀请您见证我们的幸福时刻。\n\n");
                sb.append("在这个特别的日子里，您的到来将是我们最珍贵的礼物。\n\n");
                sb.append("期待与您共享这份喜悦与感动。");
                break;
            case "2":
            case "birthday":
                sb.append("时光荏苒，又到了一年中最特别的日子。\n\n");
                sb.append("诚挚邀请您来参加生日派对，一起分享快乐时光！\n\n");
                sb.append("有您的陪伴，这个生日将更加精彩。");
                break;
            case "3":
            case "business":
                sb.append("诚挚地邀请您出席本次活动。\n\n");
                sb.append("我们期待与您共聚一堂，探讨合作与发展。\n\n");
                sb.append("您的莅临将是我们莫大的荣幸。");
                break;
            default:
                sb.append("诚挚地邀请您参加我们的活动。\n\n");
                sb.append("期待您的到来，与我们共同度过美好时光！\n\n");
                sb.append("您的参与将使活动更加精彩。");
        }

        if (theme != null && !theme.isEmpty()) {
            sb.append("\n\n主题：").append(theme);
        }

        return sb.toString();
    }

    private String optimizeToFormal(String content) {
        return content.replaceAll("你", "您")
                      .replaceAll("咱们", "我们")
                      .replaceAll("来吧", "敬请光临")
                      .replaceAll("一起", "共同");
    }

    private String optimizeToWarm(String content) {
        return content.replaceAll("诚挚邀请", "真心期待")
                      .replaceAll("莅临", "到来")
                      .replaceAll("荣幸", "开心");
    }

    private String optimizeToCreative(String content) {
        return "✨ " + content + " ✨\n\n— 每一个瞬间，都值得被铭记 —";
    }

    private String optimizeToConcise(String content) {
        String[] lines = content.split("\n");
        StringBuilder sb = new StringBuilder();
        for (String line : lines) {
            if (!line.trim().isEmpty()) {
                sb.append(line.trim()).append("\n");
            }
        }
        return sb.toString().trim();
    }

    private String[] getBlessingTemplates(String eventType, String relationship) {
        switch (eventType != null ? eventType : "other") {
            case "1":
            case "wedding":
                return new String[]{
                    "百年好合，永结同心！",
                    "愿你们的爱情如美酒般越陈越香。",
                    "执子之手，与子偕老。祝新婚快乐！",
                    "愿你们的婚姻生活甜蜜幸福，相濡以沫。",
                    "琴瑟和鸣，白头偕老！",
                    "愿爱情之花永远绽放，幸福之路永远相伴。",
                    "祝你们携手共度每一个美好时光！",
                    "愿你们的未来充满爱与温馨。"
                };
            case "2":
            case "birthday":
                return new String[]{
                    "生日快乐！愿所有美好如期而至！",
                    "祝你年年有今日，岁岁有今朝！",
                    "愿你心想事成，万事如意！",
                    "年龄只是数字，快乐才是真理！",
                    "愿你永远年轻，永远热泪盈眶！",
                    "祝生日快乐，笑口常开！",
                    "愿新的一岁，你成为更好的自己！",
                    "生命因你而精彩，生日快乐！"
                };
            case "3":
            case "business":
                return new String[]{
                    "祝事业蒸蒸日上，再创辉煌！",
                    "愿合作愉快，共铸辉煌！",
                    "祝万事顺意，前程似锦！",
                    "携手共进，共创未来！",
                    "祝活动圆满成功！",
                    "愿我们合作共赢，共同发展！"
                };
            default:
                return new String[]{
                    "万事如意，心想事成！",
                    "愿你一切顺利，幸福安康！",
                    "祝生活美满，事业有成！",
                    "愿每一天都充满阳光与希望！",
                    "真诚的祝福送给最特别的你！",
                    "愿美好常伴左右！"
                };
        }
    }
}
