CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    email VARCHAR(255) UNIQUE NOT NULL,
    username VARCHAR(100) UNIQUE, -- 사용자 별명 (닉네임)
    real_username VARCHAR(100),
    password VARCHAR(255),
    picture_url TEXT,
    provider VARCHAR(50) NOT NULL, -- 'EMAIL', 'GOOGLE'
    provider_user_id TEXT, -- 해당 Provider에서의 사용자 ID
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
);

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_username ON users(username);

CREATE TABLE roles (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE, -- 'ROLE_ADMIN', 'ROLE_USER'
    description VARCHAR(255)
);

INSERT INTO roles (name) VALUES ('ROLE_ADMIN'), ('ROLE_USER') ON CONFLICT (name) DO NOTHING;

CREATE TABLE user_roles (
    user_id UUID NOT NULL,
    role_id INTEGER NOT NULL,

    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

CREATE INDEX idx_user_roles_user_id ON user_roles(user_id);
CREATE INDEX idx_user_roles_role_id ON user_roles(role_id);

CREATE TABLE blogs (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL,
    slug VARCHAR(100) NOT NULL UNIQUE,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_blogs_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT
);

CREATE INDEX idx_blogs_slug ON blogs(slug);
CREATE INDEX idx_blogs_user_id ON blogs(user_id);

CREATE TABLE posts (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    blog_id UUID NOT NULL,
    slug VARCHAR(255) NOT NULL, 
    title VARCHAR(255) NOT NULL,
    content_markdown TEXT,
    content_html TEXT,
    meta_title VARCHAR(255),
    meta_description VARCHAR(500),
    featured_image_url TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'draft',
    published_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uq_posts_blog_slug UNIQUE (blog_id, slug), -- 복합 유니크 제약조건
    CONSTRAINT fk_posts_blog FOREIGN KEY (blog_id) REFERENCES blogs(id) ON DELETE CASCADE,
);

CREATE INDEX idx_posts_blog_id_status_published_at ON posts(blog_id, status, published_at);
CREATE INDEX idx_posts_blog_id_slug ON posts(blog_id, slug);
CREATE INDEX idx_posts_status ON posts(status);

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