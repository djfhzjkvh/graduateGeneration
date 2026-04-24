USE graduate_generation;

SET NAMES utf8mb4;

INSERT INTO sys_role (id, role_code, role_name, status) VALUES
(1, 'ADVISOR', '置业顾问', 1),
(2, 'MANAGER', '销售经理', 1),
(3, 'ADMIN', '系统管理员', 1);

INSERT INTO sys_dept (id, dept_name, parent_id, leader_id, status) VALUES
(1, '天津销售中心', 0, 2, 1),
(2, '河西一组', 1, 2, 1),
(3, '南开二组', 1, 3, 1);

-- Demo password is 123456. Replace with BCrypt hash when auth is implemented.
INSERT INTO sys_user (id, username, password, nickname, mobile, role_id, dept_id, manager_id, status) VALUES
(1, 'admin', '123456', '系统管理员', '13800000000', 3, 1, NULL, 1),
(2, 'manager_hx', '123456', '河西经理', '13800000001', 2, 2, NULL, 1),
(3, 'manager_nk', '123456', '南开经理', '13800000002', 2, 3, NULL, 1),
(4, 'advisor_a', '123456', '顾问A', '13800000003', 1, 2, 2, 1),
(5, 'advisor_b', '123456', '顾问B', '13800000004', 1, 2, 2, 1),
(6, 'advisor_c', '123456', '顾问C', '13800000005', 1, 3, 3, 1);

INSERT INTO sys_dict (dict_type, dict_code, dict_name, sort_no) VALUES
('customer_status', 'NEW', '新线索', 1),
('customer_status', 'FOLLOWING', '跟进中', 2),
('customer_status', 'VISITED', '已到访', 3),
('customer_status', 'DEAL', '已成交', 4),
('customer_status', 'LOST', '已流失', 5),
('follow_type', 'WECHAT', '微信', 1),
('follow_type', 'PHONE', '电话', 2),
('follow_type', 'SMS', '短信', 3),
('follow_type', 'VISIT', '面谈', 4),
('task_status', 'PENDING', '待完成', 1),
('task_status', 'DONE', '已完成', 2),
('task_status', 'OVERDUE', '已逾期', 3),
('intent_level', 'LOW', '低意向', 1),
('intent_level', 'MEDIUM', '中意向', 2),
('intent_level', 'HIGH', '高意向', 3);

INSERT INTO sys_config (config_key, config_value, config_name, remark) VALUES
('follow.rule.days', '3,7,15', '默认跟进节奏', '按 3/7/15 天生成回访任务'),
('heat.high.threshold', '80', '高意向阈值', '热度分大于等于该值进入高意向池'),
('ai.prompt.version', 'v1', 'Prompt版本', '默认AI提示词版本');

INSERT INTO crm_customer_tag (id, tag_name, tag_type, color) VALUES
(1, '价格敏感', 'SYSTEM', '#f56c6c'),
(2, '急需入住', 'SYSTEM', '#e6a23c'),
(3, '品质关注', 'SYSTEM', '#409eff'),
(4, '贷款购房', 'SYSTEM', '#67c23a'),
(5, '学区关注', 'SYSTEM', '#909399'),
(6, '改善型', 'SYSTEM', '#8e44ad'),
(7, '投资型', 'SYSTEM', '#16a085');

INSERT INTO crm_project (id, project_name, city, region, address, avg_price, highlights, discount_info, handover_date) VALUES
(1, '津湾学府', '天津', '河西', '河西区友谊路88号', 18500.00, '近地铁，准现房，低密社区', '认购享98折，限时送车位券', '2026-12-31'),
(2, '南开云筑', '天津', '南开', '南开区黄河道66号', 21000.00, '学区资源，商业配套成熟', '首付分期，老带新优惠', '2027-06-30'),
(3, '滨海澜庭', '天津', '滨海新区', '滨海新区中新大道18号', 13500.00, '低总价，生态景观，发展潜力', '团购满3组额外优惠1%', '2026-10-01');

