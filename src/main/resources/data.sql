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
                    updated_at,
                    is_deleted)
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
        '2024-07-01',
        false);

INSERT INTO project_subject(project_subject_id, project_id, subject_id, created_at, updated_at)
VALUES (1, 1, 8, '2024-07-01', '2024-07-01');

INSERT INTO project_target(project_target_id, project_id, target_id, created_at, updated_at)
VALUES (1, 1, 5, '2024-07-01', '2024-07-01');
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
    updated_at,
    is_deleted)
VALUES (2,
        1,
        true,
        '열정 있는 팀원을 원합니다.',
        '2024-07-02',
        '2024-12-02',
        '청소년 꿈찾기 프로젝트',
        '프로젝트 대표사진1',
        JSON_ARRAY('소제목1', '소제목2'),
        JSON_ARRAY('본문1', '본문2'),
        JSON_ARRAY('프로젝트 사진1', '프로젝트 사진2', '프로젝트 사진3'),
        'COMPLETED',
        'NOT_DONATED',
        0,
        20,
        '2024-07-02',
        '2024-07-02',
        false);

INSERT INTO project_subject(project_subject_id, project_id, subject_id, created_at, updated_at)
VALUES (2, 2, 1, '2024-07-02', '2024-07-02');

INSERT INTO project_target(project_target_id, project_id, target_id, created_at, updated_at)
VALUES (2, 2, 2, '2024-07-02', '2024-07-02');
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
    updated_at,
    is_deleted)
VALUES (3,
        1,
        true,
        '열정 있는 팀원을 원합니다.',
        '2024-07-03',
        '2024-12-03',
        '영유아의 안정적인 보금자리',
        '프로젝트 대표사진1',
        JSON_ARRAY('소제목1', '소제목2'),
        JSON_ARRAY('본문1', '본문2'),
        JSON_ARRAY('프로젝트 사진1', '프로젝트 사진2', '프로젝트 사진3'),
        'COMPLETED',
        'NOT_DONATED',
        0,
        120,
        '2024-07-03',
        '2024-07-03',
        false);

INSERT INTO project_subject(project_subject_id, project_id, subject_id, created_at, updated_at)
VALUES (3, 3, 2, '2024-07-03', '2024-07-03');

INSERT INTO project_target(project_target_id, project_id, target_id, created_at, updated_at)
VALUES (3, 3, 1, '2024-07-03', '2024-07-03');
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
    updated_at,
    is_deleted)
VALUES (4,
        1,
        true,
        '열정 있는 팀원을 원합니다.',
        '2024-07-04',
        '2024-12-04',
        '여성의 안심 귀가 프로젝트',
        '프로젝트 대표사진1',
        JSON_ARRAY('소제목1', '소제목2'),
        JSON_ARRAY('본문1', '본문2'),
        JSON_ARRAY('프로젝트 사진1', '프로젝트 사진2', '프로젝트 사진3'),
        'COMPLETED',
        'NOT_DONATED',
        0,
        50,
        '2024-07-04',
        '2024-07-04',
        false);

INSERT INTO project_subject(project_subject_id, project_id, subject_id, created_at, updated_at)
VALUES (4, 4, 8, '2024-07-04', '2024-07-04');

INSERT INTO project_target(project_target_id, project_id, target_id, created_at, updated_at)
VALUES (4, 4, 4, '2024-07-04', '2024-07-04');
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
    updated_at,
    is_deleted)
VALUES (5,
        1,
        true,
        '열정 있는 팀원을 원합니다.',
        '2024-07-05',
        '2024-12-05',
        '장애인 일자리 창출 프로젝트',
        '프로젝트 대표사진1',
        JSON_ARRAY('소제목1', '소제목2'),
        JSON_ARRAY('본문1', '본문2'),
        JSON_ARRAY('프로젝트 사진1', '프로젝트 사진2', '프로젝트 사진3'),
        'COMPLETED',
        'NOT_DONATED',
        0,
        30,
        '2024-07-05',
        '2024-07-05'
        ,false);

INSERT INTO project_subject(project_subject_id, project_id, subject_id, created_at, updated_at)
VALUES (5, 5, 8, '2024-07-05', '2024-07-05');

INSERT INTO project_target(project_target_id, project_id, target_id, created_at, updated_at)
VALUES (5, 5, 6, '2024-07-05', '2024-07-05');
COMMIT;

# 테스트용 데이터
START TRANSACTION;
INSERT INTO member (member_id, email, social_login_id, status, created_at, modified_at)
VALUES (2, 'member2@test.com', 'test_1234', 'ACTIVE', '2024-07-03', '2024-07-03');
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
    updated_at,
    is_deleted)
