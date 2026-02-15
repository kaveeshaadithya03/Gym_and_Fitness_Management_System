package com.example.gym.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class MemberChatbotService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Check if a member has completed the chatbot registration
     * by looking for their email in the members table
     */
    public boolean hasMemberCompletedChatbot(String email) {
        try {
            String sql = "SELECT COUNT(*) FROM members WHERE email = ?";
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, email.toLowerCase());
            return count != null && count > 0;
        } catch (Exception e) {
            // If members table doesn't exist or query fails, assume not completed
            return false;
        }
    }

    /**
     * Check if chatbot is currently in use by another user
     */
    public boolean isChatbotInUse() {
        try {
            // Check if there's an active chatbot session in the last 10 minutes
            String sql = "SELECT COUNT(*) FROM chatbot_sessions WHERE is_active = 1 AND last_activity > DATE_SUB(NOW(), INTERVAL 10 MINUTE)";
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
            return count != null && count > 0;
        } catch (Exception e) {
            // If table doesn't exist, chatbot is not in use
            return false;
        }
    }

    /**
     * Start a chatbot session for a user
     */
    public boolean startChatbotSession(String userEmail) {
        try {
            // First check if chatbot is already in use
            if (isChatbotInUse()) {
                return false;
            }

            // Clear any old inactive sessions
            String clearOldSql = "DELETE FROM chatbot_sessions WHERE last_activity < DATE_SUB(NOW(), INTERVAL 10 MINUTE)";
            jdbcTemplate.update(clearOldSql);

            // Start new session
            String insertSql = "INSERT INTO chatbot_sessions (user_email, is_active, last_activity) VALUES (?, 1, NOW()) ON DUPLICATE KEY UPDATE is_active = 1, last_activity = NOW()";
            jdbcTemplate.update(insertSql, userEmail.toLowerCase());
            return true;
        } catch (Exception e) {
            // If table doesn't exist, create it and try again
            createChatbotSessionsTable();
            try {
                String insertSql = "INSERT INTO chatbot_sessions (user_email, is_active, last_activity) VALUES (?, 1, NOW())";
                jdbcTemplate.update(insertSql, userEmail.toLowerCase());
                return true;
            } catch (Exception ex) {
                return false;
            }
        }
    }

    /**
     * End a chatbot session for a user
     */
    public void endChatbotSession(String userEmail) {
        try {
            String sql = "UPDATE chatbot_sessions SET is_active = 0 WHERE user_email = ?";
            jdbcTemplate.update(sql, userEmail.toLowerCase());
        } catch (Exception e) {
            // Ignore errors when ending session
        }
    }

    /**
     * Update chatbot session activity
     */
    public void updateChatbotActivity(String userEmail) {
        try {
            String sql = "UPDATE chatbot_sessions SET last_activity = NOW() WHERE user_email = ? AND is_active = 1";
            jdbcTemplate.update(sql, userEmail.toLowerCase());
        } catch (Exception e) {
            // Ignore errors when updating activity
        }
    }

    /**
     * Create chatbot_sessions table if it doesn't exist
     */
    private void createChatbotSessionsTable() {
        try {
            String createTableSQL = """
                CREATE TABLE IF NOT EXISTS chatbot_sessions (
                    user_email VARCHAR(255) PRIMARY KEY,
                    is_active BOOLEAN DEFAULT FALSE,
                    last_activity TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
                )
                """;
            jdbcTemplate.execute(createTableSQL);
        } catch (Exception e) {
            // Ignore table creation errors
        }
    }

    /**
     * Get member chatbot data by email
     */
    public MemberChatbotData getMemberChatbotData(String email) {
        try {
            String sql = "SELECT id, name, age, contact_number, email, gender, payment_method, transaction_id, registered_at FROM members WHERE email = ?";
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
                MemberChatbotData data = new MemberChatbotData();
                data.setId(rs.getLong("id"));
                data.setName(rs.getString("name"));
                data.setAge(rs.getInt("age"));
                data.setContactNumber(rs.getString("contact_number"));
                data.setEmail(rs.getString("email"));
                data.setGender(rs.getString("gender"));
                data.setPaymentMethod(rs.getString("payment_method"));
                data.setTransactionId(rs.getString("transaction_id"));
                data.setRegisteredAt(rs.getTimestamp("registered_at"));
                return data;
            }, email.toLowerCase());
        } catch (Exception e) {
            return null;
        }
    }

    public static class MemberChatbotData {
        private Long id;
        private String name;
        private Integer age;
        private String contactNumber;
        private String email;
        private String gender;
        private String paymentMethod;
        private String transactionId;
        private java.sql.Timestamp registeredAt;

        // Getters and setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public Integer getAge() { return age; }
        public void setAge(Integer age) { this.age = age; }
        
        public String getContactNumber() { return contactNumber; }
        public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getGender() { return gender; }
        public void setGender(String gender) { this.gender = gender; }
        
        public String getPaymentMethod() { return paymentMethod; }
        public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
        
        public String getTransactionId() { return transactionId; }
        public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
        
        public java.sql.Timestamp getRegisteredAt() { return registeredAt; }
        public void setRegisteredAt(java.sql.Timestamp registeredAt) { this.registeredAt = registeredAt; }
    }
}