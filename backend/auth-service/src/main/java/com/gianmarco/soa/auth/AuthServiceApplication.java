package com.gianmarco.soa.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
@EnableScheduling
public class AuthServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
        System.out.println("========================================");
        System.out.println("🚖 TAXI SOA SYSTEM STARTED SUCCESSFULLY");
        System.out.println("========================================");
        System.out.println("📌 Available Services:");
        System.out.println("   - Auth: http://localhost:8080/api/auth");
        System.out.println("   - Driver: http://localhost:8080/api/drivers");
        System.out.println("   - Ride Request: http://localhost:8080/api/ride-requests");
        System.out.println("   - Ride Assignment: http://localhost:8080/api/assignments");
        System.out.println("   - Payment: http://localhost:8080/api/payments");
        System.out.println("   - Notification: http://localhost:8080/api/notifications");
        System.out.println("   - Audit: http://localhost:8080/api/audit");
        System.out.println("   - Orchestrator: http://localhost:8080/api/orchestrator");
        System.out.println("========================================");
        System.out.println("✅ 4 CRITERIA MATCHING ACTIVE:");
        System.out.println("   1. Availability | 2. Service Type (STANDARD/CONFORT)");
        System.out.println("   3. Proximity (Haversine) | 4. Best Rating");
        System.out.println("========================================");
    }
}