VALUES (6,
        2,
        true,
        '열정 있는 팀원을 원합니다.',
        '2024-07-01',
        '2024-12-01',
        '쓰레기 없는 동해 프로젝트',
        '프로젝트 대표사진1',
        JSON_ARRAY('소제목1', '소제목2'),
        JSON_ARRAY('본문1', '본문2'),
        JSON_ARRAY('프로젝트 사진1', '프로젝트 사진2', '프로젝트 사진3'),
        'COMPLETED',
        'DONATED',
        150000,
        1580,
        '2024-07-01',
        '2024-07-01'
        ,false);

INSERT INTO project_subject(project_subject_id, project_id, subject_id, created_at, updated_at)
VALUES (6, 6, 8, '2024-07-01', '2024-07-01');

INSERT INTO project_target(project_target_id, project_id, target_id, created_at, updated_at)
VALUES (6, 6, 11, '2024-07-01', '2024-07-01');
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
    updated_at,
    is_deleted)
VALUES (7,
        2,
        true,
        '열정 있는 팀원을 원합니다.',
        '2024-07-02',
        '2024-12-02',
        '고속도로 서식 야생도물 보호 프로젝트',
        '프로젝트 대표사진1',
        JSON_ARRAY('소제목1', '소제목2'),
        JSON_ARRAY('본문1', '본문2'),
        JSON_ARRAY('프로젝트 사진1', '프로젝트 사진2', '프로젝트 사진3'),
        'COMPLETED',
        'DONATED',
        280000,
        2000,
        '2024-07-02',
        '2024-07-02',
        false);

INSERT INTO project_subject(project_subject_id, project_id, subject_id, created_at, updated_at)
VALUES (7, 7, 9, '2024-07-02', '2024-07-02');

INSERT INTO project_target(project_target_id, project_id, target_id, created_at, updated_at)
VALUES (7, 7, 13, '2024-07-02', '2024-07-02');
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
    updated_at,
    is_deleted)
VALUES (8,
        2,
        true,
        '열정 있는 팀원을 원합니다.',
        '2024-07-03',
        '2024-12-03',
        '탈북민 정착 지원 프로젝트',
        '프로젝트 대표사진1',
        JSON_ARRAY('소제목1', '소제목2'),
        JSON_ARRAY('본문1', '본문2'),
        JSON_ARRAY('프로젝트 사진1', '프로젝트 사진2', '프로젝트 사진3'),
        'COMPLETED',
        'DONATED',
        520000,
        3200,
        '2024-07-03',
        '2024-07-03',
        false);

INSERT INTO project_subject(project_subject_id, project_id, subject_id, created_at, updated_at)
VALUES (8, 8, 7, '2024-07-03', '2024-07-03');

INSERT INTO project_target(project_target_id, project_id, target_id, created_at, updated_at)
VALUES (8, 8, 7, '2024-07-03', '2024-07-03');
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
    updated_at,
    is_deleted)
VALUES (9,
        2,
        true,
        '열정 있는 팀원을 원합니다.',
        '2024-07-04',
        '2024-12-04',
        '직장인 대상 인권 교육 프로젝트',
        '프로젝트 대표사진1',
        JSON_ARRAY('소제목1', '소제목2'),
        JSON_ARRAY('본문1', '본문2'),
        JSON_ARRAY('프로젝트 사진1', '프로젝트 사진2', '프로젝트 사진3'),
        'COMPLETED',
        'DONATED',
        10000,
        1200,
        '2024-07-04',
        '2024-07-04',
        false);

INSERT INTO project_subject(project_subject_id, project_id, subject_id, created_at, updated_at)
VALUES (9, 9, 5, '2024-07-04', '2024-07-04');

INSERT INTO project_target(project_target_id, project_id, target_id, created_at, updated_at)
VALUES (9, 9, 3, '2024-07-04', '2024-07-04');
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
    updated_at,
    is_deleted)
VALUES (10,
        2,
        true,
        '열정 있는 팀원을 원합니다.',
        '2024-07-05',
        '2024-12-05',
        '동네 주민 벼룩 시장 활성화 프로젝트',
        '프로젝트 대표사진1',
        JSON_ARRAY('소제목1', '소제목2'),
        JSON_ARRAY('본문1', '본문2'),
        JSON_ARRAY('프로젝트 사진1', '프로젝트 사진2', '프로젝트 사진3'),
        'COMPLETED',
        'DONATED',
        1350000,
        5700,
        '2024-07-05',
        '2024-07-05',
        false);

INSERT INTO project_subject(project_subject_id, project_id, subject_id, created_at, updated_at)
VALUES (10, 10, 7, '2024-07-05', '2024-07-05');

INSERT INTO project_target(project_target_id, project_id, target_id, created_at, updated_at)
VALUES (10, 10, 11, '2024-07-05', '2024-07-05');
COMMIT;