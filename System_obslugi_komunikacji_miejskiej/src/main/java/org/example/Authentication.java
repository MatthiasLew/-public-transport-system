package org.example;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.sql.Connection;
public class Authentication {
    private final Map<String, String> userDatabase = new HashMap<>(); // Stores username and password
    private final Map<String, Runnable> accountFunctions = new HashMap<>(); // Stores account-specific functions
    private final Connection connection;
    public Authentication(Connection connection) {
        this.connection = connection;
        // Adding users with their passwords
        userDatabase.put("admin", "admin123");
        userDatabase.put("user", "user123");
        userDatabase.put("driver", "driver123");

        // Defining account-specific functions
        accountFunctions.put("admin", this::adminFunction);
        accountFunctions.put("user", this::userFunction);
        accountFunctions.put("driver", this::driverFunction);
    }

    public void login() {
        Scanner scanner = new Scanner(System.in);
        boolean authenticated = false;
        while (!authenticated) {
            System.out.print("Wprowadz login: ");
            String username = scanner.nextLine();

            System.out.print("Wprowadz hasło: ");
            String password = scanner.nextLine();

            if (authenticate(username, password)) {
                authenticated = true;
                System.out.println("Logowanie udane!");
                accountFunctions.get(username).run(); // Execute specific function for account
            } else {
                System.out.println("Nie własciwy login lub hasło. Sprobuj ponownie");
            }
        }
    }

    private boolean authenticate(String username, String password) {
        return userDatabase.containsKey(username) && userDatabase.get(username).equals(password);
    }

    private void adminFunction() {
        System.out.println("Witaj administratorze. Miłej pracy");
        AdminFunction adminFunction = new AdminFunction(connection); // Create an instance of AdminFunction
        adminFunction.editDatabase();
    }

    private void userFunction() {
        System.out.println("Witaj uzytkowniku, mozesz sprawedzic rozklad jazdy i ich opoznienia");
        UserFunction userFunction = new UserFunction(connection);
        Scanner scanner = new Scanner(System.in); // Initialize scanner for user input
        System.out.print("Wprowadz linie, ktora chcesz sprawdzic: ");
        String numer_line = scanner.nextLine();

        try {
            int lineNumber = Integer.parseInt(numer_line); // Convert the input to an integer
            userFunction.displayLineStatus(lineNumber); // Call the method with the integer
        } catch (NumberFormatException e) {
            System.out.println("Wprowadzono niepoprawny numer linii. Proszę spróbować ponownie.");
        }
    }
    private void driverFunction() {
        System.out.println("Witaj kierowco, mozesz tu wpisac opoznienia jesli takie wynikną");

        try (Scanner scanner = new Scanner(System.in)) {
            DriverFunction driverFunction = new DriverFunction(connection);
            // Zapytaj o ID harmonogramu (schedule) i nowe opóźnienie
            System.out.print("Podaj ID harmonogramu (schedule), dla ktorego chcesz ustawic opoznienie: ");
            int scheduleId = scanner.nextInt(); // Odczytanie ID harmonogramu

            System.out.print("Podaj nowe opóźnienie (w minutach): ");
            int newDelayTime = scanner.nextInt(); // Odczytanie nowego opóźnienia

            // Aktualizacja opóźnienia
            driverFunction.updateDelay(scheduleId, newDelayTime);
        } catch (Exception e) {
            System.err.println("Wystąpił błąd: " + e.getMessage());
        }
        // Zamknij skaner
    }

    public static void main(String[] args) {
        Connection connection = ConnectDatabase.getConnection();
        Authentication loginManager = new Authentication(connection);
        loginManager.login();
    }
}