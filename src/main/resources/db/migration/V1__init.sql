CREATE TABLE IF NOT EXISTS admin_member (
    id BIGINT NOT NULL AUTO_INCREMENT,
    username VARCHAR(20) NOT NULL,
    password VARCHAR(64) NOT NULL,
    last_login_date DATETIME(6) NOT NULL,
    admin_type ENUM ('ADMIN','MASTER'),
    status ENUM ('ACTIVE','DELETED','DORMANT'),
    created_at DATETIME(6),
    updated_at DATETIME(6),
    PRIMARY KEY (id)
) engine=InnoDB;

CREATE TABLE IF NOT EXISTS assistant (
    id BIGINT AUTO_INCREMENT,
    name VARCHAR(20) NOT NULL,
    assistant_id VARCHAR(50) NOT NULL,
    model VARCHAR(20) NOT NULL,
    created_at DATETIME(6),
    status ENUM ('USABLE','DELETED'),
    PRIMARY KEY (id)
) engine=InnoDB;

CREATE TABLE IF NOT EXISTS member (
    id BIGINT NOT NULL AUTO_INCREMENT,
    social_login_id VARCHAR(50) NOT NULL,
    email VARCHAR(30) NOT NULL,
    nickname VARCHAR(20) NOT NULL,
    profile_img VARCHAR(255),
    introduction VARCHAR(50),
    created_at DATETIME(6),
    updated_at DATETIME(6),
    status ENUM ('ACTIVE','DELETED','DORMANT'),
    PRIMARY KEY (id)
) engine=InnoDB;

CREATE TABLE IF NOT EXISTS chat_room (
    id BIGINT AUTO_INCREMENT,
    member_id BIGINT NOT NULL,
    chat_room_name VARCHAR(30),
    thread VARCHAR(255) NOT NULL,
    is_summarized BOOLEAN NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    status ENUM ('USABLE','DELETED'),
    FOREIGN KEY (member_id) REFERENCES member(id),
    PRIMARY KEY (id)
) engine=InnoDB;

CREATE TABLE IF NOT EXISTS project (
    id BIGINT AUTO_INCREMENT,
    member_id BIGINT,
    summary VARCHAR(20) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    project_title VARCHAR(50) NOT NULL,
    main_image_path VARCHAR(255) NOT NULL,
    sub_title_list VARCHAR(180),
    content_list VARCHAR(3000),
    project_image_list VARCHAR(1000),
    completed_status ENUM ('COMPLETED','NOT_COMPLETED','CLOSED'),
    created_at DATETIME(6),
    updated_at DATETIME(6),
    status ENUM ('USABLE','DELETED'),
    FOREIGN KEY (member_id) REFERENCES member(id),
    PRIMARY KEY (id)
    ) engine=InnoDB;

CREATE TABLE IF NOT EXISTS likes (
    id BIGINT AUTO_INCREMENT,
    project_id BIGINT NOT NULL,
    member_id BIGINT NOT NULL,
    created_at DATETIME(6),
    status ENUM ('USABLE','DELETED'),
    FOREIGN KEY (member_id) REFERENCES member (id),
    FOREIGN KEY (project_id) REFERENCES project (id),
    PRIMARY KEY (id)
) engine=InnoDB;

CREATE TABLE IF NOT EXISTS message (
    id BIGINT AUTO_INCREMENT,
    chat_room_id BIGINT NOT NULL,
    contents VARCHAR(1000) NOT NULL,
    sender_type ENUM ('MEMBER','AURORA_AI'),
    created_at DATETIME(6),
    status ENUM ('USABLE','DELETED'),
    FOREIGN KEY (chat_room_id) REFERENCES chat_room(id),
    PRIMARY KEY (id)
) engine=InnoDB;

CREATE TABLE IF NOT EXISTS note (
    id BIGINT AUTO_INCREMENT,
    target VARCHAR(20),
    problem VARCHAR(100),
    title VARCHAR(50),
    open_title_list VARCHAR(180),
    open_summary_list VARCHAR(300),
    solution VARCHAR(255),
    chat_room_id BIGINT NOT NULL,
    created_at DATETIME(6),
    updated_at DATETIME(6),
    status ENUM ('USABLE','DELETED'),
    FOREIGN KEY (chat_room_id) REFERENCES chat_room(id),
    PRIMARY KEY (id)
) engine=InnoDB;

CREATE TABLE IF NOT EXISTS tag (
    id BIGINT AUTO_INCREMENT,
    tag_title VARCHAR(10) NOT NULL,
    PRIMARY KEY (id)
) engine=InnoDB;

CREATE TABLE IF NOT EXISTS target (
    id BIGINT AUTO_INCREMENT,
    target_title VARCHAR(10) NOT NULL,
    PRIMARY KEY (id)
) engine=InnoDB;

CREATE TABLE IF NOT EXISTS project_tag (
    id BIGINT AUTO_INCREMENT,
    project_id BIGINT,
    tag_id BIGINT,
    created_at DATETIME(6),
    status ENUM ('USABLE','DELETED'),
    FOREIGN KEY (project_id) REFERENCES project(id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES tag(id) ON DELETE CASCADE,
    PRIMARY KEY (id)
) engine=InnoDB;

CREATE TABLE IF NOT EXISTS project_target (
    id BIGINT AUTO_INCREMENT,
    project_id BIGINT,
    target_id BIGINT,
    created_at DATETIME(6),
    status ENUM ('USABLE','DELETED'),
    FOREIGN KEY (project_id) REFERENCES project(id) ON DELETE CASCADE,
    FOREIGN KEY (target_id) REFERENCES target(id) ON DELETE CASCADE,
    PRIMARY KEY (id)
) engine=InnoDB;

-- 초기 태그 데이터 삽입
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

-- 초기 대상 데이터 삽입
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








