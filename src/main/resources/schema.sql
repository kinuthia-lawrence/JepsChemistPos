--
-- File generated with SQLiteStudio v3.4.4 on Sun Dec 15 20:49:24 2024
--
-- Text encoding used: System
--
PRAGMA foreign_keys = off;
BEGIN TRANSACTION;

-- Table: categories
CREATE TABLE IF NOT EXISTS categories (
    id                   INTEGER,
    category_name        TEXT    NOT NULL,
    category_description TEXT,
    PRIMARY KEY (
        id AUTOINCREMENT
    )
);


-- Table: expired_goods
CREATE TABLE IF NOT EXISTS expired_goods (
    id            INTEGER NOT NULL,
    name          TEXT    NOT NULL,
    category      TEXT,
    quantity      INTEGER,
    buying_price  REAL,
    supplier_name TEXT,
    date_added    TEXT,
    expiry_date   TEXT
);


-- Table: money
CREATE TABLE IF NOT EXISTS money (
    id               INTEGER,
    date             TEXT,
    cash_withdrawal  REAL,
    mpesa_withdrawal REAL,
    description      TEXT,
    PRIMARY KEY (
        id AUTOINCREMENT
    )
);


-- Table: products
CREATE TABLE IF NOT EXISTS products (
    id            INTEGER,
    name          TEXT    NOT NULL,
    category      TEXT,
    quantity      INTEGER,
    min_quantity  INTEGER,
    buying_price  REAL,
    selling_price TEXT,
    supplier_name TEXT,
    date_added    TEXT,
    expiry_date   TEXT,
    description   INTEGER,
    PRIMARY KEY (
        id AUTOINCREMENT
    )
);


-- Table: sales
CREATE TABLE IF NOT EXISTS sales (
    id             INTEGER,
    date           TEXT,
    goods          TEXT,
    expected_total REAL,
    total_amount   REAL,
    discount       REAL,
    cash           REAL,
    mpesa          REAL,
    credit         REAL,
    description    TEXT,
    PRIMARY KEY (
        id AUTOINCREMENT
    )
);


-- Table: service_history
CREATE TABLE IF NOT EXISTS service_history (
    id            INTEGER,
    date          TEXT,
    service_name  TEXT    NOT NULL,
    description   TEXT    NOT NULL,
    cash_payment  REAL,
    mpesa_payment REAL,
    PRIMARY KEY (
        id AUTOINCREMENT
    )
);


-- Table: suppliers
CREATE TABLE IF NOT EXISTS suppliers (
    id                  INTEGER PRIMARY KEY AUTOINCREMENT,
    name                TEXT    NOT NULL,
    contact_information TEXT
);


-- Table: users
CREATE TABLE IF NOT EXISTS users (
    id       INTEGER,
    username TEXT    NOT NULL
                     UNIQUE,
    email    TEXT    NOT NULL
                     UNIQUE,
    password TEXT    NOT NULL,
    PRIMARY KEY (
        id AUTOINCREMENT
    )
);


-- Table: utils
CREATE TABLE IF NOT EXISTS utils (
    id                         INTEGER,
    dark_theme                 BOOLEAN,
    current_stock_value        REAL,
    total_value_of_added_stock REAL,
    total_cash_from_sales      REAL,
    total_mpesa_from_sales     REAL,
    current_cash               REAL,
    current_mpesa              REAL,
    services_number            INTEGER,
    services_revenue           REAL,
    services_total_cash        REAL,
    services_total_mpesa       REAL,
    expired_loss               REAL,
    refunded_expired           REAL,
    PRIMARY KEY (
        id AUTOINCREMENT
    )
);


COMMIT TRANSACTION;
PRAGMA foreign_keys = on;
