package org.example;

import java.sql.DriverManager;
import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.Connection;

public class ConnectDatabase
{
    private static final Logger logger = LoggerFactory.getLogger(ConnectDatabase.class);
    private static final String URL = "jdbc:mysql://localhost:3306";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    public static Connection getConnection()
    {
        try
        {
            Connection connection = DriverManager.getConnection(URL,USER,PASSWORD);
            logger.info("Połączono z MySQL");
            return connection;
        }catch (SQLException e)
        {
            logger.error("Błąd podczas łączenia z bazą danych",e);
            return null;
        }
    }
    public static void main(String[] args)
    {
        try(Connection connection =getConnection())
        {
            if(connection!=null)
            {
                logger.info("Testowe połączenie zakończone sukcesem.");
            }
            else
            {
                logger.warn("Nie udało sie nawiązać testowego połączenia.");
            }
        } catch (SQLException e)
            {
            logger.error("Błąd podczas zamykania z bazą danych.", e);
            }
    }
}