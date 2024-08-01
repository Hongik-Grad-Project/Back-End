# 프로젝트 주제
START TRANSACTION;
INSERT INTO tag (id, tag_title) VALUES (1, '모두의 교육');
INSERT INTO tag (id, tag_title) VALUES (2, '기본생활지원');
INSERT INTO tag (id, tag_title) VALUES (3, '안정된 일자리');
INSERT INTO tag (id, tag_title) VALUES (4, '건강한 삶');
INSERT INTO tag (id, tag_title) VALUES (5, '인권평화와역사');
INSERT INTO tag (id, tag_title) VALUES (6, '동물');
INSERT INTO tag (id, tag_title) VALUES (7, '지역공동체');
INSERT INTO tag (id, tag_title) VALUES (8, '더나은사회');
INSERT INTO tag (id, tag_title) VALUES (9, '환경');
INSERT INTO tag (id, tag_title) VALUES (10, '열정');
INSERT INTO tag (id, tag_title) VALUES (11, '선한 영향력');
COMMIT;

# 프로젝트 대상
START TRANSACTION;
INSERT INTO target (id, target_title) VALUES (1, '아동');
INSERT INTO target (id, target_title) VALUES (2, '청소년');
INSERT INTO target (id, target_title) VALUES (3, '청년');
INSERT INTO target (id, target_title) VALUES (4, '여성');
INSERT INTO target (id, target_title) VALUES (5, '실버세대');
INSERT INTO target (id, target_title) VALUES (6, '장애인');
INSERT INTO target (id, target_title) VALUES (7, '이주민');
INSERT INTO target (id, target_title) VALUES (8, '다문화');
INSERT INTO target (id, target_title) VALUES (9, '지구촌');
INSERT INTO target (id, target_title) VALUES (10, '어려운이웃');
INSERT INTO target (id, target_title) VALUES (11, '우리사회');
INSERT INTO target (id, target_title) VALUES (12, '유기동물');
INSERT INTO target (id, target_title) VALUES (13, '야생동물');
COMMIT;

### 테스트용 멤버 데이터 ###
START TRANSACTION;
INSERT INTO member (id, email, social_login_id, nickname, status, created_at, modified_at)
VALUES (1, 'member1@test.com', 'test_0001', '윤제민' ,'ACTIVE', '2024-07-01', '2024-07-01');

INSERT INTO member (id, email, social_login_id, nickname, status, created_at, modified_at)
VALUES (2, 'member2@test.com', 'test_0002', '권동민' ,'ACTIVE', '2024-07-03', '2024-07-03');

INSERT INTO member (id, email, social_login_id, nickname, status, created_at, modified_at)
VALUES (3, 'member3@test.com', 'test_0003', '신유나' ,'ACTIVE', '2024-07-03', '2024-07-03');

INSERT INTO member (id, email, social_login_id, nickname, status, created_at, modified_at)
VALUES (4, 'member4@test.com', 'test_0004', '김서연' ,'ACTIVE', '2024-07-03', '2024-07-03');

INSERT INTO member (id, email, social_login_id, nickname, status, created_at, modified_at)
VALUES (5, 'member5@test.com', 'test_0005', '김태형' ,'ACTIVE', '2024-07-03', '2024-07-03');

COMMIT;

### 테스트용 프로젝트 데이터 ###
START TRANSACTION;
INSERT INTO project(
                    id,
                    member_id,
                    summary,
                    start_date,
                    end_date,
                    project_title,
                    main_image_path,
                    sub_title_list,
                    content_list,
                    project_image_list,
                    completed_status,
                    created_at,
                    updated_at,
                    is_deleted)
VALUES (1,
        1,
        '장년층의 취업 문제',
        '2024-07-01',
        '2024-10-01',
        '은퇴 후 사업 시작 안전하게!',
        '프로젝트 대표사진1',
        JSON_ARRAY('소제목1', '소제목2'),
        JSON_ARRAY('본문1', '본문2'),
        JSON_ARRAY('프로젝트 사진1', '프로젝트 사진2', '프로젝트 사진3'),
        'COMPLETED',
        '2024-07-01',
        '2024-07-01',
        false);

INSERT INTO project_tag(id, project_id, tag_id, created_at, updated_at)
VALUES (1, 1, 8, '2024-07-01', '2024-07-01');

