package org.example;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public record UserFunction(Connection connection) {
    private static final Logger logger = LoggerFactory.getLogger(UserFunction.class);

    public UserFunction(Connection connection) {
        this.connection = connection;
        selectDatabase(); // Ensure the correct database is selected
    }

    // Ensure the correct database is selected
    private void selectDatabase() {
        try (Statement statement = connection.createStatement()) {
            statement.execute("USE komunikacja_miejska");
        } catch (SQLException e) {
            logger.error("Błąd podczas wyboru bazy danych: {}", e.getMessage());
        }
    }public void displayLineStatus(int lineId) {
        String query = "SELECT S.line_id, St.name, S.departure_time, D.delay_time " +
                "FROM Schedules S " +
                "JOIN Stops St ON S.stop_id = St.id " +
                "LEFT JOIN Delays D ON S.id = D.schedule_id " +
                "WHERE S.line_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, lineId);
            ResultSet resultSet = statement.executeQuery();

            // Use a Set to track printed combinations
            Set<String> printedLines = new HashSet<>();

            while (resultSet.next()) {
                int lineIdFromDB = resultSet.getInt("line_id");
                String stopName = resultSet.getString("name");
                Time departureTime = resultSet.getTime("departure_time");
                Integer delayTime = resultSet.getObject("delay_time", Integer.class); // Using getObject for nullable

                // Create a unique key for the line and stop
                String uniqueKey = lineIdFromDB + ":" + stopName + ":" + departureTime;

                // Check if this combination has already been printed
                if (!printedLines.contains(uniqueKey)) {
                    printedLines.add(uniqueKey);
                    System.out.printf("Linia: %d, Przystanek: %s, Godzina odjazdu: %s, Opóźnienie: %d min\n",
                            lineIdFromDB, stopName, departureTime, (delayTime != null ? delayTime : 0));
                }
            }
        } catch (SQLException e) {
            logger.error("Bład - skontaktuj sie z administratorem. {}: {}", lineId, e.getMessage());
        }
    }
}
