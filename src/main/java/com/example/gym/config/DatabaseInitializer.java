package com.example.gym.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseInitializer implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        try {
            // Create members table if it doesn't exist
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
            System.out.println("✅ Members table created successfully on startup!");
            
            // Create index for better performance
            String createIndexSQL = "CREATE INDEX IF NOT EXISTS idx_members_email ON members(email)";
            jdbcTemplate.execute(createIndexSQL);
            System.out.println("✅ Members table index created successfully on startup!");

            // Create chatbot_sessions table for one-user-at-a-time functionality
            String createChatbotSessionsSQL = """
                CREATE TABLE IF NOT EXISTS chatbot_sessions (
                    user_email VARCHAR(255) PRIMARY KEY,
                    is_active BOOLEAN DEFAULT FALSE,
                    last_activity TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
                )
                """;
            
            jdbcTemplate.execute(createChatbotSessionsSQL);
            System.out.println("✅ Chatbot sessions table created successfully on startup!");
            
        } catch (Exception e) {
            System.err.println("❌ Error creating tables on startup: " + e.getMessage());
            // Don't throw exception to prevent application startup failure
        }
    }
}