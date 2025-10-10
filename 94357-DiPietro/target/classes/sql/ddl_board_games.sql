-- =====================================================================
-- BOARD GAMES – Normalización de DESIGNER, PUBLISHER y CATEGORY
-- Motor: H2 2.x
-- =====================================================================

-- ---------------------------------------------------------------------
-- Limpieza previa (idempotente)
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS BOARD_GAMES;
DROP TABLE IF EXISTS DESIGNERS;
DROP TABLE IF EXISTS PUBLISHERS;
DROP TABLE IF EXISTS CATEGORIES;

DROP SEQUENCE IF EXISTS SEQ_BOARD_GAME_ID;
DROP SEQUENCE IF EXISTS SEQ_DESIGNER_ID;
DROP SEQUENCE IF EXISTS SEQ_PUBLISHER_ID;
DROP SEQUENCE IF EXISTS SEQ_CATEGORY_ID;

-- ---------------------------------------------------------------------
-- Secuencias (estrategia autonumérica)
-- ---------------------------------------------------------------------
CREATE SEQUENCE IF NOT EXISTS SEQ_BOARD_GAME_ID START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS SEQ_DESIGNER_ID   START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS SEQ_PUBLISHER_ID  START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS SEQ_CATEGORY_ID   START WITH 1 INCREMENT BY 1;

-- ---------------------------------------------------------------------
-- Catálogos normalizados
-- ---------------------------------------------------------------------

-- Diseñadores
CREATE TABLE DESIGNERS (
    ID_DESIGNER INTEGER      NOT NULL DEFAULT NEXT VALUE FOR SEQ_DESIGNER_ID,
    NAME        VARCHAR(160) NOT NULL,
    CONSTRAINT PK_DESIGNERS PRIMARY KEY (ID_DESIGNER),
    CONSTRAINT UK_DESIGNERS_NAME UNIQUE (NAME)
);

-- Editoriales
CREATE TABLE PUBLISHERS (
    ID_PUBLISHER INTEGER      NOT NULL DEFAULT NEXT VALUE FOR SEQ_PUBLISHER_ID,
    NAME         VARCHAR(160) NOT NULL,
    CONSTRAINT PK_PUBLISHERS PRIMARY KEY (ID_PUBLISHER),
    CONSTRAINT UK_PUBLISHERS_NAME UNIQUE (NAME)
);

-- Categorías / Géneros
CREATE TABLE CATEGORIES (
    ID_CATEGORY INTEGER      NOT NULL DEFAULT NEXT VALUE FOR SEQ_CATEGORY_ID,
    NAME        VARCHAR(120) NOT NULL,
    CONSTRAINT PK_CATEGORIES PRIMARY KEY (ID_CATEGORY),
    CONSTRAINT UK_CATEGORIES_NAME UNIQUE (NAME)
);

-- ---------------------------------------------------------------------
-- Tabla base: BOARD_GAMES
-- ---------------------------------------------------------------------
CREATE TABLE BOARD_GAMES (
    ID_GAME         INTEGER       NOT NULL DEFAULT NEXT VALUE FOR SEQ_BOARD_GAME_ID,
    NAME            VARCHAR(200)  NOT NULL,
    YEAR_PUBLISHED  INTEGER           NULL,      -- fuente venía como texto; aquí se normaliza a entero (opcional/NULL)
    MIN_AGE         INTEGER           NULL,
    AVERAGE_RATING  DECIMAL(3,2)      NULL,      -- esperado 0..10
    USERS_RATING    INTEGER           NULL,      -- cantidad de usuarios (conteo)
    MIN_PLAYERS     INTEGER           NULL,
    MAX_PLAYERS     INTEGER           NULL,

    ID_DESIGNER     INTEGER       NOT NULL,
    ID_PUBLISHER    INTEGER       NOT NULL,
    ID_CATEGORY     INTEGER       NOT NULL,

    CONSTRAINT PK_BOARD_GAMES PRIMARY KEY (ID_GAME),

    -- Reglas de consistencia
    CONSTRAINT CK_RATING_RANGE CHECK (
        AVERAGE_RATING IS NULL OR (AVERAGE_RATING >= 0 AND AVERAGE_RATING <= 10)
    ),
    CONSTRAINT CK_PLAYERS_RANGE CHECK (
        (MIN_PLAYERS IS NULL AND MAX_PLAYERS IS NULL)
        OR (MIN_PLAYERS IS NOT NULL AND MAX_PLAYERS IS NOT NULL AND MIN_PLAYERS > 0 AND MAX_PLAYERS >= MIN_PLAYERS)
    ),
    CONSTRAINT CK_MIN_AGE_NONNEG CHECK (
        MIN_AGE IS NULL OR MIN_AGE >= 0
    ),
    CONSTRAINT CK_YEAR_PUBLISHED CHECK (
        YEAR_PUBLISHED IS NULL OR (YEAR_PUBLISHED BETWEEN 1800 AND 2100)
    ),

    -- Foráneas
    CONSTRAINT FK_BG_DESIGNER  FOREIGN KEY (ID_DESIGNER)  REFERENCES DESIGNERS (ID_DESIGNER),
    CONSTRAINT FK_BG_PUBLISHER FOREIGN KEY (ID_PUBLISHER) REFERENCES PUBLISHERS (ID_PUBLISHER),
    CONSTRAINT FK_BG_CATEGORY  FOREIGN KEY (ID_CATEGORY)  REFERENCES CATEGORIES (ID_CATEGORY)
);

-- ---------------------------------------------------------------------
-- Índices sugeridos
-- ---------------------------------------------------------------------
CREATE INDEX IF NOT EXISTS IX_BG_NAME        ON BOARD_GAMES (NAME);
CREATE INDEX IF NOT EXISTS IX_BG_CATEGORY    ON BOARD_GAMES (ID_CATEGORY);
CREATE INDEX IF NOT EXISTS IX_BG_PUBLISHER   ON BOARD_GAMES (ID_PUBLISHER);
CREATE INDEX IF NOT EXISTS IX_BG_DESIGNER    ON BOARD_GAMES (ID_DESIGNER);
CREATE INDEX IF NOT EXISTS IX_BG_RATING      ON BOARD_GAMES (AVERAGE_RATING);
CREATE INDEX IF NOT EXISTS IX_BG_YEAR        ON BOARD_GAMES (YEAR_PUBLISHED);

-- =====================================================================
-- Fin del DDL
-- =====================================================================