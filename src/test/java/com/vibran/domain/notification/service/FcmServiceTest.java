package com.vibran.domain.notification.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.vibran.shared.enums.AlertSeverity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests for the FcmService.
 * Uses static mocking to intercept FirebaseMessaging instance calls.
 * Verifies that notifications are correctly constructed and handled.
 */
@ExtendWith(MockitoExtension.class)
class FcmServiceTest {

    @InjectMocks
    private FcmService fcmService;

    private FirebaseMessaging firebaseMessaging;

    @BeforeEach
    void setUp() {
        firebaseMessaging = mock(FirebaseMessaging.class);
    }

    @Test
    @DisplayName("Should return false when FCM token is null or blank")
    void shouldReturnFalseWhenTokenIsMissing() {
        assertFalse(fcmService.sendPush(null, "Title", "Body", AlertSeverity.HIGH, 1L, 1L));
        assertFalse(fcmService.sendPush("", "Title", "Body", AlertSeverity.HIGH, 1L, 1L));
        assertFalse(fcmService.sendPush("  ", "Title", "Body", AlertSeverity.HIGH, 1L, 1L));
    }

    @Test
    @DisplayName("Should return true when push notification is successfully sent")
    void shouldReturnTrueWhenPushIsSuccessful() throws FirebaseMessagingException {
        try (MockedStatic<FirebaseMessaging> mockedFirebase = mockStatic(FirebaseMessaging.class)) {
            mockedFirebase.when(FirebaseMessaging::getInstance).thenReturn(firebaseMessaging);
            when(firebaseMessaging.send(any(Message.class))).thenReturn("projects/test/messages/1234");

            boolean result = fcmService.sendPush(
                    "valid-token",
                    "Over speeding",
                    "Vehicle KCC 123 is at 120km/h",
                    AlertSeverity.CRITICAL,
                    50L,
                    10L
            );

            assertTrue(result);
            verify(firebaseMessaging, times(1)).send(any(Message.class));
        }
    }

    @Test
    @DisplayName("Should return false when FirebaseMessaging throws an exception")
    void shouldReturnFalseWhenFirebaseThrowsException() throws FirebaseMessagingException {
        try (MockedStatic<FirebaseMessaging> mockedFirebase = mockStatic(FirebaseMessaging.class)) {
            mockedFirebase.when(FirebaseMessaging::getInstance).thenReturn(firebaseMessaging);
            
            FirebaseMessagingException exception = mock(FirebaseMessagingException.class);
            when(exception.getMessage()).thenReturn("Invalid token");
            when(firebaseMessaging.send(any(Message.class))).thenThrow(exception);

            boolean result = fcmService.sendPush(
                    "invalid-token",
                    "Title",
                    "Body",
                    AlertSeverity.LOW,
                    null,
                    null
            );

            assertFalse(result);
        }
    }

    @Test
    @DisplayName("Should handle null severity and IDs gracefully during message building")
    void shouldHandleNullsGracefully() throws FirebaseMessagingException {
        try (MockedStatic<FirebaseMessaging> mockedFirebase = mockStatic(FirebaseMessaging.class)) {
            mockedFirebase.when(FirebaseMessaging::getInstance).thenReturn(firebaseMessaging);
            when(firebaseMessaging.send(any(Message.class))).thenReturn("ok");

            boolean result = fcmService.sendPush(
                    "token",
                    "Title",
                    "Body",
                    null,
                    null,
                    null
            );

            assertTrue(result);
        }
    }
}
