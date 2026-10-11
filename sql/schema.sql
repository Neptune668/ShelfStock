-- =============================================================
-- 小云烘焙店库存与货架管理系统 —— 数据库建表脚本
-- 数据库：MySQL 8.x
-- 字符集：utf8mb4
-- 引擎：InnoDB
--
-- 注意：本脚本含 DROP TABLE IF EXISTS，会删除同名表后重建，
--       请勿直接在生产库执行。
--
-- 表清单（按依赖顺序）：
--   1. admin              管理员表
--   2. material           原材料基础信息
--   3. product            成品基础信息
--   4. material_batch     原材料批次（库存）
--   5. product_batch      成品批次（货架库存）
--   6. material_inbound   原材料入库流水
--   7. material_outbound  原材料出库流水
--   8. sale_record        销售记录
--
-- 状态字段统一存枚举英文名，不存裸数字：
--   material_batch.status:  IN_STOCK(在库) / NEAR_EXPIRY(临期) / EXPIRED(已过期)
--   material_outbound.reason: NORMAL_CONSUME(正常烘焙消耗) / EXPIRED_DISCARD(过期丢弃)
--   product_batch.status:   ON_SALE(在售) / DISCOUNT(临期降价) / OFF_SHELF(已下架) / EXPIRED_OFF(过期下架)
--   sale_record.sale_type:  NORMAL_SALE(正常销售) / DISCOUNT_SALE(临期降价)
-- =============================================================

CREATE DATABASE IF NOT EXISTS `shelf_stock`
    DEFAULT CHARACTER SET utf8mb4
    COLLATE utf8mb4_general_ci;

USE `shelf_stock`;