INSERT INTO project_tag(id, project_id, tag_id, created_at, updated_at)
VALUES (2, 1, 10, '2024-07-01', '2024-07-01');


INSERT INTO project_target(id, project_id, target_id, created_at, updated_at)
VALUES (1, 1, 5, '2024-07-01', '2024-07-01');
COMMIT;

START TRANSACTION;
INSERT INTO project(
    id,
    member_id,
    summary,
    start_date,
    end_date,
    project_title,
    main_image_path,
    sub_title_list,
    content_list,
    project_image_list,
    completed_status,
    created_at,
    updated_at,
    is_deleted)
VALUES (2,
        1,
        '청소년 무기력증 문제',
        '2024-07-02',
        '2024-12-02',
        '청소년 꿈찾기 프로젝트',
        '프로젝트 대표사진1',
        JSON_ARRAY('소제목1', '소제목2'),
        JSON_ARRAY('본문1', '본문2'),
        JSON_ARRAY('프로젝트 사진1', '프로젝트 사진2', '프로젝트 사진3'),
        'COMPLETED',
        '2024-07-02',
        '2024-07-02',
        false);

INSERT INTO project_tag(id, project_id, tag_id, created_at, updated_at)
VALUES (3, 2, 1, '2024-07-02', '2024-07-02');

INSERT INTO project_tag(id, project_id, tag_id, created_at, updated_at)
VALUES (4, 2, 10, '2024-07-01', '2024-07-01');

INSERT INTO project_target(id, project_id, target_id, created_at, updated_at)
VALUES (2, 2, 2, '2024-07-02', '2024-07-02');
COMMIT;

START TRANSACTION;
INSERT INTO project(
    id,
    member_id,
    summary,
    start_date,
    end_date,
    project_title,
    main_image_path,
    sub_title_list,
    content_list,
    project_image_list,
    completed_status,
    created_at,
    updated_at,
    is_deleted)
VALUES (3,
        1,
        '영유아 유기 문제',
        '2024-07-03',
        '2024-12-03',
        '영유아의 안정적인 보금자리',
        '프로젝트 대표사진1',
        JSON_ARRAY('소제목1', '소제목2'),
        JSON_ARRAY('본문1', '본문2'),
        JSON_ARRAY('프로젝트 사진1', '프로젝트 사진2', '프로젝트 사진3'),
        'COMPLETED',
        '2024-07-03',
        '2024-07-03',
        false);

INSERT INTO project_tag(id, project_id, tag_id, created_at, updated_at)
VALUES (5, 3, 2, '2024-07-03', '2024-07-03');

INSERT INTO project_tag(id, project_id, tag_id, created_at, updated_at)
VALUES (6, 3, 11, '2024-07-01', '2024-07-01');

INSERT INTO project_target(id, project_id, target_id, created_at, updated_at)
VALUES (3, 3, 1, '2024-07-03', '2024-07-03');
COMMIT;

START TRANSACTION;
INSERT INTO project(
    id,
    member_id,
    summary,
    start_date,
    end_date,
    project_title,
    main_image_path,
    sub_title_list,
    content_list,
    project_image_list,
    completed_status,
    created_at,
    updated_at,
    is_deleted)
VALUES (4,
        1,
        '여성 치안 문제',
        '2024-07-04',
        '2024-12-04',
        '여성의 안심 귀가 프로젝트',
        '프로젝트 대표사진1',
        JSON_ARRAY('소제목1', '소제목2'),
        JSON_ARRAY('본문1', '본문2'),
        JSON_ARRAY('프로젝트 사진1', '프로젝트 사진2', '프로젝트 사진3'),
        'COMPLETED',
        '2024-07-04',
        '2024-07-04',
        false);

INSERT INTO project_tag(id, project_id, tag_id, created_at, updated_at)
VALUES (7, 4, 8, '2024-07-04', '2024-07-04');

INSERT INTO project_tag(id, project_id, tag_id, created_at, updated_at)
VALUES (8, 4, 11, '2024-07-01', '2024-07-01');

INSERT INTO project_target(id, project_id, target_id, created_at, updated_at)
VALUES (4, 4, 4, '2024-07-04', '2024-07-04');
COMMIT;

