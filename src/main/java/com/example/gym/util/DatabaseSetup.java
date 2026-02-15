package com.example.gym.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseSetup {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void createMembersTable() {
        try {
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
            
            jdbcTemplate.execute(createTableSQL);
            System.out.println("✅ Members table created successfully!");
            
            // Create index for better performance
            String createIndexSQL = "CREATE INDEX IF NOT EXISTS idx_members_email ON members(email)";
            jdbcTemplate.execute(createIndexSQL);
            System.out.println("✅ Members table index created successfully!");
            
        } catch (Exception e) {
            System.err.println("❌ Error creating members table: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to create members table", e);
        }
    }
}