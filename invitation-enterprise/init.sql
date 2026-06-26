-- =============================================
-- 企业级邀请函系统 - 数据库初始化脚本
-- 兼容 MySQL 5.5：不用 JSON 列类型(用 TEXT)，不用窗口函数，字符集 utf8，引擎 InnoDB
-- =============================================

SET NAMES utf8;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- 用户表
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `phone` varchar(128) NOT NULL DEFAULT '' COMMENT '加密手机号',
  `nickname` varchar(64) NOT NULL DEFAULT '' COMMENT '昵称',
  `avatar` varchar(512) NOT NULL DEFAULT '' COMMENT '头像URL',
  `password` varchar(128) NOT NULL DEFAULT '' COMMENT '密码',
  `wx_openid` varchar(128) NOT NULL DEFAULT '' COMMENT '微信小程序openid',
  `wx_unionid` varchar(128) NOT NULL DEFAULT '' COMMENT '微信unionid',
  `wx_mp_openid` varchar(128) NOT NULL DEFAULT '' COMMENT '微信公众号openid',
  `user_type` int(11) NOT NULL DEFAULT 1 COMMENT '用户类型: 1普通 2企业 3管理员',
  `enterprise_name` varchar(128) NOT NULL DEFAULT '' COMMENT '企业名称',
  `enterprise_logo` varchar(512) NOT NULL DEFAULT '' COMMENT '企业Logo',
  `status` int(11) NOT NULL DEFAULT 1 COMMENT '状态: 0禁用 1正常',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
  `last_login_ip` varchar(64) NOT NULL DEFAULT '' COMMENT '最后登录IP',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime NOT NULL COMMENT '更新时间',
  `deleted` int(11) NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0未删除 1已删除',
  PRIMARY KEY (`id`),
  KEY `idx_phone` (`phone`),
  KEY `idx_wx_openid` (`wx_openid`),
  KEY `idx_wx_mp_openid` (`wx_mp_openid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户表';

-- ----------------------------
-- 订阅表 (P0-01: plan→plan_id, end_date→expire_time, Date→LocalDateTime)
-- ----------------------------
DROP TABLE IF EXISTS `subscription`;
CREATE TABLE `subscription` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `plan_id` bigint(20) NOT NULL DEFAULT 1 COMMENT '套餐ID: 1免费 2专业 3企业',
  `start_time` datetime DEFAULT NULL COMMENT '开始时间',
  `expire_time` datetime DEFAULT NULL COMMENT '到期时间',
  `auto_renew` int(11) NOT NULL DEFAULT 0 COMMENT '自动续费: 0否 1是',
  `status` int(11) NOT NULL DEFAULT 1 COMMENT '状态: 0无效 1有效',
  `invitation_quota` int(11) NOT NULL DEFAULT 0 COMMENT '邀请函配额',
  `invitation_used` int(11) NOT NULL DEFAULT 0 COMMENT '已使用邀请函数',
  `template_quota` int(11) NOT NULL DEFAULT 0 COMMENT '模板配额',
  `ai_quota` int(11) NOT NULL DEFAULT 0 COMMENT 'AI配额',
  `ai_used` int(11) NOT NULL DEFAULT 0 COMMENT '已使用AI数',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='订阅表';

-- ----------------------------
-- 邀请函表
-- ----------------------------
DROP TABLE IF EXISTS `invitation`;
CREATE TABLE `invitation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL COMMENT '创建者ID',
  `title` varchar(128) NOT NULL DEFAULT '' COMMENT '标题',
  `activity_type` int(11) NOT NULL DEFAULT 0 COMMENT '活动类型',
  `template_id` bigint(20) DEFAULT NULL COMMENT '模板ID',
  `cover_image` varchar(512) NOT NULL DEFAULT '' COMMENT '封面图',
  `content` text COMMENT '内容(JSON)',
  `activity_date` datetime DEFAULT NULL COMMENT '活动时间',
  `activity_address` varchar(256) NOT NULL DEFAULT '' COMMENT '活动地址',
  `latitude` decimal(10,6) DEFAULT NULL COMMENT '纬度',
  `longitude` decimal(10,6) DEFAULT NULL COMMENT '经度',
  `music_id` bigint(20) DEFAULT NULL COMMENT '音乐ID',
  `custom_music_url` varchar(512) NOT NULL DEFAULT '' COMMENT '自定义音乐URL',
  `video_cover_url` varchar(512) NOT NULL DEFAULT '' COMMENT '视频封面URL',
  `video_url` varchar(512) NOT NULL DEFAULT '' COMMENT '视频URL',
  `ai_greeting` text COMMENT 'AI祝福语',
  `extra_data` text COMMENT '扩展数据(JSON)',
  `og_title` varchar(128) NOT NULL DEFAULT '' COMMENT 'OG标题',
  `og_description` varchar(256) NOT NULL DEFAULT '' COMMENT 'OG描述',
  `og_image` varchar(512) NOT NULL DEFAULT '' COMMENT 'OG图片',
  `short_code` varchar(32) NOT NULL DEFAULT '' COMMENT '短链码',
  `qrcode_url` varchar(512) NOT NULL DEFAULT '' COMMENT '二维码URL',
  `poster_url` varchar(512) NOT NULL DEFAULT '' COMMENT '海报URL',
  `pv` int(11) NOT NULL DEFAULT 0 COMMENT 'PV浏览量',
  `uv` int(11) NOT NULL DEFAULT 0 COMMENT 'UV独立访客',
  `reply_count` int(11) NOT NULL DEFAULT 0 COMMENT '回复数',
  `attend_count` int(11) NOT NULL DEFAULT 0 COMMENT '出席数',
  `bless_count` int(11) NOT NULL DEFAULT 0 COMMENT '祝福数',
  `gift_amount` decimal(12,2) NOT NULL DEFAULT 0.00 COMMENT '礼金总额',
  `status` int(11) NOT NULL DEFAULT 0 COMMENT '状态: 0草稿 1已发布 2未发布',
  `watermark` int(11) NOT NULL DEFAULT 1 COMMENT '水印: 0否 1是',
  `published_at` datetime DEFAULT NULL COMMENT '发布时间',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime NOT NULL COMMENT '更新时间',
  `deleted` int(11) NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_short_code` (`short_code`),
  KEY `idx_template_id` (`template_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='邀请函表';

-- ----------------------------
-- 聊天消息表 (P0-02: 添加 is_read 和 is_revoked 字段)
-- ----------------------------
DROP TABLE IF EXISTS `chat_message`;
CREATE TABLE `chat_message` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `invitation_id` bigint(20) NOT NULL COMMENT '邀请函ID',
  `user_id` bigint(20) DEFAULT NULL COMMENT '发送者ID',
  `guest_name` varchar(64) NOT NULL DEFAULT '' COMMENT '访客名称',
  `content` text NOT NULL COMMENT '消息内容',
  `message_type` int(11) NOT NULL DEFAULT 0 COMMENT '消息类型: 0文本 1图片',
  `is_read` int(11) NOT NULL DEFAULT 0 COMMENT '是否已读: 0未读 1已读',
  `is_revoked` int(11) NOT NULL DEFAULT 0 COMMENT '是否撤回: 0否 1是',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_invitation_id` (`invitation_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='聊天消息表';

-- ----------------------------
-- 席位表 (P0-03: name→table_name, capacity→seat_count, 添加 table_number 和 used_seats)
-- ----------------------------
DROP TABLE IF EXISTS `seat_table`;
CREATE TABLE `seat_table` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `invitation_id` bigint(20) NOT NULL COMMENT '邀请函ID',
  `table_name` varchar(64) NOT NULL DEFAULT '' COMMENT '桌名',
  `table_number` varchar(32) NOT NULL DEFAULT '' COMMENT '桌号',
  `seat_count` int(11) NOT NULL DEFAULT 0 COMMENT '座位数',
  `used_seats` int(11) NOT NULL DEFAULT 0 COMMENT '已使用座位数',
  `layout_config` text COMMENT '布局配置(JSON)',
  `sort_order` int(11) NOT NULL DEFAULT 0 COMMENT '排序',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime NOT NULL COMMENT '更新时间',
  `deleted` int(11) NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_invitation_id` (`invitation_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='席位表';

-- ----------------------------
-- 席位分配表
-- ----------------------------
DROP TABLE IF EXISTS `seat_assignment`;
CREATE TABLE `seat_assignment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `seat_table_id` bigint(20) NOT NULL COMMENT '席位表ID',
  `invitation_id` bigint(20) NOT NULL COMMENT '邀请函ID',
  `seat_no` varchar(32) NOT NULL DEFAULT '' COMMENT '座位号',
  `guest_name` varchar(64) NOT NULL DEFAULT '' COMMENT '宾客姓名',
  `guest_phone` varchar(32) NOT NULL DEFAULT '' COMMENT '宾客手机号',
  `guest_reply_id` bigint(20) DEFAULT NULL COMMENT '宾客回复ID',
  `table_id` bigint(20) DEFAULT NULL COMMENT '桌ID',
  `guest_id` bigint(20) DEFAULT NULL COMMENT '宾客ID',
  `seat_number` varchar(32) NOT NULL DEFAULT '' COMMENT '座位编号',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_seat_table_id` (`seat_table_id`),
  KEY `idx_invitation_id` (`invitation_id`),
  KEY `idx_guest_reply_id` (`guest_reply_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='席位分配表';

-- ----------------------------
-- 宾客回复表
-- ----------------------------
DROP TABLE IF EXISTS `guest_reply`;
CREATE TABLE `guest_reply` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `invitation_id` bigint(20) NOT NULL COMMENT '邀请函ID',
  `guest_name` varchar(64) NOT NULL DEFAULT '' COMMENT '宾客姓名',
  `guest_phone` varchar(32) NOT NULL DEFAULT '' COMMENT '宾客手机号',
  `reply_status` int(11) NOT NULL DEFAULT 0 COMMENT '回复状态: 0待定 1出席 2不确定 3不出席',
  `guest_count` int(11) NOT NULL DEFAULT 1 COMMENT '出席人数',
  `remark` varchar(256) NOT NULL DEFAULT '' COMMENT '备注',
  `ip_address` varchar(64) NOT NULL DEFAULT '' COMMENT 'IP地址',
  `user_agent` varchar(512) NOT NULL DEFAULT '' COMMENT 'User-Agent',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_invitation_id` (`invitation_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='宾客回复表';

-- ----------------------------
-- 浏览日志表
-- ----------------------------
DROP TABLE IF EXISTS `view_log`;
CREATE TABLE `view_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `invitation_id` bigint(20) NOT NULL COMMENT '邀请函ID',
  `visitor_id` varchar(128) NOT NULL DEFAULT '' COMMENT '访客ID',
  `ip_address` varchar(64) NOT NULL DEFAULT '' COMMENT 'IP地址',
  `user_agent` varchar(512) NOT NULL DEFAULT '' COMMENT 'User-Agent',
  `referer` varchar(512) NOT NULL DEFAULT '' COMMENT '来源URL',
  `view_date` date DEFAULT NULL COMMENT '浏览日期',
  `view_hour` int(11) DEFAULT NULL COMMENT '浏览小时',
  `source` varchar(64) NOT NULL DEFAULT '' COMMENT '来源类型',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_invitation_id` (`invitation_id`),
  KEY `idx_view_date` (`view_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='浏览日志表';

-- ----------------------------
-- 分析事件表
-- ----------------------------
DROP TABLE IF EXISTS `analytics_event`;
CREATE TABLE `analytics_event` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `invitation_id` bigint(20) DEFAULT NULL COMMENT '邀请函ID',
  `user_id` bigint(20) DEFAULT NULL COMMENT '用户ID',
  `event_type` varchar(64) NOT NULL DEFAULT '' COMMENT '事件类型',
  `event_name` varchar(128) NOT NULL DEFAULT '' COMMENT '事件名称',
  `event_data` text COMMENT '事件数据(JSON)',
  `session_id` varchar(128) NOT NULL DEFAULT '' COMMENT '会话ID',
  `platform` varchar(64) NOT NULL DEFAULT '' COMMENT '平台',
  `ip_address` varchar(64) NOT NULL DEFAULT '' COMMENT 'IP地址',
  `user_agent` varchar(512) NOT NULL DEFAULT '' COMMENT 'User-Agent',
  `extra_data` text COMMENT '扩展数据',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_invitation_id` (`invitation_id`),
  KEY `idx_event_name` (`event_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='分析事件表';

-- ----------------------------
-- 模板表
-- ----------------------------
DROP TABLE IF EXISTS `template`;
CREATE TABLE `template` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(128) NOT NULL DEFAULT '' COMMENT '模板名称',
  `cover_image` varchar(512) NOT NULL DEFAULT '' COMMENT '封面图',
  `preview_url` varchar(512) NOT NULL DEFAULT '' COMMENT '预览URL',
  `category` varchar(64) NOT NULL DEFAULT '' COMMENT '分类',
  `type` int(11) NOT NULL DEFAULT 0 COMMENT '类型: 0免费 1付费',
  `price` decimal(10,2) NOT NULL DEFAULT 0.00 COMMENT '价格',
  `status` int(11) NOT NULL DEFAULT 1 COMMENT '状态: 0下架 1上架',
  `sort_order` int(11) NOT NULL DEFAULT 0 COMMENT '排序',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime NOT NULL COMMENT '更新时间',
  `deleted` int(11) NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='模板表';

-- ----------------------------
-- 短信日志表
-- ----------------------------
DROP TABLE IF EXISTS `sms_log`;
CREATE TABLE `sms_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `phone` varchar(32) NOT NULL DEFAULT '' COMMENT '手机号',
  `code` varchar(16) NOT NULL DEFAULT '' COMMENT '验证码',
  `biz_type` varchar(32) NOT NULL DEFAULT '' COMMENT '业务类型',
  `status` int(11) NOT NULL DEFAULT 0 COMMENT '状态: 0待发送 1已发送 2失败',
  `ip_address` varchar(64) NOT NULL DEFAULT '' COMMENT 'IP地址',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='短信日志表';

-- ----------------------------
-- 支付表
-- ----------------------------
DROP TABLE IF EXISTS `payment`;
CREATE TABLE `payment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `order_no` varchar(64) NOT NULL DEFAULT '' COMMENT '订单号',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `plan_id` int(11) NOT NULL DEFAULT 0 COMMENT '套餐ID',
  `amount` decimal(12,2) NOT NULL DEFAULT 0.00 COMMENT '金额(分)',
  `pay_channel` varchar(32) NOT NULL DEFAULT '' COMMENT '支付渠道',
  `status` int(11) NOT NULL DEFAULT 0 COMMENT '状态: 0待支付 1已支付 2已退款 3已关闭',
  `transaction_id` varchar(128) NOT NULL DEFAULT '' COMMENT '第三方交易号',
  `description` varchar(256) NOT NULL DEFAULT '' COMMENT '描述',
  `paid_at` datetime DEFAULT NULL COMMENT '支付时间',
  `refund_reason` varchar(256) NOT NULL DEFAULT '' COMMENT '退款原因',
  `refunded_at` datetime DEFAULT NULL COMMENT '退款时间',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='支付表';

-- ----------------------------
-- 祝福消息表
-- ----------------------------
DROP TABLE IF EXISTS `bless_message`;
CREATE TABLE `bless_message` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `invitation_id` bigint(20) NOT NULL COMMENT '邀请函ID',
  `guest_name` varchar(64) NOT NULL DEFAULT '' COMMENT '访客名称',
  `content` text NOT NULL COMMENT '祝福内容',
  `status` int(11) NOT NULL DEFAULT 1 COMMENT '状态: 0隐藏 1显示',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_invitation_id` (`invitation_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='祝福消息表';

-- ----------------------------
-- 邀请函版本表
-- ----------------------------
DROP TABLE IF EXISTS `invitation_version`;
CREATE TABLE `invitation_version` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `invitation_id` bigint(20) NOT NULL COMMENT '邀请函ID',
  `version_no` int(11) NOT NULL DEFAULT 1 COMMENT '版本号',
  `snapshot` text NOT NULL COMMENT '快照(JSON)',
  `change_desc` varchar(256) NOT NULL DEFAULT '' COMMENT '变更描述',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_invitation_id` (`invitation_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='邀请函版本表';

-- ----------------------------
-- 礼物记录表
-- ----------------------------
DROP TABLE IF EXISTS `gift_record`;
CREATE TABLE `gift_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `invitation_id` bigint(20) NOT NULL COMMENT '邀请函ID',
  `guest_name` varchar(64) NOT NULL DEFAULT '' COMMENT '访客名称',
  `amount` decimal(12,2) NOT NULL DEFAULT 0.00 COMMENT '金额',
  `message` varchar(256) NOT NULL DEFAULT '' COMMENT '留言',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_invitation_id` (`invitation_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='礼物记录表';

-- ----------------------------
-- 审计日志表
-- ----------------------------
DROP TABLE IF EXISTS `audit_log`;
CREATE TABLE `audit_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) DEFAULT NULL COMMENT '操作人ID',
  `module` varchar(64) NOT NULL DEFAULT '' COMMENT '模块',
  `action` varchar(64) NOT NULL DEFAULT '' COMMENT '操作',
  `description` varchar(256) NOT NULL DEFAULT '' COMMENT '描述',
  `ip_address` varchar(64) NOT NULL DEFAULT '' COMMENT 'IP地址',
  `request_data` text COMMENT '请求数据',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='审计日志表';

-- ----------------------------
-- 音乐库表
-- ----------------------------
DROP TABLE IF EXISTS `music_library`;
CREATE TABLE `music_library` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(128) NOT NULL DEFAULT '' COMMENT '名称',
  `url` varchar(512) NOT NULL DEFAULT '' COMMENT '音乐URL',
  `cover_url` varchar(512) NOT NULL DEFAULT '' COMMENT '封面URL',
  `duration` int(11) NOT NULL DEFAULT 0 COMMENT '时长(秒)',
  `category` varchar(64) NOT NULL DEFAULT '' COMMENT '分类',
  `status` int(11) NOT NULL DEFAULT 1 COMMENT '状态: 0下架 1上架',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='音乐库表';

-- ----------------------------
-- 短链表
-- ----------------------------
DROP TABLE IF EXISTS `short_link`;
CREATE TABLE `short_link` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `short_code` varchar(32) NOT NULL DEFAULT '' COMMENT '短链码',
  `original_url` varchar(1024) NOT NULL DEFAULT '' COMMENT '原始URL',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_short_code` (`short_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='短链表';

-- ----------------------------
-- 模板市场表
-- ----------------------------
DROP TABLE IF EXISTS `template_market`;
CREATE TABLE `template_market` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(128) NOT NULL DEFAULT '' COMMENT '名称',
  `description` text COMMENT '描述',
  `cover_image` varchar(512) NOT NULL DEFAULT '' COMMENT '封面图',
  `price` decimal(10,2) NOT NULL DEFAULT 0.00 COMMENT '价格',
  `status` int(11) NOT NULL DEFAULT 1 COMMENT '状态',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='模板市场表';

-- ----------------------------
-- 相册表
-- ----------------------------
DROP TABLE IF EXISTS `photo_album`;
CREATE TABLE `photo_album` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `invitation_id` bigint(20) NOT NULL COMMENT '邀请函ID',
  `image_url` varchar(512) NOT NULL DEFAULT '' COMMENT '图片URL',
  `thumbnail_url` varchar(512) NOT NULL DEFAULT '' COMMENT '缩略图URL',
  `sort_order` int(11) NOT NULL DEFAULT 0 COMMENT '排序',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_invitation_id` (`invitation_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='相册表';

SET FOREIGN_KEY_CHECKS = 1;
