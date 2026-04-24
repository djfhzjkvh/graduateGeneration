CREATE DATABASE IF NOT EXISTS graduate_generation
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE graduate_generation;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS sys_oper_log;
DROP TABLE IF EXISTS sys_notify_record;
DROP TABLE IF EXISTS sys_message;
DROP TABLE IF EXISTS report_month_stat;
DROP TABLE IF EXISTS report_daily_stat;
DROP TABLE IF EXISTS crm_customer_competitor_focus;
DROP TABLE IF EXISTS crm_competitor_project;
DROP TABLE IF EXISTS crm_house_type;
DROP TABLE IF EXISTS crm_project;
DROP TABLE IF EXISTS ai_call_log;
DROP TABLE IF EXISTS ai_model_config;
DROP TABLE IF EXISTS ai_script_generate_log;
DROP TABLE IF EXISTS crm_lead_extract_record;
DROP TABLE IF EXISTS crm_customer_heat_log;
DROP TABLE IF EXISTS crm_task_transfer_log;
DROP TABLE IF EXISTS crm_task_remind_log;
DROP TABLE IF EXISTS crm_task;
DROP TABLE IF EXISTS crm_note;
DROP TABLE IF EXISTS crm_follow_record;
DROP TABLE IF EXISTS crm_customer_assign_log;
DROP TABLE IF EXISTS crm_customer_tag_rel;
DROP TABLE IF EXISTS crm_customer_tag;
DROP TABLE IF EXISTS crm_customer;
DROP TABLE IF EXISTS sys_file;
DROP TABLE IF EXISTS sys_dict;
DROP TABLE IF EXISTS sys_config;
DROP TABLE IF EXISTS sys_role_permission;
DROP TABLE IF EXISTS sys_permission;
DROP TABLE IF EXISTS sys_user;
DROP TABLE IF EXISTS sys_dept;
DROP TABLE IF EXISTS sys_role;

CREATE TABLE sys_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    role_code VARCHAR(50) NOT NULL UNIQUE COMMENT 'Role code, e.g. ADVISOR, MANAGER, ADMIN',
    role_name VARCHAR(50) NOT NULL COMMENT 'Role name',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '1 enabled, 0 disabled',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='System role';

