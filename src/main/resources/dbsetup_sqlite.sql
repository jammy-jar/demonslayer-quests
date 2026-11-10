CREATE TABLE IF NOT EXISTS player_quests (
    id          INTEGER PRIMARY KEY     NOT NULL,
    uuid        CHAR(36)                NOT NULL,
    quest_id    INTEGER                 NOT NULL,
    active      BIT                     NOT NULL,
    UNIQUE(uuid, quest_id)
);

CREATE TABLE IF NOT EXISTS quest_progress (
    player_quest_id INTEGER         NOT NULL,
    progress        INTEGER         NOT NULL,
    progress_type   VARCHAR(64)     NOT NULL,
    FOREIGN KEY (player_quest_id) REFERENCES player_quests(id)
);

CREATE TABLE IF NOT EXISTS quest_level (
    player_quest_id INTEGER        NOT NULL,
    lvl             INTEGER        NOT NULL,
    FOREIGN KEY (player_quest_id) REFERENCES player_quests(id)
);

CREATE TABLE IF NOT EXISTS quest (
    id      INTEGER PRIMARY KEY    NOT NULL,
    name    VARCHAR(64)     NOT NULL,
    type    VARCHAR(64)     NOT NULL
);

