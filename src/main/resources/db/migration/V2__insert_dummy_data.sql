-- 테스트용 멤버 데이터
START TRANSACTION;
INSERT INTO member (id, email, social_login_id, nickname, status, created_at, updated_at)
VALUES (1, 'member1@test.com', 'test_0001', '윤제민' ,'ACTIVE', '2024-07-01', '2024-07-01');

INSERT INTO member (id, email, social_login_id, nickname, status, created_at, updated_at)
VALUES (2, 'member2@test.com', 'test_0002', '권동민' ,'ACTIVE', '2024-07-03', '2024-07-03');

INSERT INTO member (id, email, social_login_id, nickname, status, created_at, updated_at)
VALUES (3, 'member3@test.com', 'test_0003', '신유나' ,'ACTIVE', '2024-07-03', '2024-07-03');

INSERT INTO member (id, email, social_login_id, nickname, status, created_at, updated_at)
VALUES (4, 'member4@test.com', 'test_0004', '김서연' ,'ACTIVE', '2024-07-03', '2024-07-03');

INSERT INTO member (id, email, social_login_id, nickname, status, created_at, updated_at)
VALUES (5, 'member5@test.com', 'test_0005', '김태형' ,'ACTIVE', '2024-07-03', '2024-07-03');

COMMIT;

-- 테스트용 프로젝트 데이터
START TRANSACTION;
INSERT INTO project(id, member_id, summary, start_date, end_date, project_title, main_image_path, sub_title_list, content_list, project_image_list, completed_status, created_at, updated_at, status)
VALUES (1,
        1,
        '장년층의 취업 문제',
        '2024-07-01',
        '2024-10-01',
        '은퇴 후 사업 시작 안전하게!',
        'https://trackers-aurora-dev-bucket.s3.ap-northeast-2.amazonaws.com/local/project1.png',
        JSON_ARRAY('소제목1', '소제목2'),
        JSON_ARRAY('본문1', '본문2'),
        JSON_ARRAY('프로젝트 사진1', '프로젝트 사진2', '프로젝트 사진3'),
        'COMPLETED',
        '2024-07-01',
        '2024-07-01',
        'USABLE'
       );

INSERT INTO project_tag(id, project_id, tag_id, created_at, status)
VALUES (1, 1, 8, '2024-07-01', 'USABLE');

INSERT INTO project_tag(id, project_id, tag_id, created_at,  status)
VALUES (2, 1, 10, '2024-07-01', 'USABLE');


INSERT INTO project_target(id, project_id, target_id, created_at,  status)
VALUES (1, 1, 5, '2024-07-01',  'USABLE');
COMMIT;

START TRANSACTION;
INSERT INTO project(id, member_id, summary, start_date, end_date, project_title, main_image_path, sub_title_list, content_list, project_image_list, completed_status, created_at, updated_at, status)
VALUES (2,
        1,
        '청소년 무기력증 문제',
        '2024-07-02',
        '2024-12-02',
        '청소년 꿈찾기 프로젝트',
        'https://trackers-aurora-dev-bucket.s3.ap-northeast-2.amazonaws.com/local/project2.png',
        JSON_ARRAY('소제목1', '소제목2'),
        JSON_ARRAY('본문1', '본문2'),
        JSON_ARRAY('프로젝트 사진1', '프로젝트 사진2', '프로젝트 사진3'),
        'COMPLETED',
        '2024-07-02',
        '2024-07-02',
        'USABLE');

INSERT INTO project_tag(id, project_id, tag_id, created_at, status)
VALUES (3, 2, 1, '2024-07-02', 'USABLE');

INSERT INTO project_tag(id, project_id, tag_id, created_at, status)
VALUES (4, 2, 10, '2024-07-01', 'USABLE');

INSERT INTO project_target(id, project_id, target_id, created_at, status)
VALUES (2, 2, 2, '2024-07-02', 'USABLE');
COMMIT;

START TRANSACTION;
INSERT INTO project(id, member_id, summary, start_date, end_date, project_title, main_image_path, sub_title_list, content_list, project_image_list, completed_status, created_at, updated_at, status)
VALUES (3,
        1,
        '영유아 유기 문제',
        '2024-07-03',
        '2024-12-03',
        '영유아의 안정적인 보금자리',
        'https://trackers-aurora-dev-bucket.s3.ap-northeast-2.amazonaws.com/local/project3.png',
        JSON_ARRAY('소제목1', '소제목2'),
        JSON_ARRAY('본문1', '본문2'),
        JSON_ARRAY('프로젝트 사진1', '프로젝트 사진2', '프로젝트 사진3'),
        'COMPLETED',
        '2024-07-03',
        '2024-07-03',
        'USABLE');

INSERT INTO project_tag(id, project_id, tag_id, created_at, status)
VALUES (5, 3, 2, '2024-07-03', 'USABLE');

INSERT INTO project_tag(id, project_id, tag_id, created_at, status)
VALUES (6, 3, 11, '2024-07-01', 'USABLE');

