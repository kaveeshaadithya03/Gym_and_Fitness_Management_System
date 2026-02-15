package com.example.gym.controller;

import com.example.gym.util.DatabaseSetup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/database")
public class DatabaseController {

    @Autowired
    private DatabaseSetup databaseSetup;

    @PostMapping("/setup-members-table")
    public ResponseEntity<String> setupMembersTable() {
        try {
            databaseSetup.createMembersTable();
            return ResponseEntity.ok("✅ Members table created successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("❌ Error creating members table: " + e.getMessage());
        }
    }
}