START TRANSACTION;
INSERT INTO project(
    id,
    member_id,
    summary,
    start_date,
    end_date,
    project_title,
    main_image_path,
    sub_title_list,
    content_list,
    project_image_list,
    completed_status,
    created_at,
    updated_at,
    is_deleted)
VALUES (5,
        1,
        '장애인 실업 문제',
        '2024-07-05',
        '2024-12-05',
        '장애인 일자리 창출 프로젝트',
        '프로젝트 대표사진1',
        JSON_ARRAY('소제목1', '소제목2'),
        JSON_ARRAY('본문1', '본문2'),
        JSON_ARRAY('프로젝트 사진1', '프로젝트 사진2', '프로젝트 사진3'),
        'COMPLETED',
        '2024-07-05',
        '2024-07-05'
        ,false);

INSERT INTO project_tag(id, project_id, tag_id, created_at, updated_at)
VALUES (9, 5, 8, '2024-07-05', '2024-07-05');

INSERT INTO project_tag(id, project_id, tag_id, created_at, updated_at)
VALUES (10, 5, 11, '2024-07-01', '2024-07-01');

INSERT INTO project_target(id, project_id, target_id, created_at, updated_at)
VALUES (5, 5, 6, '2024-07-05', '2024-07-05');
COMMIT;

START TRANSACTION;
INSERT INTO project(
    id,
    member_id,
    summary,
    start_date,
    end_date,
    project_title,
    main_image_path,
    sub_title_list,
    content_list,
    project_image_list,
    completed_status,
    created_at,
    updated_at,
    is_deleted)
VALUES (6,
        2,
        '동해 쓰레기 문제',
        '2024-07-01',
        '2024-12-01',
        '쓰레기 없는 동해 프로젝트',
        '프로젝트 대표사진1',
        JSON_ARRAY('소제목1', '소제목2'),
        JSON_ARRAY('본문1', '본문2'),
        JSON_ARRAY('프로젝트 사진1', '프로젝트 사진2', '프로젝트 사진3'),
        'COMPLETED',
        '2024-07-01',
        '2024-07-01'
        ,false);

INSERT INTO project_tag(id, project_id, tag_id, created_at, updated_at)
VALUES (11, 6, 8, '2024-07-01', '2024-07-01');

INSERT INTO project_target(id, project_id, target_id, created_at, updated_at)
VALUES (6, 6, 11, '2024-07-01', '2024-07-01');
COMMIT;

START TRANSACTION;
INSERT INTO project(
    id,
    member_id,
    summary,
    start_date,
    end_date,
    project_title,
    main_image_path,
    sub_title_list,
    content_list,
    project_image_list,
    completed_status,
    created_at,
    updated_at,
    is_deleted)
VALUES (7,
        2,
        '고속도로 로드킬 문제',
        '2024-07-02',
        '2024-12-02',
        '고속도로 서식 야생도물 보호 프로젝트',
        '프로젝트 대표사진1',
        JSON_ARRAY('소제목1', '소제목2'),
        JSON_ARRAY('본문1', '본문2'),
        JSON_ARRAY('프로젝트 사진1', '프로젝트 사진2', '프로젝트 사진3'),
        'COMPLETED',
        '2024-07-02',
        '2024-07-02',
        false);

INSERT INTO project_tag(id, project_id, tag_id, created_at, updated_at)
VALUES (12, 7, 9, '2024-07-02', '2024-07-02');

INSERT INTO project_target(id, project_id, target_id, created_at, updated_at)
VALUES (7, 7, 13, '2024-07-02', '2024-07-02');
COMMIT;

START TRANSACTION;
INSERT INTO project(
    id,
    member_id,
    summary,
    start_date,
    end_date,
    project_title,
    main_image_path,
    sub_title_list,
    content_list,
    project_image_list,
    completed_status,
    created_at,
    updated_at,
    is_deleted)
VALUES (8,
        2,
        '탈북민 부적응 문제',
        '2024-07-03',
        '2024-12-03',
        '탈북민 정착 지원 프로젝트',
        '프로젝트 대표사진1',
        JSON_ARRAY('소제목1', '소제목2'),
        JSON_ARRAY('본문1', '본문2'),
        JSON_ARRAY('프로젝트 사진1', '프로젝트 사진2', '프로젝트 사진3'),
        'COMPLETED',
        '2024-07-03',
        '2024-07-03',
        false);

