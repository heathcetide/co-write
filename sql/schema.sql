CREATE
DATABASE docnest_db;

use
docnest_db;

#
User
CREATE TABLE `hib_user`
(
    `id`                      BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `username`                VARCHAR(100) NOT NULL UNIQUE COMMENT '用户名（唯一）',
    `email`                   VARCHAR(100) UNIQUE COMMENT '电子邮箱（可选唯一）',
    `password`                VARCHAR(255) NOT NULL COMMENT '加密后的密码',
    `avatar_url`              VARCHAR(255) COMMENT '用户头像 URL',
    `status`                  VARCHAR(50) DEFAULT 'ACTIVE' COMMENT '用户状态（ACTIVE / BANNED / DELETED）',
    `theme_dark`              TINYINT(1) DEFAULT 0 COMMENT '深色模式（0 - 浅色，1 - 深色）',
    `email_notifications`     TINYINT(1) DEFAULT 1 COMMENT '邮件通知（0 - 关闭，1 - 开启）',
    `language`                VARCHAR(50) DEFAULT 'EN' COMMENT '用户语言 (EN / ZH)',
    `current_organization_id` BIGINT      DEFAULT NULL COMMENT '当前选中的组织 ID',
    `bio`                     TEXT COMMENT '用户简介' COMMENT '用户简介',
    `created_at`              DATETIME    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`              DATETIME    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`                 TINYINT(1) DEFAULT 0 COMMENT '逻辑删除标记（0 - 未删除，1 - 删除）'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
    COMMENT ='用户表';

#
Organization
CREATE TABLE `hib_organization`
(
    `id`              BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `name`            VARCHAR(255) NOT NULL COMMENT '组织名称（唯一）',
    `description`     TEXT COMMENT '组织描述',
    `owner_id`        BIGINT       NOT NULL COMMENT '组织拥有者（用户ID）',
    `created_at`      DATETIME    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`      DATETIME    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`         TINYINT(1) DEFAULT 0 COMMENT '逻辑删除标记（0 - 未删除，1 - 删除）',
    `status`          VARCHAR(50) DEFAULT 'ACTIVE' COMMENT '组织状态（ACTIVE / BANNED / ARCHIVED）',
    `published`       TINYINT(1) DEFAULT 0 COMMENT '是否公开（0 - 否，1 - 是）',
    `max_members`     INT         DEFAULT 50 COMMENT '最大成员数限制',
    `current_members` INT         DEFAULT 0 COMMENT '当前成员数'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
    COMMENT ='组织/团队表';

#
Organization Member
CREATE TABLE `hib_organization_member`
(
    `id`              BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `organization_id` BIGINT NOT NULL COMMENT '组织ID',
    `user_id`         BIGINT NOT NULL COMMENT '用户ID',
    `role`            VARCHAR(50) DEFAULT 'MEMBER' COMMENT '成员角色（OWNER / ADMIN / MEMBER）',
    `status`          VARCHAR(50) DEFAULT 'ACTIVE' COMMENT '状态（ACTIVE / INVITED / REMOVED）',
    `joined_at`       DATETIME    DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间',
    `created_at`      DATETIME    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`      DATETIME    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`         TINYINT(1) DEFAULT 0 COMMENT '逻辑删除标记（0 - 否，1 - 是）',
    UNIQUE KEY `uk_org_user` (`organization_id`, `user_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='组织成员表';

#
Organization Invite
CREATE TABLE `hib_org_invite`
(
    `id`              BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `organization_id` BIGINT NOT NULL COMMENT '邀请所属组织',
    `inviter_id`      BIGINT NOT NULL COMMENT '发起邀请的用户ID',
    `invite_code`     VARCHAR(100) UNIQUE COMMENT '邀请码（可转为二维码链接）',
    `role`            VARCHAR(50) DEFAULT 'MEMBER' COMMENT '邀请加入的角色（MEMBER/ADMIN）',
    `max_uses`        INT         DEFAULT 1 COMMENT '最大可使用次数，null 为无限',
    `used_count`      INT         DEFAULT 0 COMMENT '已使用次数',
    `expires_at`      DATETIME COMMENT '过期时间，可为空（永久有效）',
    `created_at`      DATETIME    DEFAULT CURRENT_TIMESTAMP,
    `updated_at`      DATETIME    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted`         TINYINT(1) DEFAULT 0 COMMENT '逻辑删除'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='组织邀请码表';

#
Knowledge Base
CREATE TABLE `hib_knowledge_base`
(
    `id`              BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `name`            VARCHAR(255) NOT NULL COMMENT '知识库名称',
    `description`     TEXT COMMENT '知识库描述',
    `type`            VARCHAR(50) DEFAULT 'PRIVATE' COMMENT '知识库类型（PRIVATE / PUBLIC）',
    `owner_id`        BIGINT       NOT NULL COMMENT '所有者用户ID',
    `organization_id` BIGINT COMMENT '所属组织ID',
    `cover_url`       VARCHAR(255) COMMENT '封面图 URL',
    `created_at`      DATETIME    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`      DATETIME    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`         TINYINT(1) DEFAULT 0 COMMENT '逻辑删除标记（0 - 未删除，1 - 删除）'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='知识库表';

#
Knowledge Base Collaborator
CREATE TABLE `hib_knowledge_base_collaborator`
(
    `id`                BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `knowledge_base_id` BIGINT NOT NULL COMMENT '所属知识库ID',
    `user_id`           BIGINT NOT NULL COMMENT '协作者用户ID',
    `role`              VARCHAR(50) DEFAULT 'EDITOR' COMMENT '角色（VIEWER / EDITOR / ADMIN）',
    `joined_at`         DATETIME    DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间',
    `created_at`        DATETIME    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`        DATETIME    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`           TINYINT(1) DEFAULT 0 COMMENT '逻辑删除标记（0 - 未删除，1 - 删除）',
    UNIQUE KEY `uk_kb_user` (`knowledge_base_id`, `user_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='知识库协作者表';

#
Document
CREATE TABLE `hib_document`
(
    `id`                 BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `knowledge_base_id`  BIGINT       NOT NULL COMMENT '所属知识库ID',
    `parent_id`          BIGINT      DEFAULT NULL COMMENT '父文档ID（目录结构）',
    `title`              VARCHAR(255) NOT NULL COMMENT '文档标题',
    `type`               VARCHAR(50) DEFAULT 'DOC' COMMENT '类型（DOC / FOLDER）',
    `owner_id`           BIGINT       NOT NULL COMMENT '文档所有者ID',
    `sort_order`         INT         DEFAULT 0 COMMENT '文档顺序排序值',
    `status`             VARCHAR(50) DEFAULT 'ACTIVE' COMMENT '文档状态（ACTIVE / DELETED）',
    `version`            INT         DEFAULT 1 COMMENT '当前版本号',
    `level`              INT         DEFAULT 0 COMMENT '文档层级（1 - 一级目录，2 - 二级目录，以此类推）',
    `published`          TINYINT(1) DEFAULT 0 COMMENT '是否公开（0 - 否，1 - 是）',
    `current_version_id` BIGINT      DEFAULT NULL COMMENT '当前版本ID',
    `metadata`           JSON COMMENT '扩展元数据',
    `created_at`         DATETIME    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`         DATETIME    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`            TINYINT(1) DEFAULT 0 COMMENT '逻辑删除标记（0 - 未删除，1 - 删除）'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='文档与目录表';

#
Comment
CREATE TABLE `hib_document_comment`
(
    `id`          BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `document_id` BIGINT NOT NULL COMMENT '文档ID',
    `user_id`     BIGINT NOT NULL COMMENT '评论者ID',
    `content`     TEXT   NOT NULL COMMENT '评论内容',
    `anchor`      JSON COMMENT '锚点位置（块索引、偏移等）',
    `status`      VARCHAR(50) DEFAULT 'ACTIVE' COMMENT '状态（ACTIVE / RESOLVED / DELETED）',
    `created_at`  DATETIME    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`  DATETIME    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`     TINYINT(1) DEFAULT 0 COMMENT '逻辑删除标记（0 - 未删除，1 - 删除）'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='文档批注表';

#
Operation Log
CREATE TABLE `hib_operation_log`
(
    `id`          BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `type`        VARCHAR(100) NOT NULL COMMENT '操作类型（如：LOGIN、DOC_EDIT）',
    `description` TEXT COMMENT '操作描述',
    `user_id`     BIGINT COMMENT '执行用户ID',
    `operator`    VARCHAR(100) NOT NULL COMMENT '操作人标识（用户名或IP）',
    `success`     BOOLEAN      NOT NULL COMMENT '是否成功',
    `params`      TEXT COMMENT '输入参数快照',
    `result`      TEXT COMMENT '输出结果快照',
    `timestamp`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    `deleted`     TINYINT      NOT NULL DEFAULT 0 COMMENT '是否逻辑删除',
    `created_at`  DATETIME              DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`  DATETIME              DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='操作日志表';

#
Knowledge Favorite Base
CREATE TABLE `hib_document_favorite`
(
    `id`           BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `user_id`      BIGINT NOT NULL COMMENT '用户ID',
    `document_id`  BIGINT NOT NULL COMMENT '被收藏的文档ID',
    `favorited_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
    `created_at`   DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`   DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`      TINYINT(1) DEFAULT 0 COMMENT '逻辑删除标记（0 - 未删除，1 - 删除）',
    UNIQUE KEY `uk_user_doc` (`user_id`, `document_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='用户文档收藏表';

#
Notification
CREATE TABLE `hib_notification`
(
    `id`            BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL COMMENT '主键ID（雪花算法生成）',
    `user_id`       BIGINT       NOT NULL COMMENT '接收用户ID',
    `type`          VARCHAR(32)  NOT NULL COMMENT '通知类型（如 SYSTEM、ADVICE 等）',
    `title`         VARCHAR(255) NOT NULL COMMENT '通知标题',
    `content`       TEXT COMMENT '通知内容',
    `level`         VARCHAR(32)           DEFAULT 'NORMAL' COMMENT '通知级别（如 NORMAL、IMPORTANT、URGENT 等）',
    `is_read`       BOOLEAN      NOT NULL DEFAULT FALSE COMMENT '是否已读',
    `business_id`   varchar(255)          DEFAULT NULL COMMENT '关联业务ID',
    `business_type` VARCHAR(64)           DEFAULT NULL COMMENT '关联业务类型',
    `extra_data`    JSON                  DEFAULT NULL COMMENT '额外数据（JSON 格式）',
    `send_time`     DATETIME              DEFAULT NULL COMMENT '发送时间',
    `read_time`     DATETIME              DEFAULT NULL COMMENT '读取时间',
    `deleted`       TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除（0：正常，1：删除）',
    `created_at`    DATETIME              DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`    DATETIME              DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX           `idx_user_id` (`user_id`),
    INDEX           `idx_type` (`type`),
    INDEX           `idx_level` (`level`),
    INDEX           `idx_read` (`is_read`),
    INDEX           `idx_send_time` (`send_time`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = '通知表';

#
Tag
CREATE TABLE `hib_tag`
(
    `id`         BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `name`       VARCHAR(100) NOT NULL UNIQUE COMMENT '标签名称',
    `color`      VARCHAR(20) DEFAULT '#CCCCCC' COMMENT '标签颜色（Hex）',
    `created_at` DATETIME    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`    TINYINT(1) DEFAULT 0 COMMENT '逻辑删除标记（0 - 未删除，1 - 删除）'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
COLLATE = utf8mb4_unicode_ci COMMENT ='标签表';

#
Document Tag
CREATE TABLE `hib_entity_tag`
(
    `id`          BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `entity_type` TINYINT NOT NULL COMMENT '实体类型：1-用户，2-文件（可扩展其他类型）',
    `entity_id`   BIGINT  NOT NULL COMMENT '实体ID（用户ID或文件ID）',
    `tag_id`      BIGINT  NOT NULL COMMENT '标签ID（关联hib_tag.id）',
    `created_at`  DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`  DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`     TINYINT(1) DEFAULT 0 COMMENT '逻辑删除标记（0-未删除，1-删除）',
    UNIQUE KEY `uk_entity_tag` (`entity_type`, `entity_id`, `tag_id`),
    KEY           `idx_entity` (`entity_type`, `entity_id`),
    KEY           `idx_tag` (`tag_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT '实体-标签关联表';

#
Document Template
CREATE TABLE `hib_document_template`
(
    `id`          BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `title`       VARCHAR(255) NOT NULL COMMENT '模板标题',
    `description` TEXT COMMENT '模板描述',
    `content`     LONGTEXT     NOT NULL COMMENT '模板内容',
    `creator_id`  BIGINT       NOT NULL COMMENT '创建人ID',
    `scope`       VARCHAR(50) DEFAULT 'PRIVATE' COMMENT '模板可见性（SYSTEM / PUBLIC / PRIVATE）',
    `created_at`  DATETIME    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`  DATETIME    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`     TINYINT(1) DEFAULT 0 COMMENT '逻辑删除标记（0 - 未删除，1 - 删除）'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='文档模板表';

#
Document Permission
CREATE TABLE `hib_document_permission`
(
    `id`          BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `document_id` BIGINT      NOT NULL COMMENT '文档ID',
    `user_id`     BIGINT      NOT NULL COMMENT '被授权用户ID',
    `permission`  VARCHAR(50) NOT NULL COMMENT '权限类型（VIEW / EDIT / COMMENT / ADMIN）',
    `granted_by`  BIGINT COMMENT '授权人ID',
    `granted_at`  DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '授权时间',
    `created_at`  DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`  DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`     TINYINT(1) DEFAULT 0 COMMENT '逻辑删除标记（0 - 未删除，1 - 删除）',
    `disable_export` BOOLEAN DEFAULT FALSE COMMENT '是否禁用导出（0 - 允许，1 - 禁用）',
    UNIQUE KEY `uk_doc_user` (`document_id`, `user_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='文档细粒度权限控制表';

#
Document Share
CREATE TABLE `hib_document_share`
(
    `id`                  BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `document_id`         BIGINT       NOT NULL COMMENT '文档ID',
    `share_code`          VARCHAR(100) NOT NULL UNIQUE COMMENT '分享码（短链接标识）',
    `share_type`          VARCHAR(50)  NOT NULL COMMENT '分享类型（LINK / PASSWORD）',
    `permission`          VARCHAR(50)  NOT NULL COMMENT '分享权限（VIEW / EDIT / COMMENT）',
    `access_password`     VARCHAR(255) COMMENT '访问口令（明文，用于展示）',
    `password_hash`       VARCHAR(255) COMMENT '口令哈希（用于验证）',
    `password_retry_count` INT DEFAULT 0 COMMENT '口令重试次数',
    `password_locked_until` DATETIME COMMENT '口令锁定到期时间',
    `status`              VARCHAR(50)  DEFAULT 'ACTIVE' COMMENT '状态（ACTIVE / EXPIRED / REVOKED）',
    `expire_time`         DATETIME COMMENT '过期时间',
    `remark`              TEXT COMMENT '分享说明',
    `creator_id`          BIGINT       NOT NULL COMMENT '创建人ID',
    `access_count`        INT          DEFAULT 0 COMMENT '访问次数',
    `last_accessed_at`    DATETIME COMMENT '最后访问时间',
    `created_at`          DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`          DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`             TINYINT(1)   DEFAULT 0 COMMENT '逻辑删除标记（0 - 未删除，1 - 删除）',
    INDEX `idx_document_id` (`document_id`),
    INDEX `idx_share_code` (`share_code`),
    INDEX `idx_creator_id` (`creator_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='文档分享表';

#
Webhook
CREATE TABLE `hib_webhook`
(
    `id`              BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `organization_id` BIGINT COMMENT '所属组织ID',
    `event_type`      VARCHAR(100) NOT NULL COMMENT '事件类型（如：DOCUMENT_UPDATED）',
    `target_url`      VARCHAR(255) NOT NULL COMMENT 'Webhook接收地址',
    `enabled`         BOOLEAN  DEFAULT TRUE COMMENT '是否启用',
    `secret`          VARCHAR(255) COMMENT '签名密钥（用于校验）',
    `created_at`      DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`         TINYINT(1) DEFAULT 0 COMMENT '逻辑删除标记（0 - 未删除，1 - 删除）'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='Webhook 配置表';

#
Plugins
CREATE TABLE `hib_plugins`
(
    `id`                BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `plugin_name`       VARCHAR(255) NOT NULL COMMENT '插件名称',
    `plugin_type`       VARCHAR(100) NOT NULL COMMENT '插件类型（如：文本生成、图像识别等）',
    `description`       TEXT COMMENT '插件描述',
    `version`           VARCHAR(50)  NOT NULL COMMENT '插件版本',
    `webhook_url`       VARCHAR(255) COMMENT '插件的 Webhook 回调地址',
    `api_url`           text COMMENT '插件API地址',
    `documentation_url` VARCHAR(255) COMMENT '插件文档地址',
    `author`            VARCHAR(255) COMMENT '插件作者',
    `repository_url`    VARCHAR(255) COMMENT '源代码仓库地址',
    `tags`              JSON COMMENT '插件标签',
    `last_validated_at` DATETIME COMMENT '最后验证时间',
    `install_count`     INT         DEFAULT 0 COMMENT '安装次数',
    `download_count`    INT         DEFAULT 0 COMMENT '下载次数',
    `rating`            DECIMAL(3, 2) COMMENT '评分',
    `file_path`         VARCHAR(500) COMMENT '插件文件存储路径',
    `pl_status`         VARCHAR(50) DEFAULT 'PENDING' COMMENT '插件状态（PENDING/VALIDATED/REJECTED/ACTIVE）',
    `config_data`       varchar(255) COMMENT '插件配置数据',
    `enabled`           BOOLEAN     DEFAULT TRUE COMMENT '插件是否启用',
    `created_at`        DATETIME    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`        DATETIME    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`           TINYINT(1) DEFAULT 0 COMMENT '逻辑删除标记（0 - 未删除，1 - 删除）'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = '插件库表';

CREATE TABLE hib_social_logins
(
    id                BIGINT AUTO_INCREMENT COMMENT '主键ID' PRIMARY KEY,
    user_id           BIGINT       NOT NULL COMMENT '关联的用户ID',
    provider_name     VARCHAR(255) NOT NULL COMMENT '第三方登录提供商名称（如 GitHub、Google 等）',
    provider_user_id  VARCHAR(255) NOT NULL COMMENT '在第三方平台中的唯一用户标识',
    provider_username VARCHAR(255) COMMENT '在第三方平台中的用户名',
    access_token      VARCHAR(512) COMMENT '访问令牌，用于调用第三方 API',
    refresh_token     VARCHAR(512) COMMENT '刷新令牌，用于刷新访问令牌',
    `created_at`        DATETIME    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`        DATETIME    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`           TINYINT(1) DEFAULT 0 COMMENT '逻辑删除标记（0 - 未删除，1 - 删除）'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='社交登录信息表';
