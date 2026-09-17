package com.vibran.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;

@Configuration
@Slf4j
public class FirebaseConfig {

    @Value("${firebase.credentials-path}")
    private String credentialsPath;

    @PostConstruct
    public void initialize() {
        if (credentialsPath == null || credentialsPath.isEmpty()) {
            log.warn("Firebase credentials path not configured. FCM notifications will be disabled.");
            return;
        }

        try {
            // Only initialize once — avoids duplicate app error on hot reload
            if (FirebaseApp.getApps().isEmpty()) {
                ClassPathResource resource = new ClassPathResource(credentialsPath);
                if (!resource.exists()) {
                    log.warn("Firebase service account file not found at {}. FCM notifications will be disabled.", credentialsPath);
                    return;
                }

                InputStream serviceAccount = resource.getInputStream();

                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(
                                GoogleCredentials.fromStream(serviceAccount))
                        .build();

                FirebaseApp.initializeApp(options);
                log.info("Firebase Admin SDK initialized successfully");
            }
        } catch (IOException e) {
            log.error("Failed to initialize Firebase: {}", e.getMessage());
            // Don't throw — app still works, just no FCM push
            // Alerts still save to DB and push via WebSocket
        }
    }
}