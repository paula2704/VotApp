CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL
);

CREATE TABLE surveys (
    id BIGSERIAL PRIMARY KEY,
    question VARCHAR(500) NOT NULL,
    created_by BIGINT NOT NULL REFERENCES users(id),
    active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE options (
    id BIGSERIAL PRIMARY KEY,
    survey_id BIGINT NOT NULL REFERENCES surveys(id) ON DELETE CASCADE,
    text VARCHAR(255) NOT NULL
);

CREATE TABLE votes (
    id BIGSERIAL PRIMARY KEY,
    survey_id BIGINT NOT NULL REFERENCES surveys(id),
    option_id BIGINT NOT NULL REFERENCES options(id),
    user_id BIGINT NOT NULL REFERENCES users(id),
    voted_at TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE(survey_id, user_id)
);