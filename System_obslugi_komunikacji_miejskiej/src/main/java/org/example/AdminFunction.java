package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

public class AdminFunction {
    private final Connection connection;
    private final Scanner scanner;

    public AdminFunction(Connection connection) {
        this.connection = connection;
        this.scanner = new Scanner(System.in);
    }

    public void editDatabase() {
        while (true) {
            System.out.println("Wybierz opcję:\n1. Dodaj domyslna baze\n2. Dodaj nowy wpis\n3. Edytuj istniejący wpis\n4. Usuń wpis\n5. Zakończ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // consume the newline

            switch (choice) {
                case 1:
                    CreateDatabase.main(new String[]{});
                    break;
                case 2:
                    addEntry();
                    break;
                case 3:
                    updateEntry();
                    break;
                case 4:
                    deleteEntry();
                    break;
                case 5:
                    try {
                        connection.close();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println("Zakończono.");
                    return;
                default:
                    System.out.println("Niepoprawny wybór, spróbuj ponownie.");
            }
        }
    }

    private void addEntry() {
        System.out.println("Wybierz tabelę do dodania wpisu:\n1. Lines\n2. Stops\n3. Drivers\n4. Schedules\n5. Delays");
        int tableChoice = scanner.nextInt();
        scanner.nextLine(); // consume the newline

        try {
            switch (tableChoice) {
                case 1:
                    System.out.print("Podaj numer linii: ");
                    String lineNumber = scanner.nextLine();
                    System.out.print("Podaj trasę: ");
                    String route = scanner.nextLine();
                    addLine(lineNumber, route);
                    break;
                case 2:
                    System.out.print("Podaj nazwę przystanku: ");
                    String stopName = scanner.nextLine();
                    System.out.print("Podaj lokalizację (x y): ");
                    String location = scanner.nextLine();
                    addStop(stopName, location);
                    break;
                case 3:
                    System.out.print("Podaj nazwisko kierowcy: ");
                    String driverName = scanner.nextLine();
                    System.out.print("Podaj numer licencji: ");
                    String licenseNumber = scanner.nextLine();
                    addDriver(driverName, licenseNumber);
                    break;
                case 4:
                    System.out.print("Podaj ID linii: ");
                    int lineId = scanner.nextInt();
                    System.out.print("Podaj ID przystanku: ");
                    int stopId = scanner.nextInt();
                    System.out.print("Podaj czas odjazdu (HH:mm:ss): ");
                    String departureTime = scanner.next();
                    addSchedule(lineId, stopId, departureTime);
                    break;
                case 5:
                    System.out.print("Podaj ID rozkładu: ");
                    int scheduleId = scanner.nextInt();
                    System.out.print("Podaj czas opóźnienia: ");
                    int delayTime = scanner.nextInt();
                    addDelay(scheduleId, delayTime);
                    break;
                default:
                    System.out.println("Niepoprawny wybór.");
            }
        } catch (SQLException e) {
            System.out.println("Błąd przy dodawaniu wpisu: " + e.getMessage());
        }
    }

    private void addLine(String lineNumber, String route) throws SQLException {
        String query = "INSERT INTO `Lines` (line_number, route) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, lineNumber);
            statement.setString(2, route);
            statement.executeUpdate();
            System.out.println("Dodano nową linię.");
        }
    }

    private void addStop(String stopName, String location) throws SQLException {
        String query = "INSERT INTO `Stops` (name, location) VALUES (?, ST_GeomFromText(?))";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, stopName);
            statement.setString(2, "POINT(" + location + ")");
            statement.executeUpdate();
            System.out.println("Dodano nowy przystanek.");
        }
    }

    private void addDriver(String driverName, String licenseNumber) throws SQLException {
        String query = "INSERT INTO `Drivers` (name, license_number) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, driverName);
            statement.setString(2, licenseNumber);
            statement.executeUpdate();
            System.out.println("Dodano nowego kierowcę.");
        }
    }

    private void addSchedule(int lineId, int stopId, String departureTime) throws SQLException {
        String query = "INSERT INTO `Schedules` (line_id, stop_id, departure_time) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, lineId);
            statement.setInt(2, stopId);
            statement.setString(3, departureTime);
            statement.executeUpdate();
            System.out.println("Dodano nowy rozkład.");
        }
    }

    private void addDelay(int scheduleId, int delayTime) throws SQLException {
        String query = "INSERT INTO `Delays` (schedule_id, delay_time) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, scheduleId);
            statement.setInt(2, delayTime);
            statement.executeUpdate();
            System.out.println("Dodano nowe opóźnienie.");
        }
    }

    private void updateEntry() {
        System.out.println("Wybierz tabelę do edycji wpisu:\n1. Lines\n2. Stops\n3. Drivers\n4. Schedules\n5. Delays");
        int tableChoice = scanner.nextInt();
        scanner.nextLine(); // consume the newline

        try {
            switch (tableChoice) {
                case 1:
                    System.out.print("Podaj ID linii do edytowania: ");
                    int lineId = scanner.nextInt();
                    scanner.nextLine(); // consume the newline
                    System.out.print("Podaj nowy numer linii: ");
                    String newLineNumber = scanner.nextLine();
                    System.out.print("Podaj nową trasę: ");
                    String newRoute = scanner.nextLine();
                    updateLine(lineId, newLineNumber, newRoute);
                    break;
                case 2:
                    System.out.print("Podaj ID przystanku do edytowania: ");
                    int stopId = scanner.nextInt();
                    scanner.nextLine(); // consume the newline
                    System.out.print("Podaj nową nazwę przystanku: ");
                    String newStopName = scanner.nextLine();
                    updateStop(stopId, newStopName);
                    break;
                case 3:
                    System.out.print("Podaj ID kierowcy do edytowania: ");
                    int driverId = scanner.nextInt();
                    scanner.nextLine(); // consume the newline
                    System.out.print("Podaj nowe nazwisko kierowcy: ");
                    String newDriverName = scanner.nextLine();
                    updateDriver(driverId, newDriverName);
                    break;
                case 4:
                    System.out.print("Podaj ID rozkładu do edytowania: ");
                    int scheduleId = scanner.nextInt();
                    scanner.nextLine(); // consume the newline
                    System.out.print("Podaj nowy czas odjazdu (HH:mm:ss): ");
                    String newDepartureTime = scanner.nextLine();
                    updateSchedule(scheduleId, newDepartureTime);
                    break;
                case 5:
                    System.out.print("Podaj ID opóźnienia do edytowania: ");
                    int delayId = scanner.nextInt();
                    System.out.print("Podaj nowy czas opóźnienia: ");
                    int newDelayTime = scanner.nextInt();
                    updateDelay(delayId, newDelayTime);
                    break;
                default:
                    System.out.println("Niepoprawny wybór.");
            }
        } catch (SQLException e) {
            System.out.println("Błąd przy edytowaniu wpisu: " + e.getMessage());
        }
    }

    // Add similar methods for updating each table...

    private void updateLine(int id, String lineNumber, String route) throws SQLException {
        String query = "UPDATE `Lines` SET line_number = ?, route = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, lineNumber);
            statement.setString(2, route);
            statement.setInt(3, id);
            statement.executeUpdate();
            System.out.println("Zaktualizowano linię.");
        }
    }

    private void updateStop(int id, String stopName) throws SQLException {
        String query = "UPDATE `Stops` SET name = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, stopName);
            statement.setInt(2, id);
            statement.executeUpdate();
            System.out.println("Zaktualizowano przystanek.");
        }
    }

    private void updateDriver(int id, String name) throws SQLException {
        String query = "UPDATE `Drivers` SET name = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, name);
            statement.setInt(2, id);
            statement.executeUpdate();
            System.out.println("Zaktualizowano kierowcę.");
        }
    }

    private void updateSchedule(int id, String departureTime) throws SQLException {
        String query = "UPDATE `Schedules` SET departure_time = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, departureTime);
            statement.setInt(2, id);
            statement.executeUpdate();
            System.out.println("Zaktualizowano rozkład.");
        }
    }

    private void updateDelay(int id, int delayTime) throws SQLException {
        String query = "UPDATE `Delays` SET delay_time = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, delayTime);
            statement.setInt(2, id);
            statement.executeUpdate();
            System.out.println("Zaktualizowano opóźnienie.");
        }
    }

    private void deleteEntry() {
        System.out.println("Wybierz tabelę do usunięcia wpisu:\n1. Lines\n2. Stops\n3. Drivers\n4. Schedules\n5. Delays");
        int tableChoice = scanner.nextInt();
        scanner.nextLine(); // consume the newline

        try {
            switch (tableChoice) {
                case 1:
                    System.out.print("Podaj ID linii do usunięcia: ");
                    int lineId = scanner.nextInt();
                    deleteLine(lineId);
                    break;
                case 2:
                    System.out.print("Podaj ID przystanku do usunięcia: ");
                    int stopId = scanner.nextInt();
                    deleteStop(stopId);
                    break;
                case 3:
                    System.out.print("Podaj ID kierowcy do usunięcia: ");
                    int driverId = scanner.nextInt();
                    deleteDriver(driverId);
                    break;
                case 4:
                    System.out.print("Podaj ID rozkładu do usunięcia: ");
                    int scheduleId = scanner.nextInt();
                    deleteSchedule(scheduleId);
                    break;
                case 5:
                    System.out.print("Podaj ID opóźnienia do usunięcia: ");
                    int delayId = scanner.nextInt();
                    deleteDelay(delayId);
                    break;
                default:
                    System.out.println("Niepoprawny wybór.");
            }
        } catch (SQLException e) {
            System.out.println("Błąd przy usuwaniu wpisu: " + e.getMessage());
        }
    }

    private void deleteLine(int id) throws SQLException {
        String query = "DELETE FROM `Lines` WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            statement.executeUpdate();
            System.out.println("Usunięto linię.");
        }
    }

    private void deleteStop(int id) throws SQLException {
        String query = "DELETE FROM `Stops` WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            statement.executeUpdate();
            System.out.println("Usunięto przystanek.");
        }
    }

    private void deleteDriver(int id) throws SQLException {
        String query = "DELETE FROM `Drivers` WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            statement.executeUpdate();
            System.out.println("Usunięto kierowcę.");
        }
    }

    private void deleteSchedule(int id) throws SQLException {
        String query = "DELETE FROM `Schedules` WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            statement.executeUpdate();
            System.out.println("Usunięto rozkład.");
        }
    }

    private void deleteDelay(int id) throws SQLException {
        String query = "DELETE FROM `Delays` WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            statement.executeUpdate();
            System.out.println("Usunięto opóźnienie.");
        }
    }
}
