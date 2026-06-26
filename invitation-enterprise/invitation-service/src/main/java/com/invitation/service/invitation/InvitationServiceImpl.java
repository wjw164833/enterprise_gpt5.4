package com.invitation.service.invitation;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.invitation.common.constant.RedisKeyConstant;
import com.invitation.common.enums.InvitationStatus;
import com.invitation.common.enums.ResultCode;
import com.invitation.common.exception.BusinessException;
import com.invitation.common.model.LoginUser;
import com.invitation.common.model.PageResult;
import com.invitation.common.util.ShortUrlUtil;
import com.invitation.infra.redis.RedisService;
import com.invitation.model.dto.invitation.*;
import com.invitation.model.entity.Invitation;
import com.invitation.model.entity.InvitationVersion;
import com.invitation.model.entity.Subscription;
import com.invitation.model.mapper.InvitationMapper;
import com.invitation.model.mapper.InvitationVersionMapper;
import com.invitation.model.mapper.SubscriptionMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
public class InvitationServiceImpl implements InvitationService {

    @Autowired
    private InvitationMapper invitationMapper;
    @Autowired
    private InvitationVersionMapper versionMapper;
    @Autowired
    private SubscriptionMapper subscriptionMapper;
    @Autowired
    private RedisService redisService;
    @Autowired
    private ShortUrlUtil shortUrlUtil;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @Transactional(rollbackFor = Exception.class)
    public InvitationDetailVO create(Long userId, InvitationCreateDTO dto) {
        // 检查配额
        Subscription sub = subscriptionMapper.selectActiveByUserId(userId);
        if (sub == null || !sub.canCreateInvitation()) {
            throw new BusinessException(ResultCode.INVITATION_QUOTA_EXCEEDED);
        }

        Invitation invitation = new Invitation();
        BeanUtils.copyProperties(dto, invitation);
        invitation.setUserId(userId);
        invitation.setStatus(InvitationStatus.DRAFT.getCode());
        invitation.setPv(0);
        invitation.setUv(0);
        invitation.setReplyCount(0);
        invitation.setAttendCount(0);
        invitation.setBlessCount(0);
        invitation.setGiftAmount(java.math.BigDecimal.ZERO);
        invitation.setWatermark(1);
        invitationMapper.insert(invitation);

        // 更新已使用配额
        if (sub != null) {
            sub.setInvitationUsed(sub.getInvitationUsed() + 1);
            subscriptionMapper.updateById(sub);
        }

        return convertToDetailVO(invitation);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public InvitationDetailVO update(Long userId, Long id, InvitationUpdateDTO dto) {
        Invitation invitation = getAndValidateOwner(userId, id);
        // 保存版本快照
        saveVersion(invitation, "更新邀请函");
        // 更新字段
        if (dto.getTitle() != null) invitation.setTitle(dto.getTitle());
        if (dto.getActivityType() != null) invitation.setActivityType(dto.getActivityType());
        if (dto.getTemplateId() != null) invitation.setTemplateId(dto.getTemplateId());
        if (dto.getCoverImage() != null) invitation.setCoverImage(dto.getCoverImage());
        if (dto.getContent() != null) invitation.setContent(dto.getContent());
        if (dto.getActivityDate() != null) invitation.setActivityDate(dto.getActivityDate());
        if (dto.getActivityAddress() != null) invitation.setActivityAddress(dto.getActivityAddress());
        if (dto.getLatitude() != null) invitation.setLatitude(dto.getLatitude());
        if (dto.getLongitude() != null) invitation.setLongitude(dto.getLongitude());
        if (dto.getMusicId() != null) invitation.setMusicId(dto.getMusicId());
        if (dto.getCustomMusicUrl() != null) invitation.setCustomMusicUrl(dto.getCustomMusicUrl());
        if (dto.getVideoUrl() != null) invitation.setVideoUrl(dto.getVideoUrl());
        if (dto.getAiGreeting() != null) invitation.setAiGreeting(dto.getAiGreeting());
        if (dto.getExtraData() != null) invitation.setExtraData(dto.getExtraData());
        if (dto.getOgTitle() != null) invitation.setOgTitle(dto.getOgTitle());
        if (dto.getOgDescription() != null) invitation.setOgDescription(dto.getOgDescription());
        if (dto.getOgImage() != null) invitation.setOgImage(dto.getOgImage());
        invitationMapper.updateById(invitation);

        // 清除缓存
        redisService.delete(RedisKeyConstant.INVITATION_DETAIL + id);

        return convertToDetailVO(invitation);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long userId, Long id) {
        Invitation invitation = getAndValidateOwner(userId, id);
        invitationMapper.deleteById(id);
        redisService.delete(RedisKeyConstant.INVITATION_DETAIL + id);
    }

    @Override
    public InvitationDetailVO getDetail(Long id) {
        // 尝试从缓存获取
        String cacheKey = RedisKeyConstant.INVITATION_DETAIL + id;
        Object cached = redisService.get(cacheKey);
        if (cached != null) {
            try {
                return objectMapper.readValue(cached.toString(), InvitationDetailVO.class);
            } catch (Exception e) {
                log.warn("缓存反序列化失败: {}", e.getMessage());
            }
        }
        Invitation invitation = invitationMapper.selectById(id);
        if (invitation == null) {
            throw new BusinessException(ResultCode.INVITATION_NOT_FOUND);
        }
        InvitationDetailVO vo = convertToDetailVO(invitation);
        try {
            redisService.set(cacheKey, objectMapper.writeValueAsString(vo), RedisKeyConstant.DETAIL_CACHE_TTL, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.warn("缓存序列化失败: {}", e.getMessage());
        }
        return vo;
    }

    @Override
    public InvitationDetailVO getByShortCode(String shortCode) {
        Invitation invitation = invitationMapper.selectByShortCode(shortCode);
        if (invitation == null) {
            throw new BusinessException(ResultCode.INVITATION_NOT_FOUND);
        }
        return convertToDetailVO(invitation);
    }

    @Override
    public PageResult<InvitationListVO> listMine(Long userId, InvitationQueryDTO query) {
        LambdaQueryWrapper<Invitation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Invitation::getUserId, userId);
        if (query.getActivityType() != null) {
            wrapper.eq(Invitation::getActivityType, query.getActivityType());
        }
        if (query.getStatus() != null) {
            wrapper.eq(Invitation::getStatus, query.getStatus());
        }
        if (query.getKeyword() != null && !query.getKeyword().isEmpty()) {
            wrapper.like(Invitation::getTitle, query.getKeyword());
        }
        wrapper.orderByDesc(Invitation::getCreatedAt);

        IPage<Invitation> page = invitationMapper.selectPage(
                new Page<>(query.getPage(), query.getSize()), wrapper);

        List<InvitationListVO> records = page.getRecords().stream()
                .map(this::convertToListVO)
                .collect(Collectors.toList());

        return PageResult.of(page.getTotal(), page.getCurrent(), page.getSize(), records);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void publish(Long userId, Long id) {
        Invitation invitation = getAndValidateOwner(userId, id);
        if (invitation.getStatus() != InvitationStatus.DRAFT.getCode()
                && invitation.getStatus() != InvitationStatus.UNPUBLISHED.getCode()) {
            throw new BusinessException(ResultCode.INVITATION_STATUS_ERROR);
        }
        // 生成短链码
        if (invitation.getShortCode() == null) {
            invitation.setShortCode(shortUrlUtil.generateShortCode());
        }
        invitation.setStatus(InvitationStatus.PUBLISHED.getCode());
        invitation.setPublishedAt(new Date());
        invitationMapper.updateById(invitation);
        redisService.delete(RedisKeyConstant.INVITATION_DETAIL + id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unpublish(Long userId, Long id) {
        Invitation invitation = getAndValidateOwner(userId, id);
        if (invitation.getStatus() != InvitationStatus.PUBLISHED.getCode()) {
            throw new BusinessException(ResultCode.INVITATION_STATUS_ERROR);
        }
        invitation.setStatus(InvitationStatus.UNPUBLISHED.getCode());
        invitationMapper.updateById(invitation);
        redisService.delete(RedisKeyConstant.INVITATION_DETAIL + id);
    }

    @Override
    public List<InvitationVersion> getVersions(Long invitationId) {
        LambdaQueryWrapper<InvitationVersion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InvitationVersion::getInvitationId, invitationId)
               .orderByDesc(InvitationVersion::getVersionNo);
        return versionMapper.selectList(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rollbackVersion(Long userId, Long invitationId, Long versionId) {
        getAndValidateOwner(userId, invitationId);
        InvitationVersion version = versionMapper.selectById(versionId);
        if (version == null || !version.getInvitationId().equals(invitationId)) {
            throw new BusinessException(ResultCode.PARAM_ERROR);
        }
        try {
            Invitation snapshot = objectMapper.readValue(version.getSnapshot(), Invitation.class);
            snapshot.setId(invitationId);
            invitationMapper.updateById(snapshot);
            redisService.delete(RedisKeyConstant.INVITATION_DETAIL + invitationId);
        } catch (Exception e) {
            throw new BusinessException("版本回滚失败");
        }
    }

    @Override
    public void incrementView(Long invitationId, String visitorId, String ip, String ua, String referer, String source) {
        // Redis PV计数
        invitationMapper.incrementPv(invitationId);
        // HyperLogLog UV
        String uvKey = RedisKeyConstant.UV + invitationId + ":" + new java.text.SimpleDateFormat("yyyy-MM-dd").format(new Date());
        redisService.pfAdd(uvKey, visitorId != null ? visitorId : ip);
        Long uvCount = redisService.pfCount(uvKey);
        // 异步写浏览日志（通过MQ）
        log.debug("浏览量记录: invitationId={}, visitorId={}", invitationId, visitorId);
    }

    /** 校验邀请函所有者 */
    private Invitation getAndValidateOwner(Long userId, Long invitationId) {
        Invitation invitation = invitationMapper.selectById(invitationId);
        if (invitation == null) {
            throw new BusinessException(ResultCode.INVITATION_NOT_FOUND);
        }
        if (!invitation.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.ACCESS_DENIED);
        }
        return invitation;
    }

    /** 保存版本快照 */
    private void saveVersion(Invitation invitation, String desc) {
        try {
            LambdaQueryWrapper<InvitationVersion> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(InvitationVersion::getInvitationId, invitation.getId())
                   .orderByDesc(InvitationVersion::getVersionNo);
            List<InvitationVersion> versions = versionMapper.selectList(wrapper);
            int nextVersion = versions.isEmpty() ? 1 : versions.get(0).getVersionNo() + 1;

            InvitationVersion version = new InvitationVersion();
            version.setInvitationId(invitation.getId());
            version.setVersionNo(nextVersion);
            version.setSnapshot(objectMapper.writeValueAsString(invitation));
            version.setChangeDesc(desc);
            versionMapper.insert(version);
        } catch (Exception e) {
            log.warn("保存版本快照失败: {}", e.getMessage());
        }
    }

    private InvitationDetailVO convertToDetailVO(Invitation inv) {
        InvitationDetailVO vo = new InvitationDetailVO();
        BeanUtils.copyProperties(inv, vo);
        return vo;
    }

    private InvitationListVO convertToListVO(Invitation inv) {
        InvitationListVO vo = new InvitationListVO();
        BeanUtils.copyProperties(inv, vo);
        return vo;
    }
}
