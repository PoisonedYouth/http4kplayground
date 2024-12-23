create table APP_USER
(
    ID       UUID                   not null
        primary key,
    USERNAME CHARACTER VARYING(255) not null
        constraint USER_USERNAME_UNIQUE
            unique
);

create table CHAT
(
    ID         UUID         not null
        primary key,
    CREATED_AT TIMESTAMP(9) not null,
    OWNER      UUID         not null,
    constraint FK_CHAT_USER__ID
        foreign key (OWNER) references APP_USER
);

create table CHAT_MESSAGE
(
    ID         UUID              not null
        primary key,
    CHAT_ID    UUID              not null,
    MESSAGE    CHARACTER VARYING not null,
    CREATED_BY UUID              not null,
    CREATED_AT TIMESTAMP(9)      not null,
    constraint FK_CHAT_MESSAGE_CHAT_ID__ID
        foreign key (CHAT_ID) references CHAT
);
create table CHAT_USER
(
    ID      UUID not null
        primary key,
    CHAT_ID UUID not null,
    USER_ID UUID not null,
    constraint FK_CHAT_USER_CHAT_ID__ID
        foreign key (CHAT_ID) references CHAT,
    constraint FK_CHAT_USER_USER_ID__ID
        foreign key (USER_ID) references APP_USER
);