-- 1. 管理员表 -------------------------------------------------
DROP TABLE IF EXISTS `admin`;
CREATE TABLE `admin` (
    `id`         BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `username`   VARCHAR(50)  NOT NULL                COMMENT '用户名',
    `password`   VARCHAR(100) NOT NULL                COMMENT '密码（BCrypt 加密存储）',
    `real_name`  VARCHAR(50)  DEFAULT NULL            COMMENT '真实姓名',
    `created_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '管理员表';

-- 2. 原材料基础信息 -------------------------------------------
DROP TABLE IF EXISTS `material`;
CREATE TABLE `material` (
    `id`                 BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name`               VARCHAR(100) NOT NULL                COMMENT '原材料名称',
    `category`           VARCHAR(50)  DEFAULT NULL            COMMENT '分类（如粉类、乳制品等）',
    `unit`               VARCHAR(20)  NOT NULL                COMMENT '计量单位（kg/g/个等）',
    `shelf_life_days`    INT          NOT NULL                COMMENT '保质期天数',
    `storage_condition`  VARCHAR(100) DEFAULT NULL            COMMENT '存储条件（常温/冷藏/冷冻）',
    `created_at`         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_category` (`category`),
    KEY `idx_name` (`name`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '原材料基础信息';

-- 3. 成品基础信息 ---------------------------------------------
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product` (
    `id`              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name`            VARCHAR(100) NOT NULL                COMMENT '成品名称',
    `category`        VARCHAR(50)  DEFAULT NULL            COMMENT '分类（面包/蛋糕/饼干等）',
    `price`           DECIMAL(10,2) NOT NULL               COMMENT '售价（元）',
    `shelf_life_days` INT          NOT NULL                COMMENT '保质期天数',
    `created_at`      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_category` (`category`),
    KEY `idx_name` (`name`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '成品基础信息';

-- 4. 原材料批次（库存） ---------------------------------------
DROP TABLE IF EXISTS `material_batch`;
CREATE TABLE `material_batch` (
    `id`               BIGINT         NOT NULL AUTO_INCREMENT COMMENT '主键',
    `material_id`      BIGINT         NOT NULL                COMMENT '原材料ID（关联 material.id）',
    `production_date`  DATE           NOT NULL                COMMENT '生产日期',
    `expiry_date`      DATE           NOT NULL                COMMENT '过期日期',
    `near_expiry_days` INT            NOT NULL                COMMENT '临期阈值（天，入库时填写）',
    `quantity`         DECIMAL(10,2)  NOT NULL DEFAULT 0.00   COMMENT '剩余数量',
    `status`           VARCHAR(20)    NOT NULL DEFAULT 'IN_STOCK' COMMENT '状态：IN_STOCK/NEAR_EXPIRY/EXPIRED',
    `created_at`       DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`       DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_material` (`material_id`),
    KEY `idx_expiry` (`expiry_date`),
    KEY `idx_status` (`status`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '原材料批次（库存）';

-- 5. 成品批次（货架库存） -------------------------------------
DROP TABLE IF EXISTS `product_batch`;
CREATE TABLE `product_batch` (
    `id`               BIGINT         NOT NULL AUTO_INCREMENT COMMENT '主键',
    `product_id`       BIGINT         NOT NULL                COMMENT '成品ID（关联 product.id）',
    `production_date`  DATE           NOT NULL                COMMENT '生产日期',
    `expiry_date`      DATE           NOT NULL                COMMENT '过期日期',
    `near_expiry_days` INT            NOT NULL                COMMENT '临期阈值（天，上架时填写）',
    `quantity`         DECIMAL(10,2)  NOT NULL DEFAULT 0.00   COMMENT '剩余数量',
    `status`           VARCHAR(20)    NOT NULL DEFAULT 'ON_SALE' COMMENT '状态：ON_SALE/DISCOUNT/OFF_SHELF/EXPIRED_OFF',
    `created_at`       DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`       DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_product` (`product_id`),
    KEY `idx_expiry` (`expiry_date`),
    KEY `idx_status` (`status`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '成品批次（货架库存）';

-- 6. 原材料入库流水 -------------------------------------------
DROP TABLE IF EXISTS `material_inbound`;
CREATE TABLE `material_inbound` (
    `id`          BIGINT         NOT NULL AUTO_INCREMENT COMMENT '主键',
    `material_id` BIGINT         NOT NULL                COMMENT '原材料ID（关联 material.id）',
    `batch_id`    BIGINT         NOT NULL                COMMENT '批次ID（关联 material_batch.id）',
    `quantity`    DECIMAL(10,2)  NOT NULL                COMMENT '入库数量',
    `supplier`    VARCHAR(100)   DEFAULT NULL            COMMENT '供应商',
    `operator`    VARCHAR(50)    DEFAULT NULL            COMMENT '操作人',
    `created_at`  DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '入库时间',
    `updated_at`  DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_material` (`material_id`),
    KEY `idx_batch` (`batch_id`),
    KEY `idx_created_at` (`created_at`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '原材料入库流水';

-- 7. 原材料出库流水 -------------------------------------------
DROP TABLE IF EXISTS `material_outbound`;
CREATE TABLE `material_outbound` (
    `id`          BIGINT         NOT NULL AUTO_INCREMENT COMMENT '主键',
    `material_id` BIGINT         NOT NULL                COMMENT '原材料ID（关联 material.id）',
    `batch_id`    BIGINT         NOT NULL                COMMENT '批次ID（关联 material_batch.id）',
    `quantity`    DECIMAL(10,2)  NOT NULL                COMMENT '出库数量',
    `reason`      VARCHAR(20)    NOT NULL                COMMENT '出库原因：NORMAL_CONSUME/EXPIRED_DISCARD',
    `operator`    VARCHAR(50)    DEFAULT NULL            COMMENT '操作人',
    `created_at`  DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '出库时间',
    `updated_at`  DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_material` (`material_id`),
    KEY `idx_batch` (`batch_id`),
    KEY `idx_reason` (`reason`),
    KEY `idx_created_at` (`created_at`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '原材料出库流水';

-- 8. 销售记录 -------------------------------------------------
DROP TABLE IF EXISTS `sale_record`;
CREATE TABLE `sale_record` (
    `id`             BIGINT         NOT NULL AUTO_INCREMENT COMMENT '主键',
    `product_id`     BIGINT         NOT NULL                COMMENT '成品ID（关联 product.id）',
    `batch_id`       BIGINT         NOT NULL                COMMENT '批次ID（关联 product_batch.id）',
    `quantity`       DECIMAL(10,2)  NOT NULL                COMMENT '销售数量',
    `sale_type`      VARCHAR(20)    NOT NULL                COMMENT '销售类型：NORMAL_SALE/DISCOUNT_SALE',
    `original_price` DECIMAL(10,2)  NOT NULL                COMMENT '原价（元）',
    `actual_price`   DECIMAL(10,2)  NOT NULL                COMMENT '实际成交单价（元，临期降价为折扣价）',
    `created_at`     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '销售时间',
    `updated_at`     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_product` (`product_id`),
    KEY `idx_batch` (`batch_id`),
    KEY `idx_sale_type` (`sale_type`),
    KEY `idx_created_at` (`created_at`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '销售记录';

-- =============================================================
-- 初始化说明
-- 管理员账号不在此脚本中硬编码，建议由后端启动时初始化或手动插入：
--   INSERT INTO `admin` (`username`, `password`, `real_name`)
--   VALUES ('admin', '<BCrypt 加密后的密码>', '店长');
-- 密码必须使用 BCrypt 加密，禁止明文存储。
-- =============================================================
