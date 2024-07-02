# 프로젝트 주제
START TRANSACTION;
INSERT INTO subject (subject_id, subject_title) VALUES (1, '모두의 교육');
INSERT INTO subject (subject_id, subject_title) VALUES (2, '기본생활지원');
INSERT INTO subject (subject_id, subject_title) VALUES (3, '안정된 일자리');
INSERT INTO subject (subject_id, subject_title) VALUES (4, '건강한 삶');
INSERT INTO subject (subject_id, subject_title) VALUES (5, '인권평화와역사');
INSERT INTO subject (subject_id, subject_title) VALUES (6, '동물');
INSERT INTO subject (subject_id, subject_title) VALUES (7, '지역공동체');
INSERT INTO subject (subject_id, subject_title) VALUES (8, '더나은사회');
INSERT INTO subject (subject_id, subject_title) VALUES (9, '환경');
COMMIT;

# 프로젝트 대상
START TRANSACTION;
INSERT INTO target (target_id, target_title) VALUES (1, '아동');
INSERT INTO target (target_id, target_title) VALUES (2, '청소년');
INSERT INTO target (target_id, target_title) VALUES (3, '청년');
INSERT INTO target (target_id, target_title) VALUES (4, '여성');
INSERT INTO target (target_id, target_title) VALUES (5, '실버세대');
INSERT INTO target (target_id, target_title) VALUES (6, '장애인');
INSERT INTO target (target_id, target_title) VALUES (7, '이주민');
INSERT INTO target (target_id, target_title) VALUES (8, '다문화');
INSERT INTO target (target_id, target_title) VALUES (9, '지구촌');
INSERT INTO target (target_id, target_title) VALUES (10, '어려운이웃');
INSERT INTO target (target_id, target_title) VALUES (11, '우리사회');
INSERT INTO target (target_id, target_title) VALUES (12, '유기동물');
INSERT INTO target (target_id, target_title) VALUES (13, '야생동물');
COMMIT;

# 테스트용 데이터
START TRANSACTION;
INSERT INTO member (member_id, email, social_login_id, status, created_at, modified_at)
VALUES (1, 'member1@test.com', 'test_1234', 'ACTIVE', '2024-07-01', '2024-07-01');
COMMIT;

START TRANSACTION;
INSERT INTO project(
                    project_id,
                    member_id,
                    is_recruit,
                    wanted_member,
                    start_date,
                    end_date,
                    project_title,
                    main_image_path,
                    sub_title_list,
                    content_list,
                    project_image_list,
                    completed_status,
                    donated_status,
                    donated_amount,
                    likes,
                    created_at,
                    updated_at)
VALUES (1,
        1,
        true,
        '열정 있는 팀원을 원합니다.',
        '2024-07-01',
        '2024-12-01',
        '은퇴 후 사업 시작 안전하게!',
        '프로젝트 대표사진1',
        JSON_ARRAY('소제목1', '소제목2'),
        JSON_ARRAY('본문1', '본문2'),
        JSON_ARRAY('프로젝트 사진1', '프로젝트 사진2', '프로젝트 사진3'),
        'COMPLETED',
        'NOT_DONATED',
        0,
        10,
        '2024-07-01',
        '2024-07-01');

INSERT INTO project_subject(project_subject_id, project_id, subject_id, created_at, updated_at)
VALUES (1, 1, 8, '2024-07-01', '2024-07-01');

INSERT INTO project_target(project_target_id, project_id, target_id, created_at, updated_at)
VALUES (1, 1, 5, '2024-07-01', '2024-07-01');
COMMIT;