INSERT INTO project_tag(id, project_id, tag_id, created_at, updated_at)
VALUES (13, 8, 7, '2024-07-03', '2024-07-03');

INSERT INTO project_target(id, project_id, target_id, created_at, updated_at)
VALUES (8, 8, 7, '2024-07-03', '2024-07-03');
COMMIT;

START TRANSACTION;
INSERT INTO project(
    id,
    member_id,
    summary,
    start_date,
    end_date,
    project_title,
    main_image_path,
    sub_title_list,
    content_list,
    project_image_list,
    completed_status,
    created_at,
    updated_at,
    is_deleted)
VALUES (9,
        2,
        '직장내 인격 모독 문제',
        '2024-07-04',
        '2024-12-04',
        '직장인 대상 인권 교육 프로젝트',
        '프로젝트 대표사진1',
        JSON_ARRAY('소제목1', '소제목2'),
        JSON_ARRAY('본문1', '본문2'),
        JSON_ARRAY('프로젝트 사진1', '프로젝트 사진2', '프로젝트 사진3'),
        'COMPLETED',
        '2024-07-04',
        '2024-07-04',
        false);

INSERT INTO project_tag(id, project_id, tag_id, created_at, updated_at)
VALUES (14, 9, 5, '2024-07-04', '2024-07-04');

INSERT INTO project_target(id, project_id, target_id, created_at, updated_at)
VALUES (9, 9, 3, '2024-07-04', '2024-07-04');
COMMIT;

START TRANSACTION;
INSERT INTO project(
    id,
    member_id,
    summary,
    start_date,
    end_date,
    project_title,
    main_image_path,
    sub_title_list,
    content_list,
    project_image_list,
    completed_status,
    created_at,
    updated_at,
    is_deleted)
VALUES (10,
        2,
        '동네 시장 비활성화 문제',
        '2024-07-05',
        '2024-12-05',
        '동네 주민 벼룩 시장 활성화 프로젝트',
        '프로젝트 대표사진1',
        JSON_ARRAY('소제목1', '소제목2'),
        JSON_ARRAY('본문1', '본문2'),
        JSON_ARRAY('프로젝트 사진1', '프로젝트 사진2', '프로젝트 사진3'),
        'COMPLETED',
        '2024-07-05',
        '2024-07-05',
        false);

INSERT INTO project_tag(id, project_id, tag_id, created_at, updated_at)
VALUES (15, 10, 7, '2024-07-05', '2024-07-05');

INSERT INTO project_target(id, project_id, target_id, created_at, updated_at)
VALUES (10, 10, 11, '2024-07-05', '2024-07-05');
COMMIT;

# 테스트용 프로젝트 좋아요
START TRANSACTION;

INSERT INTO likes(id, member_id, project_id)
VALUES (1, 1, 6);
INSERT INTO likes(id, member_id, project_id)
VALUES (2, 1, 7);
INSERT INTO likes(id, member_id, project_id)
VALUES (3, 1, 8);
INSERT INTO likes(id, member_id, project_id)
VALUES (4, 1, 9);
INSERT INTO likes(id, member_id, project_id)
VALUES (5, 1, 10);

INSERT INTO likes(id, member_id, project_id)
VALUES (6, 2, 6);
INSERT INTO likes(id, member_id, project_id)
VALUES (7, 2, 7);
INSERT INTO likes(id, member_id, project_id)
VALUES (8, 2, 8);
INSERT INTO likes(id, member_id, project_id)
VALUES (9, 2, 9);

INSERT INTO likes(id, member_id, project_id)
VALUES (10, 3, 6);
INSERT INTO likes(id, member_id, project_id)
VALUES (11, 3, 7);
INSERT INTO likes(id, member_id, project_id)
VALUES (12, 3, 8);

INSERT INTO likes(id, member_id, project_id)
VALUES (13, 4, 6);
INSERT INTO likes(id, member_id, project_id)
VALUES (14, 4, 7);

INSERT INTO likes(id, member_id, project_id)
VALUES (15, 5, 6);

COMMIT;