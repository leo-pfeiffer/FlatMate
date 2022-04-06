PRAGMA
    foreign_keys = TRUE;

DROP TABLE IF EXISTS "list_item";
DROP TABLE IF EXISTS "list";
DROP TABLE IF EXISTS "user_bill";
DROP TABLE IF EXISTS "bill";
DROP TABLE IF EXISTS "user";
DROP TABLE IF EXISTS "group";
DROP TABLE IF EXISTS "experiment_tracker";

CREATE TABLE "group"
(
    group_id INTEGER PRIMARY KEY AUTOINCREMENT,
    name     VARCHAR(30) NOT NULL UNIQUE
);

CREATE TABLE "user"
(
    username VARCHAR(20) PRIMARY KEY,
    password VARCHAR(64) NOT NULL,
    group_id INTEGER,
    role     VARCHAR(20) NOT NULL,
    enabled  BOOLEAN     NOT NULL,
    FOREIGN KEY (group_id) REFERENCES "group",
    CHECK (role IN ('USER', 'ADMIN'))
);

CREATE TABLE "bill"
(
    bill_id        INTEGER PRIMARY KEY AUTOINCREMENT,
    name           VARCHAR(30)   NOT NULL,
    description    VARCHAR(100),
    amount         DECIMAL(5, 2) NOT NULL,
    create_time    INTEGER       NOT NULL, -- UNIX timestamp
    payment_method VARCHAR(30),
    owner          VARCHAR(20)   NOT NULL,
    FOREIGN KEY (owner) REFERENCES "user" ON UPDATE CASCADE
);

CREATE TABLE "user_bill"
(
    user_bill_id INTEGER PRIMARY KEY AUTOINCREMENT,
    username     VARCHAR(20)   NOT NULL,
    bill_id      INTEGER       NOT NULL,
    percentage   DECIMAL(3, 2) NOT NULL,
    paid         INTEGER       NOT NULL,
    FOREIGN KEY (username) REFERENCES "user" ON UPDATE CASCADE,
    FOREIGN KEY (bill_id) REFERENCES "bill" ON UPDATE CASCADE,
    CHECK (paid IN (0, 1))
);

CREATE TABLE "list"
(
    list_id     INTEGER PRIMARY KEY AUTOINCREMENT,
    name        VARCHAR(30) NOT NULL,
    description VARCHAR(100),
    create_time INTEGER     NOT NULL, -- UNIX timestamp
    owner       VARCHAR(20) NOT NULL,
    bill_id     INTEGER,
    FOREIGN KEY (owner) REFERENCES "user" ON UPDATE CASCADE,
    FOREIGN KEY (bill_id) REFERENCES "bill" ON UPDATE CASCADE
);

CREATE TABLE list_item
(
    list_item_id INTEGER PRIMARY KEY AUTOINCREMENT,
    name         VARCHAR(30) NOT NULL,
    list_id      INTEGER     NOT NULL,
    FOREIGN KEY (list_id) REFERENCES "list" ON UPDATE CASCADE
);


-- A/B Testing
CREATE TABLE experiment_tracker
(
    experiment_tracker_id INTEGER PRIMARY KEY AUTOINCREMENT,
    experiment            VARCHAR(100) NOT NULL,
    variant               VARCHAR(100) NOT NULL,
    event                 VARCHAR(100) NOT NULL
)