INSERT INTO crm_house_type (project_id, type_name, area, rooms, total_price_min, total_price_max, selling_points) VALUES
(1, 'A户型', 98.00, '三室两厅一卫', 1750000.00, 1880000.00, '三开间朝南，适合刚需家庭'),
(1, 'B户型', 115.00, '三室两厅两卫', 2050000.00, 2250000.00, '南北通透，改善首选'),
(2, 'C户型', 89.00, '两室两厅一卫', 1800000.00, 1950000.00, '低总价学区产品'),
(2, 'D户型', 128.00, '四室两厅两卫', 2600000.00, 2850000.00, '四房改善，双卫设计'),
(3, 'E户型', 95.00, '三室两厅一卫', 1250000.00, 1380000.00, '低总价，适合首次置业');

INSERT INTO crm_competitor_project (id, project_name, region, avg_price, discount_info, house_types, handover_date, highlights, weakness) VALUES
(1, '河西悦府', '河西', 17800.00, '总价减5万', '89平两室,110平三室', '2027-06-30', '价格略低', '交房晚，距离地铁较远'),
(2, '南开公馆', '南开', 22500.00, '无明显折扣', '99平三室,130平四室', '2026-09-30', '品牌开发商', '总价高，户型选择少'),
(3, '滨海未来城', '滨海新区', 12800.00, '首付分期', '88平两室,105平三室', '2027-12-31', '价格低', '配套成熟度不足');

INSERT INTO crm_customer
(id, customer_name, mobile, gender, age, source, status, advisor_id, manager_id, dept_id, intent_level, budget_min, budget_max, region, house_type, purpose, remark, heat_score, latest_follow_time, next_follow_time, created_by)
VALUES
(1, '张伟', '13810000001', '男', 32, 'WECHAT', 'FOLLOWING', 4, 2, 2, 'HIGH', 1600000.00, 1900000.00, '河西', '三室', '刚需', '预算180万以内，关注地铁', 86, NOW() - INTERVAL 1 DAY, NOW() + INTERVAL 2 DAY, 4),
(2, '李娜', '13810000002', '女', 29, 'PHONE', 'NEW', 4, 2, 2, 'MEDIUM', 1200000.00, 1500000.00, '滨海新区', '两室', '投资', '关注低总价产品', 62, NOW() - INTERVAL 3 DAY, NOW() + INTERVAL 1 DAY, 4),
(3, '王磊', '13810000003', '男', 41, 'VISIT', 'VISITED', 5, 2, 2, 'HIGH', 2200000.00, 2800000.00, '南开', '四室', '改善', '已带家人到访，关注学区', 91, NOW() - INTERVAL 1 DAY, NOW() + INTERVAL 3 DAY, 5),
(4, '赵敏', '13810000004', '女', 35, 'REFERRAL', 'FOLLOWING', 6, 3, 3, 'MEDIUM', 1800000.00, 2200000.00, '南开', '三室', '学区', '朋友推荐，关注学校变化', 74, NOW() - INTERVAL 2 DAY, NOW() + INTERVAL 4 DAY, 6),
(5, '陈浩', '13810000005', '男', 27, 'WECHAT', 'LOST', 5, 2, 2, 'LOW', 900000.00, 1200000.00, '滨海新区', '两室', '刚需', '认为价格偏高，暂缓购房', 28, NOW() - INTERVAL 8 DAY, NULL, 5),
(6, '刘洋', '13810000006', '男', 38, 'IMPORT', 'DEAL', 6, 3, 3, 'HIGH', 2000000.00, 2400000.00, '河西', '三室', '改善', '已成交，后续可转介绍', 95, NOW() - INTERVAL 5 DAY, NULL, 6);

INSERT INTO crm_customer_tag_rel (customer_id, tag_id) VALUES
(1, 1), (1, 2), (1, 4),
(2, 7),
(3, 3), (3, 5), (3, 6),
(4, 5), (4, 4),
(5, 1),
(6, 3), (6, 6);

