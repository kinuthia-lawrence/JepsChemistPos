package com.larrykin.jepschemistpos.UTILITIES;


import com.larrykin.jepschemistpos.MODELS.UtilsData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class DatabaseConn {
    private static final Logger log = LoggerFactory.getLogger(DatabaseConn.class);

    public Connection getConnection() {
        Connection connection = null;
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:jelpschemist.db");
            createSchemaIfNotExist(connection);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return connection;
    }

    private void createSchemaIfNotExist(Connection connection) {
        try {
            connection.createStatement().execute(
                    "CREATE TABLE IF NOT EXISTS categories (\n" +
                            "    id                   INTEGER,\n" +
                            "    category_name        TEXT    NOT NULL,\n" +
                            "    category_description TEXT,\n" +
                            "    PRIMARY KEY (\n" +
                            "        id AUTOINCREMENT\n" +
                            "    )\n" +
                            ");"
            );
            connection.createStatement().execute(
                    "CREATE TABLE IF NOT EXISTS expired_goods (\n" +
                            "    id            INTEGER NOT NULL,\n" +
                            "    name          TEXT    NOT NULL,\n" +
                            "    category      TEXT,\n" +
                            "    quantity      INTEGER,\n" +
                            "    buying_price  REAL,\n" +
                            "    supplier_name TEXT,\n" +
                            "    date_added    TEXT,\n" +
                            "    expiry_date   TEXT\n" +
                            ");"
            );
            connection.createStatement().execute(
                    "CREATE TABLE IF NOT EXISTS money (\n" +
                            "    id               INTEGER,\n" +
                            "    date             TEXT,\n" +
                            "    cash_withdrawal  REAL,\n" +
                            "    mpesa_withdrawal REAL,\n" +
                            "    description      TEXT,\n" +
                            "    PRIMARY KEY (\n" +
                            "        id AUTOINCREMENT\n" +
                            "    )\n" +
                            ");"
            );
            connection.createStatement().execute(
                    "CREATE TABLE IF NOT EXISTS products (\n" +
                            "    id            INTEGER,\n" +
                            "    name          TEXT    NOT NULL,\n" +
                            "    category      TEXT,\n" +
                            "    quantity      INTEGER,\n" +
                            "    min_quantity  INTEGER,\n" +
                            "    buying_price  REAL,\n" +
                            "    selling_price TEXT,\n" +
                            "    supplier_name TEXT,\n" +
                            "    date_added    TEXT,\n" +
                            "    expiry_date   TEXT,\n" +
                            "    description   INTEGER,\n" +
                            "    PRIMARY KEY (\n" +
                            "        id AUTOINCREMENT\n" +
                            "    )\n" +
                            ");"
            );
            connection.createStatement().execute(
                    "CREATE TABLE IF NOT EXISTS sales (\n" +
                            "    id             INTEGER,\n" +
                            "    date           TEXT,\n" +
                            "    goods          TEXT,\n" +
                            "    expected_total REAL,\n" +
                            "    total_amount   REAL,\n" +
                            "    discount       REAL,\n" +
                            "    cash           REAL,\n" +
                            "    mpesa          REAL,\n" +
                            "    credit         REAL,\n" +
                            "    description    TEXT,\n" +
                            "    PRIMARY KEY (\n" +
                            "        id AUTOINCREMENT\n" +
                            "    )\n" +
                            ");"
            );
            connection.createStatement().execute(
                    "CREATE TABLE IF NOT EXISTS service_history (\n" +
                            "    id            INTEGER,\n" +
                            "    date          TEXT,\n" +
                            "    service_name  TEXT    NOT NULL,\n" +
                            "    description   TEXT    NOT NULL,\n" +
                            "    cash_payment  REAL,\n" +
                            "    mpesa_payment REAL,\n" +
                            "    PRIMARY KEY (\n" +
                            "        id AUTOINCREMENT\n" +
                            "    )\n" +
                            ");"
            );
            connection.createStatement().execute(
                    "CREATE TABLE IF NOT EXISTS suppliers (\n" +
                            "    id                  INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                            "    name                TEXT    NOT NULL,\n" +
                            "    contact_information TEXT\n" +
                            ");"
            );
            connection.createStatement().execute(
                    "CREATE TABLE IF NOT EXISTS users (\n" +
                            "    id       INTEGER,\n" +
                            "    username TEXT    NOT NULL\n" +
                            "                     UNIQUE,\n" +
                            "    email    TEXT    NOT NULL\n" +
                            "                     UNIQUE,\n" +
                            "    password TEXT    NOT NULL,\n" +
                            "    PRIMARY KEY (\n" +
                            "        id AUTOINCREMENT\n" +
                            "    )\n" +
                            ");"
            );
            connection.createStatement().execute(
                    "CREATE TABLE IF NOT EXISTS utils (\n" +
                            "    id                         INTEGER,\n" +
                            "    dark_theme                 BOOLEAN,\n" +
                            "    current_stock_value        REAL,\n" +
                            "    total_value_of_added_stock REAL,\n" +
                            "    total_cash_from_sales      REAL,\n" +
                            "    total_mpesa_from_sales     REAL,\n" +
                            "    current_cash               REAL,\n" +
                            "    current_mpesa              REAL,\n" +
                            "    services_number            INTEGER,\n" +
                            "    services_revenue           REAL,\n" +
                            "    services_total_cash        REAL,\n" +
                            "    services_total_mpesa       REAL,\n" +
                            "    expired_loss               REAL,\n" +
                            "    refunded_expired           REAL,\n" +
                            "    PRIMARY KEY (\n" +
                            "        id AUTOINCREMENT\n" +
                            "    )\n" +
                            ");"
            );

            // Check if a row with id = 1 already exists
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM utils WHERE id = 1");
            rs.next();
            int count = rs.getInt(1);

            if (count == 0) {
                // Insert only if the row does not exist
                stmt.execute(
                        "INSERT INTO utils (id, dark_theme, current_cash, current_mpesa, current_stock_value, " +
                                "services_number, total_cash_from_sales, services_revenue, total_value_of_added_stock, " +
                                "total_mpesa_from_sales, services_total_cash, services_total_mpesa, expired_loss, refunded_expired) " +
                                "VALUES (1, false, 0.0, 0.0, 0.0, 0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0);"
                );
            }

        } catch (SQLException e) {
            log.error("Error creating schemas:: ", e);
            throw new RuntimeException(e);
        }
    }
}