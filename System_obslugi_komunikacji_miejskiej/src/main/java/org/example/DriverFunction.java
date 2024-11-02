package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class DriverFunction {
    private final Connection connection;
    public DriverFunction(Connection connection) {
        this.connection = connection;
        selectDatabase();
    }
    private void selectDatabase() {
        try (Statement statement = connection.createStatement()) {
            statement.execute("USE komunikacja_miejska");
        } catch (SQLException e) {
            System.err.println("Błąd podczas wyboru bazy danych: " + e.getMessage());
        }
    }

    // Method to update the delay time for a specific schedule
    public void updateDelay(int scheduleId, int newDelayTime) {
        String updateQuery = "UPDATE Delays SET delay_time = ? WHERE schedule_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(updateQuery)) {
            statement.setInt(1, newDelayTime);
            statement.setInt(2, scheduleId);
            int rowsUpdated = statement.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("Opóźnienie zaktualizowane pomyślnie.");
            } else {
                System.out.println("Nie znaleziono harmonogramu o podanym ID.");
            }
        } catch (SQLException e) {
            System.err.println("Błąd podczas aktualizacji opóźnienia: " + e.getMessage());
        }
    }
}
