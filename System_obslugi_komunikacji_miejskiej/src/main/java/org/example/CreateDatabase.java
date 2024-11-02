package org.example;
import java.sql.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.SQLException;
import java.sql.Statement;
public class CreateDatabase {
    private  static  final Logger logger =LoggerFactory.getLogger(CreateDatabase.class);
    private static final String Database_name="komunikacja_miejska";
    public static void main(String[] args)
    {
        Connection connection;
        try {
            connection = ConnectDatabase.getConnection();
            if (connection == null) {
                logger.error("Brak połaczenia z baza danych");
                return;
            }
            try (Statement statement = connection.createStatement()) {
                String createDatabaseQuerry = "CREATE DATABASE IF NOT EXISTS " + Database_name;
                statement.executeUpdate(createDatabaseQuerry);
                System.out.println("Baza danych " + Database_name + " została utworzona");
                statement.executeUpdate("USE " + Database_name);
            }
            ManagerDatabase managerdb = new ManagerDatabase(connection);
            managerdb.createTables();
            managerdb.populateTables();
        }
        catch (SQLException e)
        {
            // Obsługa wyjątku
            logger.error("Wystąpił błąd podczas tworzenia bazy danych {} " + Database_name, e.getMessage(),e);

        }
    }
}