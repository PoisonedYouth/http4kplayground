CREATE TABLE event
(
    id         UUID NOT NULL PRIMARY KEY,
    created_at TIMESTAMP(9) NOT NULL,
    payload    TEXT NOT NULL,
    event_type       VARCHAR NOT NULL
);