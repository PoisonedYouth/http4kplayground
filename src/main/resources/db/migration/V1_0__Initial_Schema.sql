CREATE TABLE app_user
(
    id       UUID NOT NULL PRIMARY KEY,
    username CHARACTER VARYING(255) NOT NULL CONSTRAINT user_username_unique
        UNIQUE
);

CREATE TABLE chat
(
    id         UUID NOT NULL PRIMARY KEY,
    created_at TIMESTAMP(9) NOT NULL,
    owner      UUID NOT NULL,
    CONSTRAINT fk_chat_user__id FOREIGN KEY (owner) REFERENCES app_user
);

CREATE TABLE chat_message
(
    id         UUID NOT NULL PRIMARY KEY,
    chat_id    UUID NOT NULL,
    message    CHARACTER VARYING NOT NULL,
    created_by UUID NOT NULL,
    created_at TIMESTAMP(9) NOT NULL,
    CONSTRAINT fk_chat_message_chat_id__id FOREIGN KEY (chat_id) REFERENCES
        chat
);

CREATE TABLE chat_user
(
    id      UUID NOT NULL PRIMARY KEY,
    chat_id UUID NOT NULL,
    user_id UUID NOT NULL,
    CONSTRAINT fk_chat_user_chat_id__id FOREIGN KEY (chat_id) REFERENCES chat,
    CONSTRAINT fk_chat_user_user_id__id FOREIGN KEY (user_id) REFERENCES
        app_user
);