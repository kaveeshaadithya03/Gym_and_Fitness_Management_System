import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class CreateMembersTable {
    public static void main(String[] args) {
        String url = "jdbc:mysql://127.0.0.1:3306/gym";
        String username = "root";
        String password = "Kaveesha@2026";
        
        String createTableSQL = """
            CREATE TABLE IF NOT EXISTS members (
                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                name VARCHAR(255) NOT NULL,
                age INT NOT NULL,
                contact_number VARCHAR(20) NOT NULL,
                email VARCHAR(255) NOT NULL UNIQUE,
                gender VARCHAR(10) NOT NULL,
                payment_method VARCHAR(20),
                transaction_id VARCHAR(100),
                registered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
            """;
        
        String createIndexSQL = "CREATE INDEX IF NOT EXISTS idx_members_email ON members(email)";
        
        try (Connection conn = DriverManager.getConnection(url, username, password);
             Statement stmt = conn.createStatement()) {
            
            // Create the members table
            stmt.execute(createTableSQL);
            System.out.println("✅ Members table created successfully!");
            
            // Create index for better performance
            stmt.execute(createIndexSQL);
            System.out.println("✅ Members table index created successfully!");
            
            // Verify table exists
            stmt.execute("DESCRIBE members");
            System.out.println("✅ Members table verified!");
            
        } catch (Exception e) {
            System.err.println("❌ Error creating members table: " + e.getMessage());
            e.printStackTrace();
        }
    }
}