INSERT INTO project_target(id, project_id, target_id, created_at, status)
VALUES (3, 3, 1, '2024-07-03', 'USABLE');
COMMIT;

START TRANSACTION;
INSERT INTO project( id, member_id, summary, start_date, end_date, project_title, main_image_path, sub_title_list, content_list, project_image_list, completed_status, created_at, updated_at, status)
VALUES (4,
        1,
        '여성 치안 문제',
        '2024-07-04',
        '2024-12-04',
        '여성의 안심 귀가 프로젝트',
        'https://trackers-aurora-dev-bucket.s3.ap-northeast-2.amazonaws.com/local/project4.png',
        JSON_ARRAY('소제목1', '소제목2'),
        JSON_ARRAY('본문1', '본문2'),
        JSON_ARRAY('프로젝트 사진1', '프로젝트 사진2', '프로젝트 사진3'),
        'COMPLETED',
        '2024-07-04',
        '2024-07-04',
        'USABLE');

INSERT INTO project_tag(id, project_id, tag_id, created_at, status)
VALUES (7, 4, 8, '2024-07-04', 'USABLE');

INSERT INTO project_tag(id, project_id, tag_id, created_at, status)
VALUES (8, 4, 11, '2024-07-01', 'USABLE');

INSERT INTO project_target(id, project_id, target_id, created_at, status)
VALUES (4, 4, 4, '2024-07-04', 'USABLE');
COMMIT;

START TRANSACTION;
INSERT INTO project( id, member_id, summary, start_date, end_date, project_title, main_image_path, sub_title_list, content_list, project_image_list, completed_status, created_at, updated_at, status)
VALUES (5,
        1,
        '장애인 실업 문제',
        '2024-07-05',
        '2024-12-05',
        '장애인 일자리 창출 프로젝트',
        'https://trackers-aurora-dev-bucket.s3.ap-northeast-2.amazonaws.com/local/project5.png',
        JSON_ARRAY('소제목1', '소제목2'),
        JSON_ARRAY('본문1', '본문2'),
        JSON_ARRAY('프로젝트 사진1', '프로젝트 사진2', '프로젝트 사진3'),
        'COMPLETED',
        '2024-07-05',
        '2024-07-05',
        'USABLE');

INSERT INTO project_tag(id, project_id, tag_id, created_at, status)
VALUES (9, 5, 8, '2024-07-05', 'USABLE');

INSERT INTO project_tag(id, project_id, tag_id, created_at, status)
VALUES (10, 5, 11, '2024-07-01', 'USABLE');

INSERT INTO project_target(id, project_id, target_id, created_at, status)
VALUES (5, 5, 6, '2024-07-05', 'USABLE');
COMMIT;

START TRANSACTION;
INSERT INTO project( id, member_id, summary, start_date, end_date, project_title, main_image_path, sub_title_list, content_list, project_image_list, completed_status, created_at, updated_at, status)
VALUES (6,
        2,
        '동해 쓰레기 문제',
        '2024-07-01',
        '2024-12-01',
        '쓰레기 없는 동해 프로젝트',
        'https://trackers-aurora-dev-bucket.s3.ap-northeast-2.amazonaws.com/local/project6.png',
        JSON_ARRAY('소제목1', '소제목2'),
        JSON_ARRAY('본문1', '본문2'),
        JSON_ARRAY('프로젝트 사진1', '프로젝트 사진2', '프로젝트 사진3'),
        'COMPLETED',
        '2024-07-01',
        '2024-07-01',
        'USABLE');

INSERT INTO project_tag(id, project_id, tag_id, created_at, status)
VALUES (11, 6, 8, '2024-07-01', 'USABLE');

INSERT INTO project_target(id, project_id, target_id, created_at, status)
VALUES (6, 6, 11, '2024-07-01', 'USABLE');
COMMIT;

START TRANSACTION;
INSERT INTO project( id, member_id, summary, start_date, end_date, project_title, main_image_path, sub_title_list, content_list, project_image_list, completed_status, created_at, updated_at, status)
VALUES (7,
        2,
        '고속도로 로드킬 문제',
        '2024-07-02',
        '2024-12-02',
        '고속도로 서식 야생도물 보호 프로젝트',
        'https://trackers-aurora-dev-bucket.s3.ap-northeast-2.amazonaws.com/local/project7.png',
        JSON_ARRAY('소제목1', '소제목2'),
        JSON_ARRAY('본문1', '본문2'),
        JSON_ARRAY('프로젝트 사진1', '프로젝트 사진2', '프로젝트 사진3'),
        'COMPLETED',
        '2024-07-02',
        '2024-07-02',
        'USABLE');

INSERT INTO project_tag(id, project_id, tag_id, created_at, status)
VALUES (12, 7, 9, '2024-07-02', 'USABLE');

INSERT INTO project_target(id, project_id, target_id, created_at, status)
VALUES (7, 7, 13, '2024-07-02', 'USABLE');
COMMIT;