CREATE TABLE sys_dept (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    dept_name VARCHAR(100) NOT NULL COMMENT 'Department or team name',
    parent_id BIGINT NOT NULL DEFAULT 0 COMMENT 'Parent department id',
    leader_id BIGINT DEFAULT NULL COMMENT 'Leader user id',
    status TINYINT NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_parent_id (parent_id),
    KEY idx_leader_id (leader_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Department and team';

CREATE TABLE sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL COMMENT 'BCrypt hash in production',
    nickname VARCHAR(50) NOT NULL,
    mobile VARCHAR(20),
    openid VARCHAR(64),
    role_id BIGINT NOT NULL,
    dept_id BIGINT,
    manager_id BIGINT,
    status TINYINT NOT NULL DEFAULT 1,
    deleted TINYINT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_role_id (role_id),
    KEY idx_dept_id (dept_id),
    KEY idx_manager_id (manager_id),
    KEY idx_mobile (mobile)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='System user';

CREATE TABLE sys_permission (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    perm_code VARCHAR(100) NOT NULL UNIQUE,
    perm_name VARCHAR(100) NOT NULL,
    perm_type VARCHAR(20) NOT NULL COMMENT 'MENU, BUTTON, API',
    path VARCHAR(200),
    method VARCHAR(20),
    parent_id BIGINT NOT NULL DEFAULT 0,
    sort_no INT NOT NULL DEFAULT 0,
    status TINYINT NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    KEY idx_parent_id (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Permission';

CREATE TABLE sys_role_permission (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    role_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_role_permission (role_id, permission_id),
    KEY idx_role_id (role_id),
    KEY idx_permission_id (permission_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Role permission relation';

CREATE TABLE sys_config (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    config_key VARCHAR(100) NOT NULL UNIQUE,
    config_value TEXT,
    config_name VARCHAR(100) NOT NULL,
    remark VARCHAR(255),
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='System config';

CREATE TABLE sys_dict (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    dict_type VARCHAR(50) NOT NULL,
    dict_code VARCHAR(50) NOT NULL,
    dict_name VARCHAR(100) NOT NULL,
    sort_no INT NOT NULL DEFAULT 0,
    status TINYINT NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_dict_type_code (dict_type, dict_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Dictionary';

CREATE TABLE sys_file (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    biz_type VARCHAR(50),
    biz_id BIGINT,
    file_name VARCHAR(255) NOT NULL,
    file_url VARCHAR(500) NOT NULL,
    file_type VARCHAR(50),
    file_size BIGINT,
    created_by BIGINT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    KEY idx_biz (biz_type, biz_id),
    KEY idx_created_by (created_by)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Uploaded file';

CREATE TABLE crm_customer (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    customer_name VARCHAR(50) NOT NULL,
    mobile VARCHAR(20),
    gender VARCHAR(10),
    age INT,
    source VARCHAR(30) COMMENT 'WECHAT, PHONE, VISIT, REFERRAL, IMPORT',
    status VARCHAR(30) NOT NULL DEFAULT 'NEW' COMMENT 'NEW, FOLLOWING, VISITED, DEAL, LOST',
    advisor_id BIGINT,
    manager_id BIGINT,
    dept_id BIGINT,
    intent_level VARCHAR(20) COMMENT 'LOW, MEDIUM, HIGH',
    budget_min DECIMAL(12,2),
    budget_max DECIMAL(12,2),
    region VARCHAR(100),
    house_type VARCHAR(100),
    purpose VARCHAR(50),
    remark VARCHAR(500),
    heat_score INT NOT NULL DEFAULT 0,
    latest_follow_time DATETIME,
    next_follow_time DATETIME,
    deleted TINYINT NOT NULL DEFAULT 0,
    created_by BIGINT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_advisor_id (advisor_id),
    KEY idx_manager_id (manager_id),
    KEY idx_dept_id (dept_id),
    KEY idx_status (status),
    KEY idx_mobile (mobile),
    KEY idx_heat_score (heat_score),
    KEY idx_next_follow_time (next_follow_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Customer lead';

CREATE TABLE crm_customer_tag (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    tag_name VARCHAR(50) NOT NULL,
    tag_type VARCHAR(30) NOT NULL DEFAULT 'CUSTOM' COMMENT 'SYSTEM, CUSTOM',
    color VARCHAR(20),
    status TINYINT NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_tag_name (tag_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Customer tag';

CREATE TABLE crm_customer_tag_rel (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    customer_id BIGINT NOT NULL,
    tag_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_customer_tag (customer_id, tag_id),
    KEY idx_customer_id (customer_id),
    KEY idx_tag_id (tag_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Customer tag relation';

CREATE TABLE crm_customer_assign_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    customer_id BIGINT NOT NULL,
    from_user_id BIGINT,
    to_user_id BIGINT,
    action_type VARCHAR(30) NOT NULL COMMENT 'ASSIGN, TRANSFER, RECYCLE',
    remark VARCHAR(255),
    created_by BIGINT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    KEY idx_customer_id (customer_id),
    KEY idx_to_user_id (to_user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Customer assignment log';

CREATE TABLE crm_follow_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    customer_id BIGINT NOT NULL,
    follow_type VARCHAR(20) NOT NULL COMMENT 'WECHAT, PHONE, SMS, VISIT, OTHER',
    follow_result VARCHAR(30) NOT NULL COMMENT 'CONNECTED, NO_ANSWER, WAITING, VISITED, DEAL, LOST',
    content TEXT,
    summary VARCHAR(1000),
    next_follow_time DATETIME,
    user_id BIGINT NOT NULL,
    call_duration INT,
    sentiment_score DECIMAL(5,2),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_customer_id (customer_id),
    KEY idx_user_id (user_id),
    KEY idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Follow record';

CREATE TABLE crm_note (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    customer_id BIGINT NOT NULL,
    follow_id BIGINT,
    note_type VARCHAR(20) NOT NULL COMMENT 'AUTO, MANUAL',
    title VARCHAR(100),
    summary TEXT,
    objection_top3 VARCHAR(1000),
    next_topic VARCHAR(500),
    created_by BIGINT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_customer_id (customer_id),
    KEY idx_follow_id (follow_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Follow review note';

CREATE TABLE crm_task (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    customer_id BIGINT,
    task_type VARCHAR(30) NOT NULL COMMENT 'FOLLOW, VISIT, CALL, SYSTEM',
    title VARCHAR(100) NOT NULL,
    content VARCHAR(500),
    task_date DATE NOT NULL,
    task_time TIME,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING, DONE, OVERDUE, DELAYED',
    priority VARCHAR(20) DEFAULT 'MEDIUM' COMMENT 'LOW, MEDIUM, HIGH, URGENT',
    owner_id BIGINT NOT NULL,
    source_type VARCHAR(30),
    source_id BIGINT,
    delay_count INT NOT NULL DEFAULT 0,
    complete_time DATETIME,
    deleted TINYINT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_customer_id (customer_id),
    KEY idx_owner_id (owner_id),
    KEY idx_task_date (task_date),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Reminder task';

CREATE TABLE crm_task_remind_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    task_id BIGINT NOT NULL,
    remind_type VARCHAR(20) NOT NULL COMMENT 'IN_APP, SMS, WX_WORK',
    remind_time DATETIME NOT NULL,
    status VARCHAR(20) NOT NULL COMMENT 'SUCCESS, FAILED',
    result_msg VARCHAR(255),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    KEY idx_task_id (task_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Task reminder log';

CREATE TABLE crm_task_transfer_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    task_id BIGINT NOT NULL,
    from_user_id BIGINT,
    to_user_id BIGINT NOT NULL,
    reason VARCHAR(255),
    created_by BIGINT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    KEY idx_task_id (task_id),
    KEY idx_to_user_id (to_user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Task transfer log';

CREATE TABLE crm_customer_heat_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    customer_id BIGINT NOT NULL,
    score INT NOT NULL,
    score_date DATE NOT NULL,
    reply_speed_score DECIMAL(6,2) DEFAULT 0,
    ask_depth_score DECIMAL(6,2) DEFAULT 0,
    bargain_score DECIMAL(6,2) DEFAULT 0,
    visit_score DECIMAL(6,2) DEFAULT 0,
    sentiment_score DECIMAL(6,2) DEFAULT 0,
    total_reason VARCHAR(500),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_customer_score_date (customer_id, score_date),
    KEY idx_score_date (score_date),
    KEY idx_score (score)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Customer heat score log';

CREATE TABLE crm_lead_extract_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    source_type VARCHAR(20) NOT NULL COMMENT 'TEXT, OCR, VOICE, EXCEL',
    source_file_id BIGINT,
    raw_text TEXT,
    cleaned_text TEXT,
    extract_json JSON,
    suggestion_json JSON,
    confirm_status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING, CONFIRMED, REJECTED',
    customer_id BIGINT,
    model_name VARCHAR(50),
    prompt_version VARCHAR(30),
    created_by BIGINT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    KEY idx_source_file_id (source_file_id),
    KEY idx_customer_id (customer_id),
    KEY idx_created_by (created_by)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI lead extraction record';

CREATE TABLE ai_script_generate_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    customer_id BIGINT NOT NULL,
    scene_type VARCHAR(30) NOT NULL COMMENT 'FOLLOW_UP, INVITE_VISIT, PRICE_NEGOTIATION',
    channel_type VARCHAR(20) NOT NULL COMMENT 'WECHAT, PHONE, SMS',
    input_json JSON,
    result_text TEXT,
    model_name VARCHAR(50),
    prompt_version VARCHAR(30),
    created_by BIGINT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    KEY idx_customer_id (customer_id),
    KEY idx_created_by (created_by)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI script generation log';

CREATE TABLE ai_model_config (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    provider_name VARCHAR(50) NOT NULL,
    model_name VARCHAR(50) NOT NULL,
    api_url VARCHAR(255) NOT NULL,
    api_key VARCHAR(255) NOT NULL,
    temperature DECIMAL(4,2) DEFAULT 0.70,
    status TINYINT NOT NULL DEFAULT 1,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI model config';

CREATE TABLE ai_call_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    biz_type VARCHAR(30) NOT NULL,
    biz_id BIGINT,
    model_name VARCHAR(50),
    request_json JSON,
    response_json JSON,
    token_usage INT,
    status VARCHAR(20) NOT NULL COMMENT 'SUCCESS, FAILED',
    error_msg VARCHAR(500),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    KEY idx_biz (biz_type, biz_id),
    KEY idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI call log';

CREATE TABLE crm_project (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    project_name VARCHAR(100) NOT NULL,
    city VARCHAR(50),
    region VARCHAR(100),
    address VARCHAR(255),
    avg_price DECIMAL(12,2),
    highlights VARCHAR(1000),
    discount_info VARCHAR(500),
    handover_date DATE,
    status TINYINT NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_region (region)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Real estate project';

CREATE TABLE crm_house_type (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    project_id BIGINT NOT NULL,
    type_name VARCHAR(100) NOT NULL,
    area DECIMAL(8,2),
    rooms VARCHAR(50),
    total_price_min DECIMAL(12,2),
    total_price_max DECIMAL(12,2),
    selling_points VARCHAR(1000),
    status TINYINT NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    KEY idx_project_id (project_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Project house type';

CREATE TABLE crm_competitor_project (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    project_name VARCHAR(100) NOT NULL,
    region VARCHAR(100),
    avg_price DECIMAL(12,2),
    discount_info VARCHAR(500),
    house_types VARCHAR(500),
    handover_date DATE,
    highlights VARCHAR(1000),
    weakness VARCHAR(1000),
    status TINYINT NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_region (region)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Competitor project';

CREATE TABLE crm_customer_competitor_focus (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    customer_id BIGINT NOT NULL,
    competitor_id BIGINT NOT NULL,
    focus_content VARCHAR(500),
    created_by BIGINT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    KEY idx_customer_id (customer_id),
    KEY idx_competitor_id (competitor_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Customer competitor focus';

CREATE TABLE report_daily_stat (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    stat_date DATE NOT NULL,
    user_id BIGINT,
    dept_id BIGINT,
    new_customer_count INT NOT NULL DEFAULT 0,
    high_intent_count INT NOT NULL DEFAULT 0,
    pending_task_count INT NOT NULL DEFAULT 0,
    expected_amount DECIMAL(14,2) NOT NULL DEFAULT 0,
    forget_task_count INT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_stat_date_user (stat_date, user_id),
    KEY idx_dept_id (dept_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Daily report statistics';

CREATE TABLE report_month_stat (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    stat_month VARCHAR(7) NOT NULL COMMENT 'yyyy-MM',
    user_id BIGINT,
    dept_id BIGINT,
    new_customer_count INT NOT NULL DEFAULT 0,
    deal_customer_count INT NOT NULL DEFAULT 0,
    sign_amount DECIMAL(14,2) NOT NULL DEFAULT 0,
    conversion_rate DECIMAL(8,4) NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_stat_month_user (stat_month, user_id),
    KEY idx_dept_id (dept_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Monthly report statistics';

CREATE TABLE sys_message (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    msg_type VARCHAR(30) NOT NULL COMMENT 'TASK, HEAT, PREDICT, GROUP, REFERRAL, SYSTEM',
    title VARCHAR(100) NOT NULL,
    content VARCHAR(1000) NOT NULL,
    biz_type VARCHAR(30),
    biz_id BIGINT,
    read_status TINYINT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    KEY idx_user_id (user_id),
    KEY idx_read_status (read_status),
    KEY idx_biz (biz_type, biz_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='In-app message';

CREATE TABLE sys_notify_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    channel_type VARCHAR(20) NOT NULL COMMENT 'IN_APP, SMS, WX_WORK, SUBSCRIBE',
    receiver VARCHAR(100) NOT NULL,
    biz_type VARCHAR(30),
    biz_id BIGINT,
    send_status VARCHAR(20) NOT NULL COMMENT 'SUCCESS, FAILED',
    send_content VARCHAR(1000),
    send_time DATETIME,
    fail_reason VARCHAR(500),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    KEY idx_biz (biz_type, biz_id),
    KEY idx_receiver (receiver)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Notification record';

CREATE TABLE sys_oper_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT,
    biz_type VARCHAR(50),
    biz_id BIGINT,
    action VARCHAR(50) NOT NULL,
    content VARCHAR(1000),
    ip VARCHAR(50),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    KEY idx_user_id (user_id),
    KEY idx_biz (biz_type, biz_id),
    KEY idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Operation log';

SET FOREIGN_KEY_CHECKS = 1;

