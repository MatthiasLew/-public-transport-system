package org.example;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ManagerDatabase {
    private static final Logger logger = LoggerFactory.getLogger(ManagerDatabase.class);
    private final Connection connection;

    public ManagerDatabase(Connection connection) {
        this.connection = connection;
    }

    public void createTables() {
        createLinesTable();
        createStopsTable();
        createDriversTable();
        createSchedulesTable();
        createDelaysTable();
    }
    public void populateTables() {
        insertDefaultLines();
        insertDefaultStops();
        insertDefaultDrivers();
        insertDefaultSchedules();
        insertDefaultDelays();
    }

    private void createLinesTable() {
        String createTableQuery = "CREATE TABLE IF NOT EXISTS `Lines` ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "line_number VARCHAR(10) NOT NULL, "
                + "route VARCHAR(255) NOT NULL)";
        executeUpdate(createTableQuery, "Tabela 'Lines'");
    }

    private void createStopsTable() {
        String createTableQuery = "CREATE TABLE IF NOT EXISTS `Stops` ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "name VARCHAR(100) NOT NULL, "
                + "location POINT NOT NULL)";
        executeUpdate(createTableQuery, "Tabela 'Stops'");
    }

    private void createDriversTable() {
        String createTableQuery = "CREATE TABLE IF NOT EXISTS `Drivers` ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "name VARCHAR(100) NOT NULL, "
                + "license_number VARCHAR(50) NOT NULL)";
        executeUpdate(createTableQuery, "Tabela 'Drivers'");
    }

    private void createSchedulesTable() {
        String createTableQuery = "CREATE TABLE IF NOT EXISTS `Schedules` ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "line_id INT, "
                + "stop_id INT, "
                + "departure_time TIME NOT NULL, "
                + "FOREIGN KEY (line_id) REFERENCES `Lines`(id), "
                + "FOREIGN KEY (stop_id) REFERENCES `Stops`(id))";
        executeUpdate(createTableQuery, "Tabela 'Schedules'");
    }

    private void createDelaysTable() {
        String createTableQuery = "CREATE TABLE IF NOT EXISTS `Delays` ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "schedule_id INT, "
                + "delay_time INT, "
                + "reported_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
                + "FOREIGN KEY (schedule_id) REFERENCES `Schedules`(id))";
        executeUpdate(createTableQuery, "Tabela 'Delays'");
    }
    private void insertDefaultLines() {
        String insertQuery = "INSERT INTO `Lines` (line_number, route) VALUES "
                + "('1', 'Route A'), "
                + "('2', 'Route B'), "
                + "('3', 'Route C')";
        executeUpdate(insertQuery, "Wpisy w tabeli 'Lines'");
    }

    private void insertDefaultStops() {
        String insertQuery = "INSERT INTO `Stops` (name, location) VALUES "
                + "('Stop A', ST_GeomFromText('POINT(1 1)')), "
                + "('Stop B', ST_GeomFromText('POINT(2 2)')), "
                + "('Stop C', ST_GeomFromText('POINT(3 3)'))";
        executeUpdate(insertQuery, "Wpisy w tabeli 'Stops'");
    }

    private void insertDefaultDrivers() {
        String insertQuery = "INSERT INTO `Drivers` (name, license_number) VALUES "
                + "('Alice', 'L12345'), "
                + "('Bob', 'L67890'), "
                + "('Charlie', 'L54321')";
        executeUpdate(insertQuery, "Wpisy w tabeli 'Drivers'");
    }

    private void insertDefaultSchedules() {
        String insertQuery = "INSERT INTO `Schedules` (line_id, stop_id, departure_time) VALUES "
                + "(1, 1, '08:00:00'), "
                + "(1, 2, '08:30:00'), "
                + "(2, 3, '09:00:00')";
        executeUpdate(insertQuery, "Wpisy w tabeli 'Schedules'");
    }

    private void insertDefaultDelays() {
        String insertQuery = "INSERT INTO `Delays` (schedule_id, delay_time) VALUES "
                + "(1, 5), "
                + "(2, 10), "
                + "(3, 15)";
        executeUpdate(insertQuery, "Wpisy w tabeli 'Delays'");
    }

    private void executeUpdate(String query, String tableName) {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(query);
            logger.info("{} została utworzona.", tableName);
        } catch (SQLException e) {
            logger.error("Wystąpił błąd podczas tworzenia tabeli '{}': {}", tableName, e.getMessage(), e);
        }
    }
}
