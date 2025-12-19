-- 文档评论模块 SQL
-- 表名: hib_document_comment

-- 创建文档评论表
CREATE TABLE IF NOT EXISTS `hib_document_comment`
(
    `id`          BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `document_id` BIGINT       NOT NULL COMMENT '文档ID',
    `user_id`     BIGINT       NOT NULL COMMENT '评论者ID',
    `content`     TEXT         NOT NULL COMMENT '评论内容',
    `anchor`      JSON COMMENT '锚点位置（块索引、偏移等）',
    `status`      VARCHAR(50) DEFAULT 'ACTIVE' COMMENT '状态（ACTIVE / RESOLVED / DELETED）',
    `created_at`  DATETIME    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`  DATETIME    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`     TINYINT(1) DEFAULT 0 COMMENT '逻辑删除标记（0 - 未删除，1 - 删除）',
    INDEX `idx_document_id` (`document_id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_status` (`status`),
    INDEX `idx_created_at` (`created_at`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='文档批注表';

-- 添加外键约束（可选，根据实际需求）
-- ALTER TABLE `hib_document_comment`
--     ADD CONSTRAINT `fk_comment_document` FOREIGN KEY (`document_id`) REFERENCES `hib_document` (`id`) ON DELETE CASCADE;
-- ALTER TABLE `hib_document_comment`
--     ADD CONSTRAINT `fk_comment_user` FOREIGN KEY (`user_id`) REFERENCES `hib_user` (`id`) ON DELETE CASCADE;