INSERT INTO crm_follow_record
(customer_id, follow_type, follow_result, content, summary, next_follow_time, user_id, sentiment_score)
VALUES
(1, 'WECHAT', 'CONNECTED', '客户咨询98平三室，总价预算180万以内，希望靠近地铁，周六下午可到访。', '客户预算明确，到访意向强。', NOW() + INTERVAL 2 DAY, 4, 88.00),
(1, 'PHONE', 'CONNECTED', '电话确认到访时间，客户表示会带家人一起看。', '带家人到访，成交意向增强。', NOW() + INTERVAL 2 DAY, 4, 92.00),
(2, 'PHONE', 'NO_ANSWER', '首次电话未接通，短信已发送项目简介。', '等待客户回复。', NOW() + INTERVAL 1 DAY, 4, 45.00),
(3, 'VISIT', 'VISITED', '客户已到访，重点关注128平四房和学区资源。', '改善需求强，关注品质和学区。', NOW() + INTERVAL 3 DAY, 5, 90.00),
(4, 'WECHAT', 'CONNECTED', '客户询问学校政策和贷款利率。', '关注学区和贷款成本。', NOW() + INTERVAL 4 DAY, 6, 76.00),
(5, 'WECHAT', 'LOST', '客户表示价格偏高，暂时再看看。', '价格阻力明显，短期流失。', NULL, 5, 25.00),
(6, 'VISIT', 'DEAL', '客户完成认购，后续推荐老带新活动。', '成交客户，可转介绍。', NULL, 6, 95.00);

INSERT INTO crm_task
(customer_id, task_type, title, content, task_date, task_time, status, priority, owner_id, source_type, source_id)
VALUES
(1, 'FOLLOW', '确认周六到访', '提前发送定位和样板间信息', CURDATE() + INTERVAL 1 DAY, '10:00:00', 'PENDING', 'HIGH', 4, 'CUSTOMER', 1),
(2, 'CALL', '再次电话联系李娜', '首次未接通，需二次联系', CURDATE(), '15:00:00', 'PENDING', 'MEDIUM', 4, 'CUSTOMER', 2),
(3, 'FOLLOW', '发送四房优惠方案', '客户关注四房，需给出价格测算', CURDATE() + INTERVAL 2 DAY, '09:30:00', 'PENDING', 'HIGH', 5, 'CUSTOMER', 3),
(4, 'FOLLOW', '解答学区政策', '整理学区资料和贷款方案', CURDATE() + INTERVAL 3 DAY, '14:30:00', 'PENDING', 'MEDIUM', 6, 'CUSTOMER', 4),
(5, 'FOLLOW', '低总价产品二次触达', '推荐滨海低总价房源', CURDATE() - INTERVAL 1 DAY, '16:00:00', 'OVERDUE', 'LOW', 5, 'CUSTOMER', 5);

INSERT INTO crm_customer_heat_log
(customer_id, score, score_date, reply_speed_score, ask_depth_score, bargain_score, visit_score, sentiment_score, total_reason)
VALUES
(1, 86, CURDATE(), 18, 18, 16, 18, 16, '客户预算明确并约定到访，意向较高。'),
(2, 62, CURDATE(), 10, 12, 12, 8, 20, '客户关注低总价，但回复不稳定。'),
(3, 91, CURDATE(), 18, 19, 17, 20, 17, '客户已带家人到访，改善需求明确。'),
(4, 74, CURDATE(), 14, 16, 12, 14, 18, '客户持续询问学区和贷款，需加强资料支撑。'),
(5, 28, CURDATE(), 4, 6, 4, 4, 10, '客户明确表示价格偏高，短期意向低。'),
(6, 95, CURDATE(), 20, 18, 18, 20, 19, '客户已成交，可进入转介绍维护。');

INSERT INTO crm_note
(customer_id, follow_id, note_type, title, summary, objection_top3, next_topic, created_by)
VALUES
(1, 1, 'AUTO', '张伟跟进复盘', '客户关注98平三室、预算180万以内、靠近地铁。', '总价控制;地铁距离;到访时间确认', '发送到访路线和98平户型图', 4),
(3, 4, 'AUTO', '王磊到访复盘', '客户带家人看房，重点关注四房和学区。', '学区稳定性;总价压力;车位需求', '输出四房优惠和学区资料', 5);

