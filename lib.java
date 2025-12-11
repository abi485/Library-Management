import java.sql.*;
import java.util.Scanner;

public class lib {

    public static void main(String[] args) {
        try {
            // Load the MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish the connection to the database
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/library", "root", "12345");
            Scanner scanner = new Scanner(System.in);

            while (true) {
                System.out.println("\n1. Insert Book");
                System.out.println("2. Update Book");
                System.out.println("3. Delete Book");
                System.out.println("4. Display Books");
                System.out.println("5. Exit");
                System.out.print("Choose an option: ");
                int choice = scanner.nextInt();
                scanner.nextLine(); // consume newline

                switch (choice) {
                    case 1 -> insertBook(conn, scanner);
                    case 2 -> updateBook(conn, scanner);
                    case 3 -> deleteBook(conn, scanner);
                    case 4 -> displayBooks(conn, scanner);
                    case 5 -> {
                        System.out.println("Exiting...");
                        return;
                    }
                    default -> System.out.println("Invalid option.");
                }
            }

        } catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC Driver not found. Ensure the driver is in the classpath.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Database error occurred.");
            e.printStackTrace();
        }
    }

    private static void insertBook(Connection conn, Scanner scanner) throws SQLException {
        System.out.print("Enter title: ");
        String title = scanner.nextLine();
        System.out.print("Enter author: ");
        String author = scanner.nextLine();
        System.out.print("Enter year: ");
        int year = scanner.nextInt();

        String sql = "INSERT INTO books (title, author, year) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, title);
            stmt.setString(2, author);
            stmt.setInt(3, year);
            stmt.executeUpdate();
            System.out.println("Book inserted successfully.");
        }
    }

    private static void updateBook(Connection conn, Scanner scanner) throws SQLException {
        System.out.print("Enter book ID to update: ");
        int id = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Enter new title: ");
        String title = scanner.nextLine();
        System.out.print("Enter new author: ");
        String author = scanner.nextLine();
        System.out.print("Enter new year: ");
        int year = scanner.nextInt();

        String sql = "UPDATE books SET title = ?, author = ?, year = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, title);
            stmt.setString(2, author);
            stmt.setInt(3, year);
            stmt.setInt(4, id);
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Book updated successfully.");
            } else {
                System.out.println("Book not found.");
            }
        }
    }

    private static void deleteBook(Connection conn, Scanner scanner) throws SQLException {
        System.out.print("Enter book ID to delete: ");
        int id = scanner.nextInt();

        String sql = "DELETE FROM books WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Book deleted successfully.");
            } else {
                System.out.println("Book not found.");
            }
        }
    }

    private static void displayBooks(Connection conn, Scanner scanner) throws SQLException {
        System.out.print("Enter field to search by (title/author/year): ");
        String field = scanner.nextLine().toLowerCase();

        // Validate the field
        if (!field.equals("title") && !field.equals("author") && !field.equals("year")) {
            System.out.println("Invalid field. Please choose 'title', 'author', or 'year'.");
            return;
        }

        System.out.print("Enter the value to search for: ");
        String value = scanner.nextLine();

        String sql = "SELECT * FROM books WHERE " + field + " LIKE ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (field.equals("year")) {
                // If field is "year", match the exact year (no LIKE)
                sql = "SELECT * FROM books WHERE year = ?";
                stmt.setInt(1, Integer.parseInt(value));
            } else {
                // If field is "title" or "author", use LIKE
                stmt.setString(1, "%" + value + "%");
            }

            try (ResultSet rs = stmt.executeQuery()) {
                boolean found = false;
                while (rs.next()) {
                    System.out.println("ID: " + rs.getInt("id"));
                    System.out.println("Title: " + rs.getString("title"));
                    System.out.println("Author: " + rs.getString("author"));
                    System.out.println("Year: " + rs.getInt("year"));
                    System.out.println("---------------------------");
                    found = true;
                }
                if (!found) {
                    System.out.println("No books found matching the criteria.");
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input for year. Please enter a valid integer.");
        }
    }
}
