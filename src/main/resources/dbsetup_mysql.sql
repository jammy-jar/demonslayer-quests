CREATE TABLE IF NOT EXISTS player_quests (
    id          INT UNSIGNED    NOT NULL    AUTO_INCREMENT,
    uuid        CHAR(36)        NOT NULL,
    quest_id    INT UNSIGNED    NOT NULL,
    active      BIT             NOT NULL,
    UNIQUE INDEX player_quest(uuid, quest_id),
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS quest_progress (
    player_quest_id INT UNSIGNED        NOT NULL,
    progress        INT   NOT NULL,
    progress_type   VARCHAR(64)         NOT NULL,
    FOREIGN KEY (player_quest_id) REFERENCES player_quests(id)
);

CREATE TABLE IF NOT EXISTS quest_level (
    player_quest_id INT UNSIGNED       NOT NULL,
    lvl             SMALLINT UNSIGNED  NOT NULL,
    FOREIGN KEY (player_quest_id) REFERENCES player_quests(id)
);

CREATE TABLE IF NOT EXISTS quest (
    id      INT UNSIGNED    NOT NULL    AUTO_INCREMENT,
    name    VARCHAR(64)     NOT NULL,
    type    VARCHAR(64)     NOT NULL,
    PRIMARY KEY (id)
);


