CREATE TABLE users (
    id UUID PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    real_username VARCHAR(100),
    username VARCHAR(100),
--     password VARCHAR(255),
    picture_url TEXT,
    status VARCHAR(50) NOT NULL DEFAULT 'UNVERIFIED', -- 'ACTIVE', 'UNVERIFIED', 'SUSPENDED', 'DELETED'
--     provider VARCHAR(50) NOT NULL, -- 'EMAIL', 'GOOGLE'
--     provider_id TEXT, -- 해당 Provider(구글 로그인)에서의 사용자 ID
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_users_username ON users(username);

CREATE TABLE roles (
    id UUID PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE, -- 'ROLE_ADMIN', 'ROLE_USER'
    description VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE user_roles (
    user_id UUID NOT NULL,
    role_id UUID NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (user_id, role_id),
    -- user_roles 테이블에 연관 데이터가 있는데 users 또는 roles 테이블의 데이터가 삭제되는 것을 방지 (RESTRICT)
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT,
    CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE RESTRICT
);

CREATE INDEX idx_user_roles_role_id ON user_roles(role_id);

CREATE TABLE refresh_tokens (
    id UUID PRIMARY KEY,
    token VARCHAR(512) NOT NULL UNIQUE,
    expiry_date TIMESTAMP WITH TIME ZONE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    user_id UUID NOT NULL,

    -- 사용자 삭제 시 해당 사용자의 리프레시 토큰도 함께 삭제된다. (CASCADE)
    CONSTRAINT fk_refresh_token_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_refresh_token_user_id ON refresh_token(user_id);
CREATE INDEX idx_refresh_token_expiry_date ON refresh_token(expiry_date);

CREATE TABLE credentials (
    id UUID PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    password VARCHAR(255),
    provider VARCHAR(50) NOT NULL, -- 'EMAIL', 'GOOGLE'
    provider_id TEXT, -- 해당 Provider(구글 로그인)에서의 사용자 ID
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    user_id UUID NOT NULL,

    CONSTRAINT uq_credentials_email UNIQUE (email),
    CONSTRAINT uq_credentials_provider_id UNIQUE (provider, provider_id),
    CONSTRAINT fk_credentials_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_credentials_user_id ON credentials(user_id);

CREATE TABLE blogs (
    id UUID PRIMARY KEY,
    slug VARCHAR(100) NOT NULL UNIQUE,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    user_id UUID NOT NULL,

    CONSTRAINT fk_blogs_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT
);

CREATE INDEX idx_blogs_user_id ON blogs(user_id);

CREATE TABLE categories (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    slug VARCHAR(255) NOT NULL,
    display_order INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    -- 부모 카테고리 ID (최상위 카테고리는 NULL)
    -- 카테고리 최대 2-depth 제한은 데이터베이스 레벨에서 강제하기 복잡하므로, 애플리케이션 레벨에서 제한한다.
    parent_id UUID,
    blog_id UUID NOT NULL,

    CONSTRAINT up_categories_blog_slug UNIQUE (blog_id, slug),
    -- 블로그 삭제 시 카테고리도 삭제된다. (CASCADE)
    CONSTRAINT fk_categories_blog FOREIGN KEY (blog_id) REFERENCES blogs(id) ON DELETE CASCADE,
    -- 부모 카테고리 삭제 시 자식 카테고리가 있으면 삭제 방지 (RESTRICT)
    CONSTRAINT fk_categories_parent FOREIGN KEY (parent_id) REFERENCES categories(id) ON DELETE RESTRICT
);

-- 최상위 카테고리는 블로그 내에서 이름이 중복될 수 없다.
CREATE UNIQUE INDEX uq_categories_toplevel ON categories(blog_id, name) WHERE parent_id IS NULL;
-- 하위 카테고리는 같은 부모 카테고리 아래에서 이름이 중복될 수 없다.
CREATE UNIQUE INDEX uq_categories_nested ON categories (blog_id, parent_id, name) WHERE parent_id IS NOT NULL;

CREATE INDEX idx_categories_blog_id ON categories(blog_id);
CREATE INDEX idx_categories_parent_id ON categories(parent_id);

CREATE TABLE posts (
    id UUID PRIMARY KEY,
    slug VARCHAR(255) NOT NULL,
    title VARCHAR(255) NOT NULL,
    content_markdown TEXT,
    summary VARCHAR(500),
    meta_title VARCHAR(255), -- SEO(검색 엔진), 소셜 미디어 공유를 위한 컬럼
    meta_description VARCHAR(500), -- SEO(검색 엔진), 소셜 미디어 공유를 위한 컬럼
    featured_image_url TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT', -- 'DRAFT', 'PUBLISHED', 'ARCHIVED', 'PUBLISHED'
    like_count INT NOT NULL DEFAULT 0,
    view_count INT NOT NULL DEFAULT 0,
    published_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    blog_id UUID NOT NULL,
    category_id UUID,

    CONSTRAINT uq_posts_blog_slug UNIQUE (blog_id, slug),
    -- 블로그 삭제 시 글도 삭제된다. (CASCADE)
    CONSTRAINT fk_posts_blog FOREIGN KEY (blog_id) REFERENCES blogs(id) ON DELETE CASCADE,
    -- 카테고리 삭제 시 해당 카테고리에 속한 글이 있으면 삭제 방지 (RESTRICT)
    CONSTRAINT fk_posts_category FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE RESTRICT
);

CREATE INDEX idx_posts_blog_id_status_published_at ON posts(blog_id, status, published_at DESC);
CREATE INDEX idx_posts_category_id ON posts(category_id);
CREATE INDEX idx_posts_status ON posts(status);

CREATE TABLE visit_logs (
    id UUID PRIMARY KEY,
    blog_id UUID NOT NULL,
    post_id UUID, -- NULL이면 블로그 홈 방문, 값이 있으면 특정 게시글 방문
    visitor_ip VARCHAR(45), -- IP 주소 (방문자 중복 제거용, 개인정보 보호를 위해 해싱 필요)
    user_agent TEXT, -- 기기 정보 (예: 모바일/PC 구분 등)
    referer_url TEXT, -- 유입 경로 (예: 검색 엔진, 소셜 미디어 등)
    visited_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_logs_blog FOREIGN KEY (blog_id) REFERENCES blogs(id) ON DELETE CASCADE
);

CREATE INDEX idx_visit_logs_date ON visit_logs (blog_id, visited_at);

CREATE TABLE daily_statistics (
    id UUID PRIMARY KEY,
    blog_id UUID NOT NULL,
    post_id UUID, -- 특정 게시글 통계라면 ID, 블로그 전체 통계라면 NULL
    stat_date DATE NOT NULL, -- 통계 날짜 (예: 2026-01-01)
    visit_count INT DEFAULT 0, -- 방문자 수 (UV (Unique View): IP 기준 중복 제거)
    view_count INT DEFAULT 0, -- 조회수 (PV (Page View): 단순 클릭 수)
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uq_daily_stats UNIQUE (blog_id, post_id, stat_date),
    CONSTRAINT fk_stats_blog FOREIGN KEY (blog_id) REFERENCES blogs(id) ON DELETE CASCADE
);

CREATE TABLE media (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL, -- 미디어를 업로드한 사용자 ID
    post_id UUID, -- 이 미디어가 사용된 게시글 ID (Nullable)
    file_name VARCHAR(255), -- 원본 파일명
    file_path TEXT NOT NULL, -- 스토리지 내 경로 또는 파일 시스템 경로
    file_url TEXT NOT NULL UNIQUE, -- 웹에서 접근 가능한 고유 URL (마크다운에 삽입될 값)
    mime_type VARCHAR(100), -- 파일 MIME 타입 (예: 'image/jpeg', 'image/png')
    file_size_bytes BIGINT,
    storage_type VARCHAR(50) DEFAULT 'LOCAL', -- 'LOCAL', 'S3', 'GCS'
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_media_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_media_post FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE SET NULL -- 게시글 삭제 시 미디어는 남기고 연결만 끊음
);

CREATE INDEX idx_media_user_id ON media(user_id);
CREATE INDEX idx_media_post_id ON media(post_id);
CREATE INDEX idx_media_file_url ON media(file_url);

CREATE TABLE subscriptions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL,
    blog_id UUID NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uq_subscriptions_user_blog UNIQUE (user_id, blog_id),
    CONSTRAINT fk_subscriptions_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_subscriptions_blog FOREIGN KEY (blog_id) REFERENCES blogs(id) ON DELETE CASCADE
);

CREATE INDEX idx_subscriptions_user_id ON subscriptions(user_id);
CREATE INDEX idx_subscriptions_blog_id ON subscriptions(blog_id);

CREATE TABLE comments (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    post_id UUID NOT NULL,
    user_id UUID, -- Nullable for deleted users
    parent_comment_id UUID, -- Nullable for top-level comments
    content TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP WITH TIME ZONE,

    CONSTRAINT fk_comments_post FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE,
    CONSTRAINT fk_comments_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT fk_comments_parent FOREIGN KEY (parent_comment_id) REFERENCES comments(id) ON DELETE SET NULL -- 부모 삭제 시 연결만 끊음
);

CREATE INDEX idx_comments_post_id_created_at ON comments(post_id, created_at ASC);
CREATE INDEX idx_comments_parent_comment_id ON comments(parent_comment_id);
CREATE INDEX idx_comments_user_id ON comments(user_id);

CREATE TABLE notifications (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    recipient_user_id UUID NOT NULL,
    type VARCHAR(50) NOT NULL, -- 어떤 종류의 이벤트로 인해 알림이 발생했는지 구분하기 위함. 'new_post', 'new_comment', 'new_reply', 'new_subscriber'
    related_entity_id UUID, -- 해당 알림과 가장 직접적으로 관련된 데이터의 고유 ID를 명시한다.
    related_entity_type VARCHAR(50), -- 해당 알림과 가장 직접적으로 관련된 데이터가 어떤 테이블의 것인지를 명시한다. 예) type이 'new_post'이면 'post'
    message TEXT,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_notifications_recipient FOREIGN KEY (recipient_user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_notifications_recipient_user_id_created_at ON notifications(recipient_user_id, created_at DESC);
CREATE INDEX idx_notifications_recipient_user_id_is_read ON notifications(recipient_user_id, is_read);