INSERT INTO crm_lead_extract_record
(source_type, raw_text, cleaned_text, extract_json, suggestion_json, confirm_status, customer_id, model_name, prompt_version, created_by)
VALUES
('TEXT', '客户：三室还有吗？预算180万以内，最好靠近地铁，周六下午可以看。', '三室还有吗？预算180万以内，最好靠近地铁，周六下午可以看。',
 JSON_OBJECT('budgetMax', 1800000, 'houseType', '三室', 'regionPreference', '靠近地铁', 'visitTime', '周六下午', 'intentLevel', 'HIGH'),
 JSON_OBJECT('customerName', '待补充', 'mobile', '待补充'), 'CONFIRMED', 1, 'deepseek-chat', 'v1', 4);

INSERT INTO ai_script_generate_log
(customer_id, scene_type, channel_type, input_json, result_text, model_name, prompt_version, created_by)
VALUES
(1, 'FOLLOW_UP', 'WECHAT',
 JSON_OBJECT('customerName', '张伟', 'budgetMax', 1800000, 'houseType', '三室'),
 '张先生您好，您关注的98平三室目前还有合适房源，距离地铁较近，总价也在您180万预算范围内。我这边先把户型图和周六到访路线发您，您看方便吗？',
 'deepseek-chat', 'v1', 4);

INSERT INTO ai_model_config (provider_name, model_name, api_url, api_key, temperature, status) VALUES
('DeepSeek', 'deepseek-chat', 'https://api.deepseek.com/chat/completions', 'PLEASE_REPLACE_WITH_REAL_KEY', 0.70, 1);

INSERT INTO report_daily_stat
(stat_date, user_id, dept_id, new_customer_count, high_intent_count, pending_task_count, expected_amount, forget_task_count)
VALUES
(CURDATE(), 4, 2, 2, 1, 2, 1800000.00, 0),
(CURDATE(), 5, 2, 1, 1, 2, 2600000.00, 1),
(CURDATE(), 6, 3, 1, 1, 1, 2100000.00, 0),
(CURDATE(), 2, 2, 3, 2, 4, 4400000.00, 1),
(CURDATE(), 3, 3, 1, 1, 1, 2100000.00, 0);

INSERT INTO report_month_stat
(stat_month, user_id, dept_id, new_customer_count, deal_customer_count, sign_amount, conversion_rate)
VALUES
(DATE_FORMAT(CURDATE(), '%Y-%m'), 4, 2, 12, 2, 3600000.00, 0.1667),
(DATE_FORMAT(CURDATE(), '%Y-%m'), 5, 2, 10, 1, 2600000.00, 0.1000),
(DATE_FORMAT(CURDATE(), '%Y-%m'), 6, 3, 9, 1, 2200000.00, 0.1111);

INSERT INTO sys_message
(user_id, msg_type, title, content, biz_type, biz_id, read_status)
VALUES
(4, 'TASK', '今日回访提醒', '请在15:00前再次电话联系李娜。', 'TASK', 2, 0),
(4, 'HEAT', '高意向客户提醒', '张伟热度分达到86，请优先跟进。', 'CUSTOMER', 1, 0),
(5, 'TASK', '逾期任务提醒', '陈浩低总价产品二次触达任务已逾期。', 'TASK', 5, 0),
(2, 'SYSTEM', '团队异常提醒', '河西一组存在1条逾期任务，请关注。', 'REPORT', 1, 0);

INSERT INTO sys_oper_log
(user_id, biz_type, biz_id, action, content, ip)
VALUES
(1, 'SYSTEM', 1, 'INIT_DATA', '初始化演示数据', '127.0.0.1'),
(4, 'CUSTOMER', 1, 'CREATE', '创建客户张伟', '127.0.0.1'),
(4, 'FOLLOW', 1, 'CREATE', '新增张伟微信跟进记录', '127.0.0.1');