START TRANSACTION;
INSERT INTO project( id, member_id, summary, start_date, end_date, project_title, main_image_path, sub_title_list, content_list, project_image_list, completed_status, created_at, updated_at, status)
VALUES (8,
        2,
        '탈북민 부적응 문제',
        '2024-07-03',
        '2024-12-03',
        '탈북민 정착 지원 프로젝트',
        'https://trackers-aurora-dev-bucket.s3.ap-northeast-2.amazonaws.com/local/project8.png',
        JSON_ARRAY('소제목1', '소제목2'),
        JSON_ARRAY('본문1', '본문2'),
        JSON_ARRAY('프로젝트 사진1', '프로젝트 사진2', '프로젝트 사진3'),
        'COMPLETED',
        '2024-07-03',
        '2024-07-03',
        'USABLE');

INSERT INTO project_tag(id, project_id, tag_id, created_at, status)
VALUES (13, 8, 7, '2024-07-03', 'USABLE');

INSERT INTO project_target(id, project_id, target_id, created_at, status)
VALUES (8, 8, 7, '2024-07-03', 'USABLE');
COMMIT;

START TRANSACTION;
INSERT INTO project(id, member_id, summary, start_date, end_date, project_title, main_image_path, sub_title_list, content_list, project_image_list, completed_status, created_at, updated_at, status)
VALUES (9,
        2,
        '직장내 인격 모독 문제',
        '2024-07-04',
        '2024-12-04',
        '직장인 대상 인권 교육 프로젝트',
        'https://trackers-aurora-dev-bucket.s3.ap-northeast-2.amazonaws.com/local/project9.png',
        JSON_ARRAY('소제목1', '소제목2'),
        JSON_ARRAY('본문1', '본문2'),
        JSON_ARRAY('프로젝트 사진1', '프로젝트 사진2', '프로젝트 사진3'),
        'COMPLETED',
        '2024-07-04',
        '2024-07-04',
        'USABLE');

INSERT INTO project_tag(id, project_id, tag_id, created_at, status)
VALUES (14, 9, 5, '2024-07-04', 'USABLE');

INSERT INTO project_target(id, project_id, target_id, created_at, status)
VALUES (9, 9, 3, '2024-07-04','USABLE');
COMMIT;

START TRANSACTION;
INSERT INTO project( id, member_id, summary, start_date, end_date, project_title, main_image_path, sub_title_list, content_list, project_image_list, completed_status, created_at, updated_at, status)
VALUES (10,
        2,
        '동네 시장 비활성화 문제',
        '2024-07-05',
        '2024-12-05',
        '동네 주민 벼룩 시장 활성화 프로젝트',
        'https://trackers-aurora-dev-bucket.s3.ap-northeast-2.amazonaws.com/local/project10.png',
        JSON_ARRAY('소제목1', '소제목2'),
        JSON_ARRAY('본문1', '본문2'),
        JSON_ARRAY('프로젝트 사진1', '프로젝트 사진2', '프로젝트 사진3'),
        'COMPLETED',
        '2024-07-05',
        '2024-07-05',
        'USABLE');

INSERT INTO project_tag(id, project_id, tag_id, created_at, status)
VALUES (15, 10, 7, '2024-07-05', 'USABLE');

INSERT INTO project_target(id, project_id, target_id, created_at, status)
VALUES (10, 10, 11, '2024-07-05', 'USABLE');
COMMIT;

-- 테스트용 프로젝트 좋아요
START TRANSACTION;

INSERT INTO likes(id, member_id, project_id, created_at)
VALUES (1, 1, 6, '2024-07-01');
INSERT INTO likes(id, member_id, project_id, created_at)
VALUES (2, 1, 7, '2024-07-02');
INSERT INTO likes(id, member_id, project_id, created_at)
VALUES (3, 1, 8, '2024-07-03');
INSERT INTO likes(id, member_id, project_id, created_at)
VALUES (4, 1, 9, '2024-07-04');
INSERT INTO likes(id, member_id, project_id, created_at)
VALUES (5, 1, 10, '2024-07-05');

INSERT INTO likes(id, member_id, project_id, created_at)
VALUES (6, 2, 6, '2024-07-01');
INSERT INTO likes(id, member_id, project_id, created_at)
VALUES (7, 2, 7, '2024-07-02');
INSERT INTO likes(id, member_id, project_id, created_at)
VALUES (8, 2, 8, '2024-07-03');
INSERT INTO likes(id, member_id, project_id, created_at)
VALUES (9, 2, 9, '2024-07-04');

INSERT INTO likes(id, member_id, project_id, created_at)
VALUES (10, 3, 6, '2024-07-01');
INSERT INTO likes(id, member_id, project_id, created_at)
VALUES (11, 3, 7, '2024-07-02');
INSERT INTO likes(id, member_id, project_id, created_at)
VALUES (12, 3, 8, '2024-07-03');

INSERT INTO likes(id, member_id, project_id, created_at)
VALUES (13, 4, 6, '2024-07-01');
INSERT INTO likes(id, member_id, project_id, created_at)
VALUES (14, 4, 7, '2024-07-02');

INSERT INTO likes(id, member_id, project_id, created_at)
VALUES (15, 5, 6, '2024-07-01');

COMMIT;