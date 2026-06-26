package com.invitation.service.auth;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.invitation.common.constant.BusinessConstant;
import com.invitation.common.constant.MqConstant;
import com.invitation.common.constant.RedisKeyConstant;
import com.invitation.common.enums.ResultCode;
import com.invitation.common.exception.BusinessException;
import com.invitation.common.model.LoginUser;
import com.invitation.common.model.R;
import com.invitation.common.util.*;
import com.invitation.infra.mq.MessageSender;
import com.invitation.infra.redis.RedisService;
import com.invitation.infra.security.JwtTokenProvider;
import com.invitation.model.dto.auth.SmsLoginDTO;
import com.invitation.model.dto.auth.TokenDTO;
import com.invitation.model.dto.auth.WxLoginDTO;
import com.invitation.model.entity.SmsLog;
import com.invitation.model.entity.Subscription;
import com.invitation.model.entity.User;
import com.invitation.model.mapper.SmsLogMapper;
import com.invitation.model.mapper.SubscriptionMapper;
import com.invitation.model.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 认证服务实现
 */
@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private SmsLogMapper smsLogMapper;
    @Autowired
    private SubscriptionMapper subscriptionMapper;
    @Autowired
    private RedisService redisService;
    @Autowired
    private SmsUtil smsUtil;
    @Autowired
    private AesUtil aesUtil;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private MessageSender messageSender;
    @Autowired
    private IpUtil ipUtil;
    // P1-01: 注入WxMaService用于获取真实openid
    @Autowired
    private WxMaUtil wxMaUtil;
    // P1-02: 注入WxMpUtil用于获取真实扫码URL
    @Autowired
    private WxMpUtil wxMpUtil;

    @Value("${wx.mp.appid:}")
    private String mpAppId;

    @Override
    public R<Void> sendSmsCode(String phone, String ip) {
        // 检查手机号维度限流（60秒内不能重发）
        String limitKey = RedisKeyConstant.SMS_LIMIT + phone;
        if (Boolean.TRUE.equals(redisService.hasKey(limitKey))) {
            throw new BusinessException(ResultCode.SMS_SEND_LIMIT);
        }
        // 检查IP维度限流（1小时内最多10次）
        String ipLimitKey = RedisKeyConstant.SMS_IP_LIMIT + ip;
        Integer ipCount = (Integer) redisService.get(ipLimitKey);
        if (ipCount != null && ipCount >= 10) {
            throw new BusinessException(ResultCode.SMS_SEND_LIMIT);
        }

        // 生成验证码
        String code = smsUtil.generateCode();

        // 存入Redis
        redisService.set(RedisKeyConstant.SMS_CODE + phone, code, RedisKeyConstant.SMS_CODE_TTL, TimeUnit.SECONDS);
        redisService.set(limitKey, "1", RedisKeyConstant.SMS_LIMIT_TTL, TimeUnit.SECONDS);
        if (ipCount == null) {
            redisService.set(ipLimitKey, 1, RedisKeyConstant.SMS_IP_LIMIT_TTL, TimeUnit.SECONDS);
        } else {
            redisService.increment(ipLimitKey);
        }

        // 记录短信日志
        SmsLog smsLog = new SmsLog();
        smsLog.setPhone(phone);
        smsLog.setCode(code);
        smsLog.setBizType("login");
        smsLog.setStatus(0);
        smsLog.setIpAddress(ip);
        smsLogMapper.insert(smsLog);

        // 异步发送短信
        Map<String, String> msg = new HashMap<>(4);
        msg.put("phone", phone);
        msg.put("code", code);
        msg.put("smsLogId", String.valueOf(smsLog.getId()));
        messageSender.send(MqConstant.EXCHANGE_SMS, MqConstant.RK_SMS_SEND, msg);

        log.info("验证码已发送: phone={}", phone);
        return R.ok();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TokenDTO smsLogin(SmsLoginDTO dto, String ip) {
        // 校验验证码
        String codeKey = RedisKeyConstant.SMS_CODE + dto.getPhone();
        String cachedCode = redisService.getString(codeKey);
        if (cachedCode == null) {
            throw new BusinessException(ResultCode.SMS_CODE_EXPIRED);
        }
        if (!cachedCode.equals(dto.getCode())) {
            throw new BusinessException(ResultCode.SMS_CODE_INVALID);
        }
        // 删除已使用的验证码
        redisService.delete(codeKey);

        // 加密手机号查询
        String encryptedPhone = aesUtil.encrypt(dto.getPhone());
        User user = userMapper.selectByPhone(encryptedPhone);

        // 新用户自动注册
        boolean isNewUser = false;
        if (user == null) {
            user = new User();
            user.setPhone(encryptedPhone);
            user.setNickname("用户" + dto.getPhone().substring(7));
            user.setUserType(1);
            user.setStatus(1);
            user.setLastLoginIp(ip);
            user.setLastLoginTime(new Date());
            userMapper.insert(user);
            isNewUser = true;
            log.info("新用户注册: phone={}, userId={}", dto.getPhone(), user.getId());
        } else {
            if (user.getStatus() == 0) {
                throw new BusinessException(ResultCode.USER_DISABLED);
            }
            user.setLastLoginIp(ip);
            user.setLastLoginTime(new Date());
            userMapper.updateById(user);
        }

        // 新用户初始化免费订阅
        if (isNewUser) {
            initFreeSubscription(user.getId());
        }

        // 生成Token
        return generateAndCacheToken(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TokenDTO wxMiniLogin(WxLoginDTO dto, String ip) {
        try {
            // P1-01: 使用WxMaService获取真实openid，替代硬编码stub
            cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult session =
                    wxMaUtil.jsCode2Session(dto.getCode());
            String openid;
            if (session != null && session.getOpenid() != null) {
                openid = session.getOpenid();
            } else {
                // 如果微信服务未配置或获取失败，记录警告并抛出异常
                log.error("微信小程序登录失败: 无法获取openid, code={}", dto.getCode());
                throw new BusinessException(ResultCode.WX_LOGIN_FAIL);
            }

            User user = userMapper.selectByWxOpenid(openid);

            if (user == null) {
                user = new User();
                user.setWxOpenid(openid);
                user.setNickname("微信用户");
                user.setPhone("");
                user.setUserType(1);
                user.setStatus(1);
                user.setLastLoginIp(ip);
                user.setLastLoginTime(new Date());
                userMapper.insert(user);
                initFreeSubscription(user.getId());
            } else {
                if (user.getStatus() == 0) {
                    throw new BusinessException(ResultCode.USER_DISABLED);
                }
                user.setLastLoginIp(ip);
                user.setLastLoginTime(new Date());
                userMapper.updateById(user);
            }

            return generateAndCacheToken(user);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("微信小程序登录失败: {}", e.getMessage(), e);
            throw new BusinessException(ResultCode.WX_LOGIN_FAIL);
        }
    }

    @Override
    public Map<String, String> getWxScanQrCode() {
        String state = UUID.randomUUID().toString().replace("-", "");
        // P1-02: 使用注入的真实appid替代占位符
        String qrCodeUrl = "https://open.weixin.qq.com/connect/qrconnect?appid=" + mpAppId + "&state=" + state;
        Map<String, String> result = new HashMap<>(2);
        result.put("state", state);
        result.put("qrCodeUrl", qrCodeUrl);
        return result;
    }

    @Override
    public TokenDTO checkWxScanStatus(String state) {
        String scanKey = RedisKeyConstant.WX_SCAN_STATE + state;
        Object userId = redisService.get(scanKey);
        if (userId == null) {
            return null; // 未扫码
        }
        User user = userMapper.selectById(Long.valueOf(userId.toString()));
        if (user == null) {
            return null;
        }
        redisService.delete(scanKey);
        return generateAndCacheToken(user);
    }

    @Override
    public String wxScanCallback(String code, String state) {
        // 通过code获取用户信息，存入Redis等待轮询
        try {
            // P1-02: 使用WxMpUtil获取真实openid
            me.chanjar.weixin.common.bean.oauth2.WxOAuth2AccessToken oAuth2AccessToken =
                    wxMpUtil.oauth2getAccessToken(code);
            String openid = "";
            if (oAuth2AccessToken != null) {
                openid = oAuth2AccessToken.getOpenId();
            }
            if (openid == null || openid.isEmpty()) {
                openid = "wx_mp_openid_" + code; // fallback
            }
            User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                    .eq(User::getWxMpOpenid, openid)
                    .eq(User::getDeleted, 0));
            if (user != null) {
                redisService.set(RedisKeyConstant.WX_SCAN_STATE + state, user.getId(), 300, TimeUnit.SECONDS);
            }
            return "success";
        } catch (Exception e) {
            log.error("微信扫码回调失败: {}", e.getMessage(), e);
            return "fail";
        }
    }

    @Override
    public TokenDTO refreshToken(String refreshToken) {
        Map<String, String> tokenMap = jwtTokenProvider.refreshAccessToken(refreshToken);
        TokenDTO dto = new TokenDTO();
        dto.setAccessToken(tokenMap.get("accessToken"));
        dto.setRefreshToken(tokenMap.get("refreshToken"));
        dto.setTokenType("Bearer");
        dto.setExpiresIn(jwtTokenProvider.validateToken(tokenMap.get("accessToken")) ? 7200L : 0L);
        return dto;
    }

    @Override
    public R<Void> logout(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Long userId = LoginUser.getUserId();
        if (userId != null) {
            redisService.delete(RedisKeyConstant.SESSION + token);
        }
        LoginUser.remove();
        return R.ok();
    }

    /** 生成Token并缓存 */
    private TokenDTO generateAndCacheToken(User user) {
        Map<String, String> tokenMap = jwtTokenProvider.generateTokenPair(
                user.getId(), user.getNickname(), user.getUserType());
        String accessToken = tokenMap.get("accessToken");
        String refreshToken = tokenMap.get("refreshToken");

        // 缓存Session
        redisService.set(RedisKeyConstant.SESSION + accessToken, user.getId(),
                RedisKeyConstant.SESSION_TTL, TimeUnit.SECONDS);

        return new TokenDTO(accessToken, refreshToken, 7200L);
    }

    /** 初始化免费订阅 */
    private void initFreeSubscription(Long userId) {
        Subscription sub = new Subscription();
        sub.setUserId(userId);
        sub.setPlanId(1L); // 免费版
        sub.setStartTime(java.time.LocalDateTime.now());
        sub.setExpireTime(null); // 免费版不过期
        sub.setAutoRenew(0);
        sub.setStatus(1);
        sub.setInvitationQuota(BusinessConstant.FREE_INVITATION_QUOTA);
        sub.setInvitationUsed(0);
        sub.setTemplateQuota(BusinessConstant.FREE_TEMPLATE_QUOTA);
        sub.setAiQuota(0);
        sub.setAiUsed(0);
        subscriptionMapper.insert(sub);
    }
}
